package com.asynctide.turnbridge.service.turnkey;

import com.asynctide.turnbridge.turnkey.TurnkeyProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 週期性執行 Turnkey XML 匯出。
 */
@Component
public class TurnkeyXmlExportScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(TurnkeyXmlExportScheduler.class);

    private final TurnkeyXmlExportService exportService;
    private final TurnkeyProperties turnkeyProperties;

    public TurnkeyXmlExportScheduler(TurnkeyXmlExportService exportService, TurnkeyProperties turnkeyProperties) {
        this.exportService = exportService;
        this.turnkeyProperties = turnkeyProperties;
    }

    @Scheduled(cron = "${turnbridge.turnkey.export-cron:0 */5 * * * *}")
    public void runScheduledExport() {
        int batchSize = Math.max(1, turnkeyProperties.getExportBatchSize());
        int processed = exportService.exportPendingInvoices(batchSize);
        LOG.debug("Turnkey scheduled export processed {} invoices", processed);
    }
}
