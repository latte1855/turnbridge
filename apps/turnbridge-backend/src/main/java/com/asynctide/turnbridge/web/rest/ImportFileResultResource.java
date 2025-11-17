package com.asynctide.turnbridge.web.rest;

import com.asynctide.turnbridge.service.upload.ImportResultService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.ByteArrayResource;

/**
 * 匯入結果下載 API（提供單批 CSV 與多批 ZIP）。
 */
@RestController
@RequestMapping("/api/import-files")
public class ImportFileResultResource {

    private final ImportResultService importResultService;

    public ImportFileResultResource(ImportResultService importResultService) {
        this.importResultService = importResultService;
    }

    /**
     * 下載單一匯入批次的 CSV 結果。
     */
    @GetMapping("/{id}/result")
    public ResponseEntity<ByteArrayResource> downloadSingleResult(@PathVariable Long id) {
        return importResultService.buildCsvResponse(id);
    }

    /**
     * 下載多個匯入批次的結果（打包 ZIP）。
     */
    @PostMapping("/results/download")
    public ResponseEntity<ByteArrayResource> downloadMultipleResults(@Valid @RequestBody ResultDownloadRequest request) {
        return importResultService.buildZipResponse(request.importFileIds());
    }

    /**
     * 下載請求 payload。
     * @param importFileIds 需打包的匯入批次 ID 清單
     */
    public record ResultDownloadRequest(@NotEmpty List<Long> importFileIds) {}
}
