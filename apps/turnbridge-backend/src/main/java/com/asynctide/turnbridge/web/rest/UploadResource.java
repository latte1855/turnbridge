package com.asynctide.turnbridge.web.rest;

import com.asynctide.turnbridge.domain.enumeration.ImportType;
import com.asynctide.turnbridge.service.upload.UploadMetadata;
import com.asynctide.turnbridge.service.upload.UploadResponse;
import com.asynctide.turnbridge.service.upload.UploadService;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上傳 API（Phase 1）。
 */
@RestController
@RequestMapping("/api/upload")
public class UploadResource {

    private static final Logger log = LoggerFactory.getLogger(UploadResource.class);

    private final UploadService uploadService;

    public UploadResource(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    /**
     * 上傳發票/折讓 CSV/ZIP。
     */
    @PostMapping(value = "/invoice", consumes = "multipart/form-data")
    public ResponseEntity<UploadResponse> uploadInvoice(
        @RequestParam("file") MultipartFile file,
        @RequestParam("sellerId") @NotBlank String sellerId,
        @RequestParam("sha256") @NotBlank String sha256,
        @RequestParam(value = "encoding", required = false) String encoding,
        @RequestParam(value = "profile", required = false) String profile,
        @RequestParam(value = "legacyType", required = false) String legacyType,
        @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey
    ) {
        log.debug("REST request to upload invoice file [{}]", file != null ? file.getOriginalFilename() : "null");
        UploadMetadata metadata = new UploadMetadata(sellerId, encoding, profile, sha256, legacyType, idempotencyKey);
        UploadResponse response = uploadService.handleUpload(ImportType.INVOICE, file, metadata);
        return ResponseEntity.accepted().body(response);
    }

    /**
     * 上傳 E0501 配號檔。
     */
    @PostMapping(value = "/e0501", consumes = "multipart/form-data")
    public ResponseEntity<UploadResponse> uploadE0501(
        @RequestParam("file") MultipartFile file,
        @RequestParam("sellerId") @NotBlank String sellerId,
        @RequestParam("sha256") @NotBlank String sha256,
        @RequestParam(value = "encoding", required = false) String encoding,
        @RequestParam(value = "profile", required = false) String profile,
        @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey
    ) {
        log.debug("REST request to upload e0501 file [{}]", file != null ? file.getOriginalFilename() : "null");
        UploadMetadata metadata = new UploadMetadata(sellerId, encoding, profile, sha256, "E0501", idempotencyKey);
        UploadResponse response = uploadService.handleUpload(ImportType.E0501, file, metadata);
        return ResponseEntity.accepted().body(response);
    }
}
