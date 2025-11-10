package com.asynctide.turnbridge.m0.web;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.StoredObject;
import com.asynctide.turnbridge.domain.UploadJob;
import com.asynctide.turnbridge.domain.UploadJobItem;
import com.asynctide.turnbridge.domain.enumeration.JobItemStatus;
import com.asynctide.turnbridge.domain.enumeration.StoragePurpose;
import com.asynctide.turnbridge.domain.enumeration.TaxType;
import com.asynctide.turnbridge.domain.enumeration.UploadJobStatus;
import com.asynctide.turnbridge.repository.StoredObjectRepository;
import com.asynctide.turnbridge.repository.UploadJobItemRepository;
import com.asynctide.turnbridge.repository.UploadJobRepository;
import com.asynctide.turnbridge.testsupport.TestFiles;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 【驗收】批次查詢與回饋下載整合測試。
 *
 * 說明：
 * - JHipster 風格的分頁 API 回傳「List + 分頁 headers」，非 Page JSON；
 *   因此驗證重點：X-Total-Count、Link headers 與 body 陣列內容。
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "turnbridge.storage.type=local",
    "turnbridge.storage.local.base-dir=${java.io.tmpdir}/turnbridge-it-storage"
})
class JobsAndResultsApiIT {

    @Autowired MockMvc mvc;
    @Autowired UploadJobRepository uploadJobRepo;
    @Autowired UploadJobItemRepository itemRepo;
    @Autowired StoredObjectRepository soRepo;

    private UploadJob job;
    private StoredObject resultFile;
    private Path realFile;

