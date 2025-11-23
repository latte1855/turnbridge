package com.asynctide.turnbridge.turnkey;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Turnkey 相關設定（INBOX 目錄、檔名規則等）。
 */
@ConfigurationProperties(prefix = "turnbridge.turnkey", ignoreUnknownFields = false)
public class TurnkeyProperties {

    /** 產生 XML 後放置的目錄（預設為專案內 target/turnkey/INBOX）。 */
    private String inboxDir = "target/turnkey/INBOX";
    /** XML 檔名前綴，預設 FGPAYLOAD。 */
    private String filenamePrefix = "FGPAYLOAD";
    /** 產檔時是否加上租戶代碼作為子目錄。 */
    private boolean tenantSubDirectory = true;
    /** Turnkey B2SSTORAGE SRC 基底路徑；若為空則不執行搬移。 */
    private String b2sStorageSrcBase = "";
    /** 匯出排程 Cron。 */
    private String exportCron = "0 */5 * * * *";
    /** 單次匯出批次大小。 */
    private int exportBatchSize = 200;
    /** Turnkey pickup 監控排程。 */
    private String pickupMonitorCron = "30 */5 * * * *";
    /** 允許檔案停留 SRC 的最長分鐘數。 */
    private long pickupMaxAgeMinutes = 10;
    /** 允許 Pack 目錄檔案停留的最長分鐘數。 */
    private long packMaxAgeMinutes = 10;
    /** ProcessResult 目錄基底（Unpack/B2SSTORAGE）。 */
    private String processResultBase = "";
    /** ProcessResult 掃描排程。 */
    private String processResultCron = "15 */5 * * * *";

    public String getInboxDir() {
        return inboxDir;
    }

    public void setInboxDir(String inboxDir) {
        this.inboxDir = inboxDir;
    }

    public String getFilenamePrefix() {
        return filenamePrefix;
    }

    public void setFilenamePrefix(String filenamePrefix) {
        this.filenamePrefix = filenamePrefix;
    }

    public boolean isTenantSubDirectory() {
        return tenantSubDirectory;
    }

    public void setTenantSubDirectory(boolean tenantSubDirectory) {
        this.tenantSubDirectory = tenantSubDirectory;
    }

    public String getB2sStorageSrcBase() {
        return b2sStorageSrcBase;
    }

    public void setB2sStorageSrcBase(String b2sStorageSrcBase) {
        this.b2sStorageSrcBase = b2sStorageSrcBase;
    }

    public String getExportCron() {
        return exportCron;
    }

    public void setExportCron(String exportCron) {
        this.exportCron = exportCron;
    }

    public int getExportBatchSize() {
        return exportBatchSize;
    }

    public void setExportBatchSize(int exportBatchSize) {
        this.exportBatchSize = exportBatchSize;
    }

    public String getPickupMonitorCron() {
        return pickupMonitorCron;
    }

    public void setPickupMonitorCron(String pickupMonitorCron) {
        this.pickupMonitorCron = pickupMonitorCron;
    }

    public long getPickupMaxAgeMinutes() {
        return pickupMaxAgeMinutes;
    }

    public void setPickupMaxAgeMinutes(long pickupMaxAgeMinutes) {
        this.pickupMaxAgeMinutes = pickupMaxAgeMinutes;
    }

    public long getPackMaxAgeMinutes() {
        return packMaxAgeMinutes;
    }

    public void setPackMaxAgeMinutes(long packMaxAgeMinutes) {
        this.packMaxAgeMinutes = packMaxAgeMinutes;
    }

    public String getProcessResultBase() {
        return processResultBase;
    }

    public void setProcessResultBase(String processResultBase) {
        this.processResultBase = processResultBase;
    }

    public String getProcessResultCron() {
        return processResultCron;
    }

    public void setProcessResultCron(String processResultCron) {
        this.processResultCron = processResultCron;
    }
}
