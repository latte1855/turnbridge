package com.asynctide.turnbridge.service.dto;

import com.asynctide.turnbridge.domain.enumeration.ImportItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.asynctide.turnbridge.domain.ImportFileItem} entity.
 */
@Schema(description = "匯入檔明細（逐行資料，儲存原始欄位與檢核結果）")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ImportFileItemDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    @Schema(description = "行號（1-based）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer lineIndex;

    @Schema(description = "原始資料（JSON 或原始 CSV 行）", requiredMode = Schema.RequiredMode.REQUIRED)
    @Lob
    private String rawData;

    @Size(min = 64, max = 64)
    @Schema(description = "原始資料 SHA-256（利於去重、結果 append）")
    private String rawHash;

    @Size(max = 16)
    @Schema(description = "來源訊息別（A0401/B0401…）")
    private String sourceFamily;

    @Size(max = 16)
    @Schema(description = "目標訊息別（F/G 系）")
    private String normalizedFamily;

    @Schema(description = "正規化 JSON（成功時保留）")
    @Lob
    private String normalizedJson;

    @NotNull
    @Schema(description = "處理狀態", requiredMode = Schema.RequiredMode.REQUIRED)
    private ImportItemStatus status;

    @Size(max = 32)
    @Schema(description = "錯誤碼（失敗時）")
    private String errorCode;

    @Size(max = 1024)
    @Schema(description = "錯誤訊息（失敗時）")
    private String errorMessage;

    @NotNull
    @Schema(description = "匯入檔主檔")
    private ImportFileDTO importFile;

    @Schema(description = "發票主檔")
    private InvoiceDTO invoice;

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

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public String getRawHash() {
        return rawHash;
    }

    public void setRawHash(String rawHash) {
        this.rawHash = rawHash;
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

    public String getNormalizedJson() {
        return normalizedJson;
    }

    public void setNormalizedJson(String normalizedJson) {
        this.normalizedJson = normalizedJson;
    }

    public ImportItemStatus getStatus() {
        return status;
    }

    public void setStatus(ImportItemStatus status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ImportFileDTO getImportFile() {
        return importFile;
    }

    public void setImportFile(ImportFileDTO importFile) {
        this.importFile = importFile;
    }

    public InvoiceDTO getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceDTO invoice) {
        this.invoice = invoice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ImportFileItemDTO)) {
            return false;
        }

        ImportFileItemDTO importFileItemDTO = (ImportFileItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, importFileItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ImportFileItemDTO{" +
            "id=" + getId() +
            ", lineIndex=" + getLineIndex() +
            ", rawData='" + getRawData() + "'" +
            ", rawHash='" + getRawHash() + "'" +
            ", sourceFamily='" + getSourceFamily() + "'" +
            ", normalizedFamily='" + getNormalizedFamily() + "'" +
            ", normalizedJson='" + getNormalizedJson() + "'" +
            ", status='" + getStatus() + "'" +
            ", errorCode='" + getErrorCode() + "'" +
            ", errorMessage='" + getErrorMessage() + "'" +
            ", importFile=" + getImportFile() +
            ", invoice=" + getInvoice() +
            "}";
    }
}
