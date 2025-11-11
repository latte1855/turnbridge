package com.asynctide.turnbridge.app;

import com.asynctide.turnbridge.storage.StorageProvider;
import com.asynctide.turnbridge.storage.StoredObjectRef;
import com.asynctide.turnbridge.domain.StoredObject;
import com.asynctide.turnbridge.domain.UploadJob;
import com.asynctide.turnbridge.domain.UploadJobItem;
import com.asynctide.turnbridge.domain.enumeration.JobItemStatus;
import com.asynctide.turnbridge.domain.enumeration.StoragePurpose;
import com.asynctide.turnbridge.repository.StoredObjectRepository;
import com.asynctide.turnbridge.repository.UploadJobItemRepository;
import com.asynctide.turnbridge.repository.UploadJobRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 產生簡化回饋 CSV 檔，並關聯至 UploadJob.resultFile。
 */
@Component
public class ResultFileGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(UploadPipeline.class);
    private final StorageProvider storage;
    private final UploadJobRepository jobRepo;
    private final StoredObjectRepository soRepo;
    private final UploadJobItemRepository itemRepo;

    public ResultFileGenerator(StorageProvider storage, UploadJobRepository jobRepo, StoredObjectRepository soRepo, UploadJobItemRepository itemRepo) {
        this.storage = storage;
        this.jobRepo = jobRepo;
        this.soRepo = soRepo;
        this.itemRepo = itemRepo;
    }

    public void generate(Long jobDbId) {
        UploadJob job = jobRepo.findById(jobDbId).orElseThrow();

        StringBuilder sb = new StringBuilder();
        sb.append("lineNo,invoiceNo,buyerId,amount,result_code,result_msg,trace_id\n");

        int page = 0;
        int size = 2000;
        Page<UploadJobItem> p;
        do {
            p = itemRepo.findByJobId(jobDbId, PageRequest.of(page++, size));
            for (var it : p.getContent()) {
                String code = it.getStatus() == JobItemStatus.OK ? "0000" : (it.getResultCode() == null ? "0400" : it.getResultCode());
                String msg  = it.getResultMsg() == null ? (it.getStatus() == JobItemStatus.OK ? "OK" : "ERROR") : it.getResultMsg();
                sb.append(n(it.getLineNo())).append(',')
                  .append(s(it.getInvoiceNo())).append(',')
                  .append(s(it.getBuyerId())).append(',')
                  .append(n(it.getAmountIncl())).append(',')
                  .append(s(code)).append(',')
                  .append(s(msg)).append(',')
                  .append(s(it.getTraceId())).append('\n');
            }
        } while (!p.isLast());

        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        String bucket = "outbound";
        String objectKey = "results/" + job.getSellerId() + "/" + job.getJobId() + "-result.csv";

        StoredObjectRef ref = storage.store(new ByteArrayInputStream(bytes), bytes.length, "text/csv", bucket, objectKey,
            Map.of("sellerId", job.getSellerId(), "jobId", job.getJobId()));

        StoredObject so = new StoredObject();
        so.setBucket(ref.bucket());
        so.setObjectKey(ref.objectKey());
        so.setMediaType(ref.mediaType());
        so.setContentLength(ref.contentLength());
        so.setSha256(ref.sha256());
        so.setPurpose(StoragePurpose.RESULT_CSV);
        so.setFilename(job.getJobId() + "-result.csv");
        so.setStorageClass(ref.storageClass());
        so.setEncryption(ref.encryption());
        so.setCreatedDate(Instant.now());
        so = soRepo.save(so);

        job.setResultFile(so);
        job.setLastModifiedDate(Instant.now());
        jobRepo.save(job);
    }

    private static String s(String v) { return v == null ? "" : v.replace(",", " "); }
    private static String n(Object v) { return v == null ? "" : v.toString(); }
}