    private static String sha256Hex(byte[] bytes) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] d = md.digest(bytes);
        StringBuilder sb = new StringBuilder(d.length * 2);
        for (byte b : d) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    @BeforeEach
    void setUp() throws Exception {
        // 1) 建立實體 CSV 檔（含 BOM）
    	
        String baseDir = System.getProperty("java.io.tmpdir") + "/turnbridge-it-storage";
        // 建立 outbound/result 檔（含 BOM）
        Path resultPath = Path.of(baseDir, "outbound/77665544/202511/result-ACK.csv");
        Files.createDirectories(resultPath.getParent());
        TestFiles.writeCsvWithBom(resultPath,
            "invoiceNo,result_code,result_msg\nAB00000001,0000,OK\nAB00000002,0402,欄位缺漏\n");
        byte[] resultBytes = Files.readAllBytes(resultPath);
        String shaResult = sha256Hex(resultBytes);

        // 建立 inbound/original 檔（也寫一份內容，供原始檔下載使用）
        Path originalPath = Path.of(baseDir, "inbound/77665544/202511/upload-original.csv");
        Files.createDirectories(originalPath.getParent());
        TestFiles.writeCsvWithBom(originalPath,
            "colA,colB\nx,y\n"); // 內容可任意，僅需存在
        byte[] originalBytes = Files.readAllBytes(originalPath);
        String shaOriginal = sha256Hex(originalBytes);

        // 2) Dummy 原始檔 StoredObject（滿足 UploadJob 非空關聯）
        // Dummy StoredObject：original
        StoredObject original = new StoredObject();
        original.setBucket("inbound");
        // objectKey 僅放相對路徑，不含 bucket
        original.setObjectKey("77665544/202511/upload-original.csv");
        original.setMediaType("text/csv");
        original.setContentLength((long) originalBytes.length);
        original.setSha256(shaOriginal);
        original.setPurpose(StoragePurpose.UPLOAD_ORIGINAL);
        original.setFilename("upload-original.csv");
        original.setStorageClass("STANDARD");
        original.setEncryption("AES256");
        original.setCreatedDate(Instant.now());
        original.setLastModifiedDate(Instant.now());
        original = soRepo.saveAndFlush(original);

        // 3) 回饋檔 StoredObject
        resultFile = new StoredObject();
        resultFile.setBucket("outbound");
        // objectKey 僅放相對路徑，不含 bucket
        resultFile.setObjectKey("77665544/202511/result-ACK.csv");
        resultFile.setMediaType("text/csv");
        resultFile.setContentLength((long) resultBytes.length);
        resultFile.setSha256(shaResult);
        resultFile.setPurpose(StoragePurpose.RESULT_CSV);
        resultFile.setFilename("result-ACK.csv");
        resultFile.setStorageClass("STANDARD");
        resultFile.setEncryption("AES256");
        resultFile.setCreatedDate(Instant.now());
        resultFile.setLastModifiedDate(Instant.now());
        resultFile = soRepo.saveAndFlush(resultFile);

        // 4) 建立 UploadJob（注意：同時關聯 originalFile 與 resultFile）
        job = new UploadJob();
        job.setJobId("JOB-IT-0001");
        job.setSellerId("77665544");
        job.setStatus(UploadJobStatus.RESULT_READY);
        job.setTotal(3);
        job.setAccepted(2);
        job.setFailed(1);
        job.setSent(2);
        job.setCreatedDate(Instant.now());
        job.setLastModifiedDate(Instant.now());
        job.setOriginalFile(original);
        job.setResultFile(resultFile);
        job = uploadJobRepo.saveAndFlush(job);

        // 5) 建立 3 筆明細
        itemRepo.saveAndFlush(newItem(job, 1, "JOB-IT-0001-0001", JobItemStatus.OK,
            null, null, "A123456789", "王小明", "TWD",
            bd("100.00"), bd("5.00"), bd("105.00"), TaxType.TAXABLE,
            LocalDate.now(), "AE00000001", "AE"));

        itemRepo.saveAndFlush(newItem(job, 2, "JOB-IT-0001-0002", JobItemStatus.ERROR,
            "0402", "欄位缺漏", "8J00000000", "小確幸商行", "TWD",
            bd("200.00"), bd("0.00"), bd("200.00"), TaxType.EXEMPT,
            LocalDate.now().minusDays(1), null, null));

        itemRepo.saveAndFlush(newItem(job, 3, "JOB-IT-0001-0003", JobItemStatus.QUEUED,
            null, null, null, null, "TWD",
            bd("50.00"), bd("0.00"), bd("50.00"), TaxType.ZERO,
            null, null, null));
    }

    @AfterEach
    void tearDown() throws Exception {
        try { Files.deleteIfExists(realFile); } catch (Exception ignored) {}
        itemRepo.deleteAllInBatch();
        uploadJobRepo.deleteAllInBatch();
        soRepo.deleteAllInBatch();
    }

    @Test
    @DisplayName("批次明細分頁（JHipster 風格）：GET /upload-jobs/{id}/items 回傳 List + 分頁 headers")
    void listItems_shouldReturnListWithPaginationHeaders() throws Exception {
        mvc.perform(get("/api/upload-jobs/{id}/items?page=0&size=20", job.getId()))
           .andExpect(status().isOk())
           .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
           // 分頁 headers 驗證
           .andExpect(header().string("X-Total-Count", "3"))
           .andExpect(header().string(org.springframework.http.HttpHeaders.LINK, org.hamcrest.Matchers.notNullValue()))
           // body 是陣列（List）
           .andExpect(jsonPath("$", hasSize(3)))
           .andExpect(jsonPath("$[0].lineNo").value(1))
           .andExpect(jsonPath("$[0].status").value("OK"))
           .andExpect(jsonPath("$[1].status").value("ERROR"))
           .andExpect(jsonPath("$[1].resultCode").value("0402"))
           .andExpect(jsonPath("$[2].status").value("QUEUED"));
    }

    @Test
    @DisplayName("回饋下載（以 jobId 字串）：GET /upload-jobs/by-job-id/{jobId}/result?bom=true")
    void downloadResult_byJobId_shouldReturnCsvWithBom() throws Exception {
        byte[] bytes = mvc.perform(get("/api/upload-jobs/by-job-id/{jobId}/result?bom=true", job.getJobId()))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", org.hamcrest.Matchers.containsString("text/csv")))
            .andReturn().getResponse().getContentAsByteArray();

        // 驗證 BOM 與內容
        assertThat(bytes.length).isGreaterThan(3);
        assertThat((bytes[0] & 0xFF)).isEqualTo(0xEF);
        assertThat((bytes[1] & 0xFF)).isEqualTo(0xBB);
        assertThat((bytes[2] & 0xFF)).isEqualTo(0xBF);
        String body = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        assertThat(body).contains("invoiceNo,result_code,result_msg");
        assertThat(body).contains("AB00000001,0000,OK");
        assertThat(body).contains("AB00000002,0402,欄位缺漏");
    }
    
    @Test
    @DisplayName("下載原始檔（以 jobId 字串）：GET /upload-jobs/by-job-id/{jobId}/original?bom=true")
    void downloadOriginal_byJobId_shouldReturnCsvWithBom() throws Exception {
        byte[] bytes = mvc.perform(get("/api/upload-jobs/by-job-id/{jobId}/original?bom=true", job.getJobId()))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", org.hamcrest.Matchers.containsString("text/csv")))
            .andReturn().getResponse().getContentAsByteArray();

        assertThat(bytes.length).isGreaterThan(3);
        assertThat((bytes[0] & 0xFF)).isEqualTo(0xEF);
        assertThat((bytes[1] & 0xFF)).isEqualTo(0xBB);
        assertThat((bytes[2] & 0xFF)).isEqualTo(0xBF);
    }

    @Test
    @DisplayName("統計與重試：/stats + /retry-failed")
    void stats_and_retry() throws Exception {
        mvc.perform(get("/api/upload-jobs/by-job-id/{jobId}/stats", job.getJobId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(3))
            .andExpect(jsonPath("$.error").value(1));

     // 1) 原本 ERROR 的第 2 筆 → 變成 QUEUED，且錯誤欄位清空
     mvc.perform(get("/api/upload-jobs/by-job-id/{jobId}/items?status=QUEUED", job.getJobId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.lineNo==2)].resultCode").value(org.hamcrest.Matchers.hasItem((Object) null)))
        .andExpect(jsonPath("$[?(@.lineNo==2)].resultMsg").value(org.hamcrest.Matchers.hasItem((Object) null)));

     // 2) 原本 OK 的第 1 筆 → 仍為 OK，且（若先前有值）錯誤欄位不被動到
     mvc.perform(get("/api/upload-jobs/by-job-id/{jobId}/items?status=OK", job.getJobId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].lineNo").value(1))
        .andExpect(jsonPath("$[0].status").value("OK"));

     // 3) 原本就 QUEUED 的第 3 筆 → 仍為 QUEUED（沒有被二次清空或異動）
     mvc.perform(get("/api/upload-jobs/by-job-id/{jobId}/items?status=QUEUED", job.getJobId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].lineNo").value(org.hamcrest.Matchers.hasItems(2, 3)));
    }

    private UploadJobItem newItem(
        UploadJob job, int lineNo, String traceId, JobItemStatus status,
        String resultCode, String resultMsg, String buyerId, String buyerName, String currency,
        BigDecimal excl, BigDecimal tax, BigDecimal incl, TaxType taxType,
        LocalDate invoiceDate, String invoiceNo, String assignedPrefix
    ) {
        UploadJobItem i = new UploadJobItem();
        i.setJob(job);
        i.setLineNo(lineNo);
        i.setTraceId(traceId);
        i.setStatus(status);
        i.setResultCode(resultCode);
        i.setResultMsg(resultMsg);
        i.setBuyerId(buyerId);
        i.setBuyerName(buyerName);
        i.setCurrency(currency);
        i.setAmountExcl(excl);
        i.setTaxAmount(tax);
        i.setAmountIncl(incl);
        i.setTaxType(taxType);
        i.setInvoiceDate(invoiceDate);
        i.setInvoiceNo(invoiceNo);
        i.setAssignedPrefix(assignedPrefix);
        i.setCreatedDate(Instant.now());
        i.setLastModifiedDate(Instant.now());
        return i;
    }

    private static BigDecimal bd(String s) { return new BigDecimal(s); }
}
