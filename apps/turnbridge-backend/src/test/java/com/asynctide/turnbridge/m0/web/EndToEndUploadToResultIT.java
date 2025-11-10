package com.asynctide.turnbridge.m0.web;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.UploadJob;
import com.asynctide.turnbridge.domain.enumeration.UploadJobStatus;
import com.asynctide.turnbridge.repository.StoredObjectRepository;
import com.asynctide.turnbridge.repository.UploadJobRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 【E2E】從上傳到下載回饋的端到端流程（由 Pipeline 自動產出回饋）。
 *
 * 流程：
 * 1) POST /invoices/upload -> 取得 jobId
 * 2) 等待 Pipeline 將 Job 推進至 RESULT_READY（並綁定 resultFile）
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

    @AfterEach
    void tearDown() {
        jobRepo.deleteAllInBatch();
        soRepo.deleteAllInBatch();
    }

    @Test
    @DisplayName("上傳 → Pipeline 產製回饋 → 下載 CSV 成功")
    void e2e_upload_then_download_result() throws Exception {
        // Step 1: 上傳 CSV
        String csv = "invoiceNo,buyerId,amount\nAB00000001,12345678,100\n";
        MockMultipartFile file = new MockMultipartFile(
            "file", "sample.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8)
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

        // Step 2: 等待 Pipeline 完成（RESULT_READY 且有 resultFile）
        Instant deadline = Instant.now().plus(Duration.ofSeconds(10));
        UploadJob job = null;
        do {
            Thread.sleep(200);
            job = jobRepo.findOneByJobId(jobId).orElseThrow();
            if (job.getStatus() == UploadJobStatus.RESULT_READY && job.getResultFile() != null) break;
        } while (Instant.now().isBefore(deadline));

        assertThat(job.getStatus()).isEqualTo(UploadJobStatus.RESULT_READY);
        assertThat(job.getResultFile()).isNotNull();

        // Step 3: 下載回饋（驗證為 CSV，且含 BOM）
        byte[] bytes = mvc.perform(get("/api/upload-jobs/by-job-id/{jobId}/result?bom=true", jobId))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", org.hamcrest.Matchers.containsString("text/csv")))
            .andReturn().getResponse().getContentAsByteArray();

        // 驗證 BOM
        assertThat(bytes.length).isGreaterThanOrEqualTo(3);
        assertThat(bytes[0]).isEqualTo((byte)0xEF);
        assertThat(bytes[1]).isEqualTo((byte)0xBB);
        assertThat(bytes[2]).isEqualTo((byte)0xBF);

        String body = new String(bytes, StandardCharsets.UTF_8);
        assertThat(body).contains("lineNo,resultCode,resultMsg"); // 由 ResultFileGenerator 產生的表頭
    }
}
