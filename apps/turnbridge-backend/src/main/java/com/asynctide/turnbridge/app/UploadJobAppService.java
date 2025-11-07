package com.asynctide.turnbridge.app;

import com.asynctide.turnbridge.config.StorageProps;
import com.asynctide.turnbridge.storage.StorageProvider;
import com.asynctide.turnbridge.storage.StoredObjectRef;
import com.asynctide.turnbridge.support.IdempotencyService;
import com.asynctide.turnbridge.domain.StoredObject;
import com.asynctide.turnbridge.domain.UploadJob;
import com.asynctide.turnbridge.domain.enumeration.StoragePurpose;
import com.asynctide.turnbridge.domain.enumeration.UploadJobStatus;
import com.asynctide.turnbridge.repository.StoredObjectRepository;
import com.asynctide.turnbridge.repository.UploadJobRepository;
import jakarta.transaction.Transactional;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 上傳批次應用服務。
 * <p>負責：儲存檔案 → 建立 UploadJob → 啟動簡化非同步管線。</p>
 */
@Service
public class UploadJobAppService {

    private final StorageProvider storage;
    private final UploadJobRepository jobRepo;
    private final StoredObjectRepository soRepo;
    private final IdempotencyService idem;
    private final StorageProps props;
    private final UploadPipeline pipeline;

    public UploadJobAppService(StorageProvider storage,
                               UploadJobRepository jobRepo,
                               StoredObjectRepository soRepo,
                               IdempotencyService idem,
                               StorageProps props,
                               UploadPipeline pipeline) {
        this.storage = storage;
        this.jobRepo = jobRepo;
        this.soRepo = soRepo;
        this.idem = idem;
        this.props = props;
        this.pipeline = pipeline;
    }

    /**
     * 由 Multipart 檔案建立批次。
     *
     * @param file     上傳檔案（CSV/ZIP）
     * @param sellerId 賣方識別
     * @param profile  解析 Profile（可為空）
     * @param idemKey  冪等鍵（可為空）
     * @return         已持久化的 UploadJob
     */
    @Transactional
    public UploadJob createFromMultipart(MultipartFile file, String sellerId, String profile, String idemKey) {
        if (idemKey != null && !idem.markIfNew(idemKey)) {
            throw new IllegalStateException("重複的 Idempotency-Key，已處理過本次請求。");
        }
        String jobId = "JOB-" + Instant.now().toString().replace(":", "").replace(".", "") + "-" + UUID.randomUUID().toString().substring(0, 8);
        // 1) 先把檔案存入 Storage
        String bucket = "inbound";
        String objectKey = String.format("%s/%s/%s", sellerId, // 可依實際路徑規劃
                Instant.now().toString().substring(0, 7).replace("-", ""),
                file.getOriginalFilename());

        Map<String,String> meta = new HashMap<>();
        meta.put("sellerId", sellerId);
        meta.put("profile", profile == null ? "" : profile);

        StoredObjectRef ref;
        try {
            ref = storage.store(file.getInputStream(), file.getSize(), file.getContentType(), bucket, objectKey, meta);
        } catch (Exception e) {
            throw new RuntimeException("儲存上傳檔案失敗：" + e.getMessage(), e);
        }

        // 2) 建立 StoredObject 實體
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
        job.setTotal(0); job.setAccepted(0); job.setFailed(0); job.setSent(0);
        job.setOriginalFile(so);
        job = jobRepo.save(job);

        // 4) 啟動管線（非同步執行）
        MDC.put("jobId", jobId);
        pipeline.enqueue(job.getId(), profile);
        MDC.remove("jobId");

        return job;
    }
}
