package com.asynctide.turnbridge.web.rest;

import com.asynctide.turnbridge.app.UploadJobAppService;
import com.asynctide.turnbridge.service.*;
import com.asynctide.turnbridge.service.dto.*;
import com.asynctide.turnbridge.domain.enumeration.JobItemStatus;
import com.asynctide.turnbridge.repository.UploadJobItemRepository;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@RestController
// 刻意不加 class-level 前綴，讓 path 與 OpenAPI 的 /invoices、/upload-jobs 對齊
public class OpenApiCompatResource {

    private final UploadJobAppService appService;
    private final UploadJobService uploadJobService;
    private final UploadJobItemService uploadJobItemService;
    private final UploadJobItemQueryService uploadJobItemQueryService;
    private final UploadJobItemRepository uploadJobItemRepository;
    private final StoredObjectService storedObjectService;

    public OpenApiCompatResource(
        UploadJobAppService appService,
        UploadJobService uploadJobService,
        UploadJobItemService uploadJobItemService,
        UploadJobItemQueryService uploadJobItemQueryService,
        UploadJobItemRepository uploadJobItemRepository,
        StoredObjectService storedObjectService
    ) {
        this.appService = appService;
        this.uploadJobService = uploadJobService;
        this.uploadJobItemService = uploadJobItemService;
        this.uploadJobItemQueryService = uploadJobItemQueryService;
        this.uploadJobItemRepository = uploadJobItemRepository;
        this.storedObjectService = storedObjectService;
    }

    // ===== /invoices/upload =====
    @PostMapping(value = "/invoices/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UploadJobDTO> upload(
        @RequestPart("file") MultipartFile file,
        @RequestParam String sellerId,
        @RequestParam(required = false) String profile,
        @RequestHeader(value = "Idempotency-Key", required = false) String idemKey
    ) {
        // 與現有 /api/invoices/upload 等價，但回傳 DTO（若你偏好回傳 Entity 也可保持一致）
        var job = appService.createFromMultipart(file, sellerId, profile, idemKey);
        // 將 Entity 轉 DTO（你已有 mapper/service，若無可改回傳 Entity）
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(uploadJobService.findOne(job.getId()).orElseThrow());
    }

    // ===== /upload-jobs/{jobId} =====
    @GetMapping(value = "/upload-jobs/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UploadJobDTO> getUploadJob(@PathVariable String jobId) {
        return uploadJobService.findOneByJobId(jobId)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // ===== /upload-jobs/{jobId}/items ===== （支援 ?status= & 分頁參數由 Spring 自動注入）
    @GetMapping(value = "/upload-jobs/{jobId}/items", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UploadJobItemDTO>> listItems(
        @PathVariable String jobId,
        @RequestParam(required = false) JobItemStatus status,
        @org.springdoc.core.annotations.ParameterObject org.springframework.data.domain.Pageable pageable
    ) {
        var page = uploadJobItemService.findByJobJobId(jobId, status, pageable);
        HttpHeaders headers = tech.jhipster.web.util.PaginationUtil
            .generatePaginationHttpHeaders(org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    // ===== /upload-jobs/{jobId}/result =====
    @GetMapping("/upload-jobs/{jobId}/result")
    public ResponseEntity<byte[]> downloadResult(@PathVariable String jobId, @RequestParam(defaultValue = "true") boolean bom) throws Exception {
        UploadJobDTO job = uploadJobService.findOneByJobId(jobId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return buildBinary(job.getResultFile(), bom);
    }

    // ===== /upload-jobs/{jobId}/original =====
    @GetMapping("/upload-jobs/{jobId}/original")
    public ResponseEntity<byte[]> downloadOriginal(@PathVariable String jobId, @RequestParam(defaultValue = "true") boolean bom) throws Exception {
        UploadJobDTO job = uploadJobService.findOneByJobId(jobId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        StoredObjectDTO so = job.getOriginalFile();
        if (so == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "尚無原始檔");
        return buildBinary(so, bom);
    }

    // ===== /upload-jobs/{jobId}/stats =====
    @GetMapping("/upload-jobs/{jobId}/stats")
    public ResponseEntity<UploadJobStatsDTO> stats(@PathVariable String jobId) {
        long total = uploadJobItemRepository.countByJobJobId(jobId);
        long ok = uploadJobItemRepository.countByJobJobIdAndStatus(jobId, JobItemStatus.OK);
        long error = uploadJobItemRepository.countByJobJobIdAndStatus(jobId, JobItemStatus.ERROR);
        long queued = uploadJobItemRepository.countByJobJobIdAndStatus(jobId, JobItemStatus.QUEUED);
        return ResponseEntity.ok(new UploadJobStatsDTO(total, ok, error, queued));
    }

    // ===== /upload-jobs/{jobId}/retry-failed =====
    @PostMapping("/upload-jobs/{jobId}/retry-failed")
    public ResponseEntity<Void> retryFailed(@PathVariable String jobId) {
        uploadJobItemService.requeueFailedByJobJobId(jobId);
        return ResponseEntity.accepted().build();
    }

    // ===== /stored-objects/{id}/download =====
    @GetMapping("/stored-objects/{id}/download")
    public ResponseEntity<byte[]> downloadStored(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean bom) throws Exception {
        StoredObjectDTO so = storedObjectService.findOne(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return buildBinary(so, bom);
    }

    // === 共用輸出 ===
    private ResponseEntity<byte[]> buildBinary(StoredObjectDTO so, boolean bom) throws Exception {
        if (so == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        try (InputStream in = appService.openStoredObject(so)) {
            byte[] body = in.readAllBytes();
            MediaType mt = MediaType.parseMediaType(so.getMediaType() == null ? "application/octet-stream" : so.getMediaType());
            if (bom && "text".equalsIgnoreCase(mt.getType())) {
                byte[] bomBytes = new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF };
                byte[] merged = new byte[bomBytes.length + body.length];
                System.arraycopy(bomBytes, 0, merged, 0, bomBytes.length);
                System.arraycopy(body, 0, merged, bomBytes.length, body.length);
                body = merged;
            }
            String filename = Optional.ofNullable(so.getFilename()).orElse("download.bin");
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + UriUtils.encode(filename, StandardCharsets.UTF_8))
                .header(HttpHeaders.ETAG, "\"" + Optional.ofNullable(so.getSha256()).orElse(String.valueOf(body.length)) + "\"")
                .contentType(mt)
                .contentLength(body.length)
                .body(body);
        } catch (IllegalStateException ex) {
            if ("物件不存在於 Storage".equals(ex.getMessage())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
            }
            throw ex;
        }
    }
}
