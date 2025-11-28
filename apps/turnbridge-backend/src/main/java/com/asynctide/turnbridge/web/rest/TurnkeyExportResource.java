package com.asynctide.turnbridge.web.rest;

import com.asynctide.turnbridge.security.AuthoritiesConstants;
import com.asynctide.turnbridge.service.dto.ImportFileLogDTO;
import com.asynctide.turnbridge.service.mapper.ImportFileLogMapper;
import com.asynctide.turnbridge.service.turnkey.TurnkeyXmlExportService;
import com.asynctide.turnbridge.turnkey.TurnkeyProperties;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

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
    public ResponseEntity<List<ImportFileLogDTO>> getRecentExportLogs(
        @RequestParam(name = "page", required = false) Integer page,
        @RequestParam(name = "size", required = false) Integer size,
        @RequestParam(name = "event", required = false) List<String> eventCodes
    ) {
        int pageNumber = page != null && page >= 0 ? page : 0;
        int pageSize = size != null && size > 0 ? size : 10;
        Page<ImportFileLogDTO> result =
            exportService
                .findExportLogs(eventCodes, PageRequest.of(pageNumber, pageSize))
                .map(importFileLogMapper::toDto);
        return ResponseEntity
            .ok()
            .headers(PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), result))
            .body(result.getContent());
    }
}
