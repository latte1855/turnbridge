package com.asynctide.turnbridge.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.asynctide.turnbridge.domain.ImportFileItemError} entity.
 */
@Schema(description = "匯入檔明細欄位錯誤（供 UI 詳細檢視）")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ImportFileItemErrorDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    @Schema(description = "欄位號（1-based）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer columnIndex;

    @NotNull
    @Size(max = 128)
    @Schema(description = "欄位名稱（如 BuyerId/Amount）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fieldName;

    @NotNull
    @Size(max = 64)
    @Schema(description = "錯誤碼（LENGTH_INVALID、NUMBER_FORMAT 等）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String errorCode;

    @Size(max = 1024)
    @Schema(description = "錯誤訊息")
    private String message;

    @Size(max = 16)
    @Schema(description = "嚴重度（INFO/WARN/ERROR）")
    private String severity;

    @Schema(description = "發生時間（可選）")
    private Instant occurredAt;

    @NotNull
    @Schema(description = "匯入檔明細")
    private ImportFileItemDTO importFileItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(Integer columnIndex) {
        this.columnIndex = columnIndex;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public ImportFileItemDTO getImportFileItem() {
        return importFileItem;
    }

    public void setImportFileItem(ImportFileItemDTO importFileItem) {
        this.importFileItem = importFileItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ImportFileItemErrorDTO)) {
            return false;
        }

        ImportFileItemErrorDTO importFileItemErrorDTO = (ImportFileItemErrorDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, importFileItemErrorDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ImportFileItemErrorDTO{" +
            "id=" + getId() +
            ", columnIndex=" + getColumnIndex() +
            ", fieldName='" + getFieldName() + "'" +
            ", errorCode='" + getErrorCode() + "'" +
            ", message='" + getMessage() + "'" +
            ", severity='" + getSeverity() + "'" +
            ", occurredAt='" + getOccurredAt() + "'" +
            ", importFileItem=" + getImportFileItem() +
            "}";
    }
}
