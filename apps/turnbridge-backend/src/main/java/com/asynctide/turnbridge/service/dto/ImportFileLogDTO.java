package com.asynctide.turnbridge.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.asynctide.turnbridge.domain.ImportFileLog} entity.
 */
@Schema(description = "匯入錯誤/訊息紀錄")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ImportFileLogDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    @Schema(description = "行號", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer lineIndex;

    @Size(max = 64)
    @Schema(description = "欄位名稱")
    private String field;

    @NotNull
    @Size(max = 32)
    @Schema(description = "錯誤碼", requiredMode = Schema.RequiredMode.REQUIRED)
    private String errorCode;

    @Size(max = 1024)
    @Schema(description = "錯誤訊息")
    private String message;

    @Schema(description = "原始行")
    @Lob
    private String rawLine;

    @Size(max = 16)
    @Schema(description = "原訊息別")
    private String sourceFamily;

    @Size(max = 16)
    @Schema(description = "正規化訊息別")
    private String normalizedFamily;

    @NotNull
    @Schema(description = "匯入檔主檔")
    private ImportFileDTO importFile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLineIndex() {
        return lineIndex;
    }

    public void setLineIndex(Integer lineIndex) {
        this.lineIndex = lineIndex;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
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

    public String getRawLine() {
        return rawLine;
    }

    public void setRawLine(String rawLine) {
        this.rawLine = rawLine;
    }

    public String getSourceFamily() {
        return sourceFamily;
    }

    public void setSourceFamily(String sourceFamily) {
        this.sourceFamily = sourceFamily;
    }

    public String getNormalizedFamily() {
        return normalizedFamily;
    }

    public void setNormalizedFamily(String normalizedFamily) {
        this.normalizedFamily = normalizedFamily;
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
            ", lineIndex=" + getLineIndex() +
            ", field='" + getField() + "'" +
            ", errorCode='" + getErrorCode() + "'" +
            ", message='" + getMessage() + "'" +
            ", rawLine='" + getRawLine() + "'" +
            ", sourceFamily='" + getSourceFamily() + "'" +
            ", normalizedFamily='" + getNormalizedFamily() + "'" +
            ", importFile=" + getImportFile() +
            "}";
    }
}
