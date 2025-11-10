package com.asynctide.turnbridge.m0.web;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.StoredObject;
import com.asynctide.turnbridge.domain.UploadJob;
import com.asynctide.turnbridge.domain.enumeration.StoragePurpose;
import com.asynctide.turnbridge.domain.enumeration.UploadJobStatus;
import com.asynctide.turnbridge.repository.StoredObjectRepository;
import com.asynctide.turnbridge.repository.UploadJobRepository;
import com.asynctide.turnbridge.testsupport.TestFiles;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 【E2E】從上傳到下載回饋的端到端流程（以測試模擬背景產製回饋）。
 *
 * 流程：
 * 1) POST /invoices/upload -> 取得 jobId
 * 2) 於測試中建立回饋檔（CSV）+ StoredObject，並把該 job 狀態設為 RESULT_READY
 * 3) GET /upload-jobs/{jobId}/result -> 下載 CSV 並驗證內容
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "turnbridge.storage.type=local",
    "turnbridge.storage.local.base-dir=${java.io.tmpdir}/turnbridge-it-storage"
})
class EndToEndUploadToResultIT {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Autowired UploadJobRepository jobRepo;
    @Autowired StoredObjectRepository soRepo;

    private Path realFile;

    @AfterEach
    void tearDown() throws Exception {
        try { if (realFile != null) Files.deleteIfExists(realFile); } catch (Exception ignored) {}
        soRepo.deleteAllInBatch();
        jobRepo.deleteAllInBatch();
    }

    @Test
    @DisplayName("上傳 → 模擬背景產製回饋 → 下載 CSV 成功")
    void e2e_upload_then_download_result() throws Exception {
        // Step 1: 上傳 CSV
        String csv = "invoiceNo,buyerId,amount\nAB00000001,12345678,100\n";
        MockMultipartFile file = new MockMultipartFile(
            "file", "sample.csv", "text/csv", new ByteArrayResource(csv.getBytes()).getByteArray()
        );

        String json = mvc.perform(
                multipart("/api/invoices/upload")
                    .file(file)
                    .param("sellerId", "24567891")
                    .param("profile", "Profile-Canonical")
                    .header("Idempotency-Key", "24567891-202511-E2E-001")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
            )
            .andExpect(status().isAccepted())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.jobId").exists())
            .andReturn().getResponse().getContentAsString();

        String jobId = om.readTree(json).path("jobId").asText();
        assertThat(jobId).isNotBlank();

        // Step 2: 測試中「模擬背景」把回饋寫入儲存並更新 Job
        String baseDir = System.getProperty("java.io.tmpdir") + "/turnbridge-it-storage";
        realFile = Path.of(baseDir, "outbound/24567891/202511/e2e-result.csv");
        TestFiles.writeCsvWithBom(realFile, "invoiceNo,result_code,result_msg\nAB00000001,0000,OK\n");

        StoredObject so = new StoredObject();
        so.setBucket("outbound");
        so.setObjectKey("outbound/24567891/202511/e2e-result.csv");
        so.setMediaType("text/csv");
        so.setContentLength(Files.size(realFile));
        so.setSha256("e3b0c44298fc1c149afbf4c8996fb924"); // 測試佔位
        so.setPurpose(StoragePurpose.RESULT_CSV);
        so.setFilename("e2e-result.csv");
        so.setStorageClass("STANDARD");
        so.setEncryption("AES256");
        so.setCreatedDate(Instant.now());
        so.setLastModifiedDate(Instant.now());
        so = soRepo.saveAndFlush(so);

        // 更新 job 狀態與關聯 resultFile
        UploadJob job = jobRepo.findOneByJobId(jobId).orElseThrow();
        job.setResultFile(so);
        job.setStatus(UploadJobStatus.RESULT_READY);
        job.setLastModifiedDate(Instant.now());
        jobRepo.saveAndFlush(job);

        // Step 3: 下載回饋
        byte[] bytes = mvc.perform(get("/api/upload-jobs/{jobId}/result?bom=true", jobId))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", org.hamcrest.Matchers.containsString("text/csv")))
            .andReturn().getResponse().getContentAsByteArray();

        // 驗證 BOM 與內容
        assertThat(bytes[0]).isEqualTo((byte)0xEF);
        assertThat(bytes[1]).isEqualTo((byte)0xBB);
        assertThat(bytes[2]).isEqualTo((byte)0xBF);
        String body = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        assertThat(body).contains("invoiceNo,result_code,result_msg");
        assertThat(body).contains("AB00000001,0000,OK");
    }
}

