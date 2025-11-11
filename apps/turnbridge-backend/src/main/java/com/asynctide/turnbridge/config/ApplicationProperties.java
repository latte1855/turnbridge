package com.asynctide.turnbridge.config;

import java.time.Duration;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Turnbridge Backend.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();
    
    private final UploadProps uploadProps = new UploadProps();

    // jhipster-needle-application-properties-property

    public Liquibase getLiquibase() {
        return liquibase;
    }
    

    public UploadProps getUploadProps() {
		return uploadProps;
	}

    // jhipster-needle-application-properties-property-getter

	public static class Liquibase {

        private Boolean asyncStart = true;

        public Boolean getAsyncStart() {
            return asyncStart;
        }

        public void setAsyncStart(Boolean asyncStart) {
            this.asyncStart = asyncStart;
        }
    }
    // jhipster-needle-application-properties-property-class
    
    public static class UploadProps {
    	/** 單檔大小上限（Bytes），預設 10MB */
        private long maxSizeBytes = 10L * 1024 * 1024;

        /** 允許的 MIME 類型白名單 */
        private Set<String> allowedMimeTypes = Set.of(
            "text/csv",
            "application/zip",
            "application/x-zip-compressed",
            "application/octet-stream",      // 有些瀏覽器上傳 zip/csv 可能是 octet-stream
            "application/vnd.ms-excel"       // csv 偶爾被標成這個
        );

        /** 允許的附檔名白名單（小寫，不含點） */
        private Set<String> allowedExtensions = Set.of("csv", "zip");

        /** Idempotency-Key 的保存時間（預設 24h） */
        private Duration idempotencyTtl = Duration.ofHours(24);

        // ===== getters/setters =====
        public long getMaxSizeBytes() { return maxSizeBytes; }
        public void setMaxSizeBytes(long maxSizeBytes) { this.maxSizeBytes = maxSizeBytes; }

        public Set<String> getAllowedMimeTypes() { return allowedMimeTypes; }
        public void setAllowedMimeTypes(Set<String> allowedMimeTypes) { this.allowedMimeTypes = allowedMimeTypes; }

        public Set<String> getAllowedExtensions() { return allowedExtensions; }
        public void setAllowedExtensions(Set<String> allowedExtensions) { this.allowedExtensions = allowedExtensions; }

        public Duration getIdempotencyTtl() { return idempotencyTtl; }
        public void setIdempotencyTtl(Duration idempotencyTtl) { this.idempotencyTtl = idempotencyTtl; }
    }
}
