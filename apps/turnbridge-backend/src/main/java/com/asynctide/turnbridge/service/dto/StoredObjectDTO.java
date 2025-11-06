package com.asynctide.turnbridge.service.dto;

import com.asynctide.turnbridge.domain.enumeration.StoragePurpose;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.asynctide.turnbridge.domain.StoredObject} entity.
 */
@Schema(description = "儲存物件（StoredObject）\n存放檔案中繼資料；位元檔實際存於檔案系統/MinIO/S3。")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StoredObjectDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 64)
    @Schema(description = "儲存桶（或邏輯名稱）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bucket;

    @NotNull
    @Size(max = 512)
    @Schema(description = "物件鍵（路徑+檔名）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String objectKey;

    @NotNull
    @Size(max = 128)
    @Schema(description = "MIME 類型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String mediaType;

    @NotNull
    @Min(value = 0L)
    @Schema(description = "內容長度（bytes；避免保留字 size，改名 contentLength）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long contentLength;

    @NotNull
    @Size(min = 64, max = 64)
    @Schema(description = "內容 SHA-256", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sha256;

    @NotNull
    @Schema(description = "用途（原檔/結果/MIG/回饋）", requiredMode = Schema.RequiredMode.REQUIRED)
    private StoragePurpose purpose;

    @Size(max = 255)
    @Schema(description = "原始檔名（未必等於 objectKey 最後片段）")
    private String filename;

    @Size(max = 32)
    @Schema(description = "儲存等級（STANDARD/GLACIER…；自由字串）")
    private String storageClass;

    @Size(max = 32)
    @Schema(description = "加密演算法（如 AES256 / aws:kms；自由字串）")
    private String encryption;

    @Schema(description = "其他中繼資料（JSON）")
    @Lob
    private String metadata;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public StoragePurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(StoragePurpose purpose) {
        this.purpose = purpose;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getStorageClass() {
        return storageClass;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    public String getEncryption() {
        return encryption;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StoredObjectDTO)) {
            return false;
        }

        StoredObjectDTO storedObjectDTO = (StoredObjectDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, storedObjectDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StoredObjectDTO{" +
            "id=" + getId() +
            ", bucket='" + getBucket() + "'" +
            ", objectKey='" + getObjectKey() + "'" +
            ", mediaType='" + getMediaType() + "'" +
            ", contentLength=" + getContentLength() +
            ", sha256='" + getSha256() + "'" +
            ", purpose='" + getPurpose() + "'" +
            ", filename='" + getFilename() + "'" +
            ", storageClass='" + getStorageClass() + "'" +
            ", encryption='" + getEncryption() + "'" +
            ", metadata='" + getMetadata() + "'" +
            "}";
    }
}
