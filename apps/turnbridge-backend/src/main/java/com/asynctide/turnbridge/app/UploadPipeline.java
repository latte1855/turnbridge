package com.asynctide.turnbridge.app;

import com.asynctide.turnbridge.domain.UploadJob;
import com.asynctide.turnbridge.domain.enumeration.UploadJobStatus;
import com.asynctide.turnbridge.repository.UploadJobRepository;
import com.asynctide.turnbridge.service.parse.InvoiceParseService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * 上傳處理管線（M0 簡化版）。
 * <p>模擬：RECEIVED → PARSING → VALIDATING → PACKING → SENT → RESULT_READY</p>
 */
@Component
public class UploadPipeline {
    private static final Logger LOG = LoggerFactory.getLogger(UploadPipeline.class);

    private final TaskExecutor taskExecutor;
    private final UploadJobRepository jobRepo;
    private final ResultFileGenerator resultFileGenerator;
    private final InvoiceParseService parseService;

    public UploadPipeline(TaskExecutor taskExecutor, UploadJobRepository jobRepo, ResultFileGenerator resultFileGenerator, InvoiceParseService parseService) {
        this.taskExecutor = taskExecutor;
        this.jobRepo = jobRepo;
        this.resultFileGenerator = resultFileGenerator;
        this.parseService = parseService;
    }

    /** 將 jobId 丟入背景執行（單純模擬延時與狀態推進）。 */
    public void enqueue(Long jobDbId, String profile) {
        taskExecutor.execute(() -> {
            try {
                step(jobDbId, UploadJobStatus.PARSING);
                
                // 解析 + 寫入明細 + 更新 counters
                UploadJob job = jobRepo.findById(jobDbId).orElseThrow();
                var stats = parseService.parseAndPersistItems(job);
                job.setTotal(stats.total());
                job.setAccepted(stats.ok());
                job.setFailed(stats.error());
                job.setLastModifiedDate(Instant.now());
                jobRepo.save(job);
                
                step(jobDbId, UploadJobStatus.VALIDATING);
                step(jobDbId, UploadJobStatus.PACKING);
                step(jobDbId, UploadJobStatus.SENT);
                // 產生結果檔並關聯
                resultFileGenerator.generate(jobDbId);
                step(jobDbId, UploadJobStatus.RESULT_READY);
            } catch (Exception e) {
                UploadJob job = jobRepo.findById(jobDbId).orElseThrow();
                job.setStatus(UploadJobStatus.FAILED);
                job.setLastModifiedDate(Instant.now());
                jobRepo.save(job);
                LOG.error("管線執行失敗：jobId={}, {}", job.getJobId(), e.getMessage(), e);
            }
        });
    }

    private void step(Long jobDbId, UploadJobStatus status) throws InterruptedException {
        UploadJob job = jobRepo.findById(jobDbId).orElseThrow();
        job.setStatus(status);
        job.setLastModifiedDate(Instant.now());
        jobRepo.save(job);
        Thread.sleep(400); // 模擬延時
    }
}
