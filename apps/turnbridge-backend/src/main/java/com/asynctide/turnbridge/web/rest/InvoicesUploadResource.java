package com.asynctide.turnbridge.web.rest;

import com.asynctide.turnbridge.app.UploadJobAppService;
import com.asynctide.turnbridge.domain.UploadJob;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 發票上傳 API（Milestone 0）
 * 提供 /api/invoices/upload，與既有的 /api/upload-jobs/_multipart 等價，
 * 主要用來符合測試與 OpenAPI 規格中的路徑。
 */
@RestController
@RequestMapping("/api/invoices")
public class InvoicesUploadResource {

    private final UploadJobAppService appService;

    public InvoicesUploadResource(UploadJobAppService appService) {
        this.appService = appService;
    }

    /**
     * 上傳發票 CSV/ZIP。
     * - form field: file (Multipart)
     * - form field: sellerId (必填)
     * - form field: profile (選填)
     * - header: Idempotency-Key (選填)
     *
     * 回應 202 Accepted + UploadAck(JSON)
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UploadJob> upload(
        @RequestPart("file") MultipartFile file,
        @RequestParam @NotBlank String sellerId,
        @RequestParam(required = false) String profile,
        @RequestHeader(value = "Idempotency-Key", required = false) String idemKey
    ) {
        UploadJob job = appService.createFromMultipart(file, sellerId, profile, idemKey);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(job);
    }
}
