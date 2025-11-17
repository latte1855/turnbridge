package com.asynctide.turnbridge.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.asynctide.turnbridge.domain.ImportFileLog} entity.
 */
@Schema(description = "匯入批次事件紀錄（非逐行）")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ImportFileLogDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 64)
    @Schema(description = "事件代碼（如 PARSE_ERROR/NORMALIZE_SUMMARY）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String eventCode;

    @NotNull
    @Size(max = 16)
    @Schema(description = "層級（INFO/WARN/ERROR）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String level;

    @Size(max = 1024)
    @Schema(description = "訊息摘要")
    private String message;

    @Schema(description = "詳細內容（JSON 或堆疊）")
    @Lob
    private String detail;

    @Schema(description = "發生時間")
    private Instant occurredAt;

    @NotNull
    @Schema(description = "匯入檔主檔")
    private ImportFileDTO importFile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public ImportFileDTO getImportFile() {
        return importFile;
    }

    public void setImportFile(ImportFileDTO importFile) {
        this.importFile = importFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ImportFileLogDTO)) {
            return false;
        }

        ImportFileLogDTO importFileLogDTO = (ImportFileLogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, importFileLogDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ImportFileLogDTO{" +
            "id=" + getId() +
            ", eventCode='" + getEventCode() + "'" +
            ", level='" + getLevel() + "'" +
            ", message='" + getMessage() + "'" +
            ", detail='" + getDetail() + "'" +
            ", occurredAt='" + getOccurredAt() + "'" +
            ", importFile=" + getImportFile() +
            "}";
    }
}
