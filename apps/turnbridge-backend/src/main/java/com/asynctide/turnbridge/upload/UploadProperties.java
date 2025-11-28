package com.asynctide.turnbridge.upload;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Turnbridge 上傳相關設定。
 */
@ConfigurationProperties(prefix = "turnbridge.upload", ignoreUnknownFields = false)
public class UploadProperties {

    /** 備份上傳檔案的根目錄（若為空則不備份）。 */
    private String backupDir = "";

    public String getBackupDir() {
        return backupDir;
    }

    public void setBackupDir(String backupDir) {
        this.backupDir = backupDir;
    }
}
