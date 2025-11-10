package com.asynctide.turnbridge.app;

import com.asynctide.turnbridge.storage.StorageProvider;
import com.asynctide.turnbridge.storage.StoredObjectRef;
import com.asynctide.turnbridge.domain.StoredObject;
import com.asynctide.turnbridge.domain.UploadJob;
import com.asynctide.turnbridge.domain.enumeration.StoragePurpose;
import com.asynctide.turnbridge.repository.StoredObjectRepository;
import com.asynctide.turnbridge.repository.UploadJobRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
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

    public ResultFileGenerator(StorageProvider storage, UploadJobRepository jobRepo, StoredObjectRepository soRepo) {
        this.storage = storage;
        this.jobRepo = jobRepo;
        this.soRepo = soRepo;
    }

    public void generate(Long jobDbId) {
        UploadJob job = jobRepo.findById(jobDbId).orElseThrow();
        String content = "lineNo,resultCode,resultMsg\n1,OK,\n2,ERROR,格式錯誤\n";
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
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
}
