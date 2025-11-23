package com.asynctide.turnbridge.web.rest;

import com.asynctide.turnbridge.security.AuthoritiesConstants;
import com.asynctide.turnbridge.service.dto.ImportFileLogDTO;
import com.asynctide.turnbridge.service.mapper.ImportFileLogMapper;
import com.asynctide.turnbridge.service.turnkey.TurnkeyXmlExportService;
import com.asynctide.turnbridge.turnkey.TurnkeyProperties;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Turnkey 匯出操作入口，供管理者手動觸發 XML 產生流程。
 */
@RestController
@RequestMapping("/api/turnkey")
public class TurnkeyExportResource {

    private static final Logger LOG = LoggerFactory.getLogger(TurnkeyExportResource.class);

    private final TurnkeyXmlExportService exportService;
    private final TurnkeyProperties turnkeyProperties;
    private final ImportFileLogMapper importFileLogMapper;

    public TurnkeyExportResource(
        TurnkeyXmlExportService exportService,
        TurnkeyProperties turnkeyProperties,
        ImportFileLogMapper importFileLogMapper
    ) {
        this.exportService = exportService;
        this.turnkeyProperties = turnkeyProperties;
        this.importFileLogMapper = importFileLogMapper;
    }

    /**
     * 手動觸發 Turnkey XML 匯出。
     *
     * @param batchSize 覆寫批次大小（可選）
     * @return 匯出結果
     */
    @PostMapping("/export")
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<Map<String, Object>> triggerExport(@RequestParam(name = "batchSize", required = false) Integer batchSize) {
        int resolvedBatchSize = resolveBatchSize(batchSize);
        int processed = exportService.exportPendingInvoices(resolvedBatchSize);
        LOG.info("Manual Turnkey export triggered: batchSize={}, processed={}", resolvedBatchSize, processed);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("batchSize", resolvedBatchSize);
        body.put("processed", processed);
        return ResponseEntity.ok(body);
    }

    private int resolveBatchSize(Integer batchSize) {
        if (batchSize != null && batchSize > 0) {
            return batchSize;
        }
        int configured = turnkeyProperties.getExportBatchSize();
        return Math.max(1, configured);
    }

    /**
     * 查詢最近的 XML 匯出事件（ImportFileLog）。
     * @param size 取回筆數
     */
    @GetMapping("/export/logs")
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<java.util.List<ImportFileLogDTO>> getRecentExportLogs(@RequestParam(name = "size", required = false) Integer size) {
        int limit = size != null && size > 0 ? size : 5;
        Pageable pageable = PageRequest.of(0, limit);
        java.util.List<ImportFileLogDTO> logs = exportService.findRecentExportLogs(pageable).stream().map(importFileLogMapper::toDto).toList();
        return ResponseEntity.ok(logs);
    }
}
