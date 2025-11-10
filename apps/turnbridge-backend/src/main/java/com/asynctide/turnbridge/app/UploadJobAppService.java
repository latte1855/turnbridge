package com.asynctide.turnbridge.app;

import com.asynctide.turnbridge.config.StorageProps;
import com.asynctide.turnbridge.domain.StoredObject;
import com.asynctide.turnbridge.domain.UploadJob;
import com.asynctide.turnbridge.domain.enumeration.StoragePurpose;
import com.asynctide.turnbridge.domain.enumeration.UploadJobStatus;
import com.asynctide.turnbridge.repository.StoredObjectRepository;
import com.asynctide.turnbridge.repository.UploadJobRepository;
import com.asynctide.turnbridge.service.StoredObjectService;
import com.asynctide.turnbridge.service.dto.StoredObjectDTO;
import com.asynctide.turnbridge.storage.StoredObjectRef;
import com.asynctide.turnbridge.storage.StorageProvider;
import com.asynctide.turnbridge.support.IdempotencyService;
import jakarta.transaction.Transactional;
import java.io.InputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上傳批次應用服務。
 * <p>流程：儲存檔案 → 建立 StoredObject/UploadJob → 送入簡化管線。</p>
 */
@Service
public class UploadJobAppService {

    private final StorageProvider storage;
    private final UploadJobRepository jobRepo;
    private final StoredObjectRepository soRepo;
    private final IdempotencyService idem;
    private final StorageProps props;
    private final UploadPipeline pipeline;
    private final StoredObjectService storedObjectService;

    public UploadJobAppService(
        StorageProvider storage,
        UploadJobRepository jobRepo,
        StoredObjectRepository soRepo,
        IdempotencyService idem,
        StorageProps props,
        UploadPipeline pipeline,
        StoredObjectService storedObjectService
    ) {
        this.storage = storage;
        this.jobRepo = jobRepo;
        this.soRepo = soRepo;
        this.idem = idem;
        this.props = props;
        this.pipeline = pipeline;
        this.storedObjectService = storedObjectService;
    }

    /**
     * 由 Multipart 檔案建立批次。
     */
    @Transactional
    public UploadJob createFromMultipart(MultipartFile file, String sellerId, String profile, String idemKey) {
        if (idemKey != null && !idem.markIfNew(idemKey)) {
            throw new IllegalStateException("重複的 Idempotency-Key，已處理過本次請求。");
        }
        String jobId =
            "JOB-" + Instant.now().toString().replace(":", "").replace(".", "") + "-" + UUID.randomUUID().toString().substring(0, 8);

        // 1) 儲存檔案到 Storage
        String bucket = "inbound";
        String objectKey =
            String.format(
                "%s/%s/%s",
                sellerId,
                Instant.now().toString().substring(0, 7).replace("-", ""),
                file.getOriginalFilename()
            );

        Map<String, String> meta = new HashMap<>();
        meta.put("sellerId", sellerId);
        meta.put("profile", profile == null ? "" : profile);

        StoredObjectRef ref;
        try {
            ref = storage.store(file.getInputStream(), file.getSize(), file.getContentType(), bucket, objectKey, meta);
        } catch (Exception e) {
            throw new RuntimeException("儲存上傳檔案失敗：" + e.getMessage(), e);
        }

        // 2) 建立 StoredObject
        StoredObject so = new StoredObject();
        so.setBucket(ref.bucket());
        so.setObjectKey(ref.objectKey());
        so.setMediaType(ref.mediaType());
        so.setContentLength(ref.contentLength());
        so.setSha256(ref.sha256());
        so.setPurpose(StoragePurpose.UPLOAD_ORIGINAL);
        so.setFilename(file.getOriginalFilename());
        so.setStorageClass(ref.storageClass());
        so.setEncryption(ref.encryption());
        so.setCreatedDate(Instant.now());
        so = soRepo.save(so);

        // 3) 建立 UploadJob
        UploadJob job = new UploadJob();
        job.setJobId(jobId);
        job.setSellerId(sellerId);
        job.setProfile(profile);
        job.setSourceFilename(file.getOriginalFilename());
        job.setSourceMediaType(file.getContentType());
        job.setStatus(UploadJobStatus.RECEIVED);
        job.setCreatedDate(Instant.now());
        job.setLastModifiedDate(Instant.now());
        job.setTotal(0);
        job.setAccepted(0);
        job.setFailed(0);
        job.setSent(0);
        job.setOriginalFile(so);
        job = jobRepo.save(job);

        // 4) 啟動管線
        MDC.put("jobId", jobId);
        pipeline.enqueue(job.getId(), profile);
        MDC.remove("jobId");

        return job;
    }

    /**
     * 開啟儲存中的物件輸入串流。
     * <p>呼叫端負責關閉 InputStream。</p>
     */
    public InputStream openStoredObject(StoredObjectDTO so) throws Exception {
    	if (so == null) throw new IllegalArgumentException("StoredObjectDTO is null");
        String bucket = so.getBucket();
        String objectKey = so.getObjectKey();
        if (bucket == null || bucket.isBlank()) {
            throw new IllegalStateException("StoredObject.bucket is null/blank");
        }
        if (objectKey == null || objectKey.isBlank()) {
            throw new IllegalStateException("StoredObject.objectKey is null/blank");
        }

        // 正規化：若 objectKey 以 "<bucket>/" 開頭，去掉重複的 bucket 前綴
        String normalizedKey = normalizeObjectKey(bucket, objectKey);

        // 交給 StorageProvider 開啟
        return storage.open(bucket, normalizedKey).orElseThrow(() -> new IllegalStateException("物件不存在於 Storage"));
    }
    
    public InputStream openStoredObject(Long soId) throws Exception {
        StoredObjectDTO so = storedObjectService.findOne(soId).orElseThrow();
        return openStoredObject(so);
    }


	/** 若 objectKey 以 "<bucket>/" 開頭就移除該段，避免路徑重複。 */
	private static String normalizeObjectKey(String bucket, String objectKey) {
	    String prefix = bucket + "/";
	    if (objectKey.startsWith(prefix)) {
	        return objectKey.substring(prefix.length());
	    }
	    return objectKey;
	}
    
}
