package com.asynctide.turnbridge.domain;

import com.asynctide.turnbridge.domain.enumeration.StoragePurpose;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 儲存物件（StoredObject）
 * 存放檔案中繼資料；位元檔實際存於檔案系統/MinIO/S3。
 */
@Entity
@Table(name = "stored_object")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StoredObject extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 儲存桶（或邏輯名稱）
     */
    @NotNull
    @Size(max = 64)
    @Column(name = "bucket", length = 64, nullable = false)
    private String bucket;

    /**
     * 物件鍵（路徑+檔名）
     */
    @NotNull
    @Size(max = 512)
    @Column(name = "object_key", length = 512, nullable = false)
    private String objectKey;

    /**
     * MIME 類型
     */
    @NotNull
    @Size(max = 128)
    @Column(name = "media_type", length = 128, nullable = false)
    private String mediaType;

    /**
     * 內容長度（bytes；避免保留字 size，改名 contentLength）
     */
    @NotNull
    @Min(value = 0L)
    @Column(name = "content_length", nullable = false)
    private Long contentLength;

    /**
     * 內容 SHA-256
     */
    @NotNull
    @Size(min = 64, max = 64)
    @Column(name = "sha_256", length = 64, nullable = false)
    private String sha256;

    /**
     * 用途（原檔/結果/MIG/回饋）
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false)
    private StoragePurpose purpose;

    /**
     * 原始檔名（未必等於 objectKey 最後片段）
     */
    @Size(max = 255)
    @Column(name = "filename", length = 255)
    private String filename;

    /**
     * 儲存等級（STANDARD/GLACIER…；自由字串）
     */
    @Size(max = 32)
    @Column(name = "storage_class", length = 32)
    private String storageClass;

    /**
     * 加密演算法（如 AES256 / aws:kms；自由字串）
     */
    @Size(max = 32)
    @Column(name = "encryption", length = 32)
    private String encryption;

    /**
     * 其他中繼資料（JSON）
     */
    // PostgreSQL 沒有 CLOB，避免 @Lob 走 getClob 路
    //@Lob
    @Column(name = "metadata")
    private String metadata;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StoredObject id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBucket() {
        return this.bucket;
    }

    public StoredObject bucket(String bucket) {
        this.setBucket(bucket);
        return this;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getObjectKey() {
        return this.objectKey;
    }

    public StoredObject objectKey(String objectKey) {
        this.setObjectKey(objectKey);
        return this;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getMediaType() {
        return this.mediaType;
    }

    public StoredObject mediaType(String mediaType) {
        this.setMediaType(mediaType);
        return this;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Long getContentLength() {
        return this.contentLength;
    }

    public StoredObject contentLength(Long contentLength) {
        this.setContentLength(contentLength);
        return this;
    }

    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    public String getSha256() {
        return this.sha256;
    }

    public StoredObject sha256(String sha256) {
        this.setSha256(sha256);
        return this;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public StoragePurpose getPurpose() {
        return this.purpose;
    }

    public StoredObject purpose(StoragePurpose purpose) {
        this.setPurpose(purpose);
        return this;
    }

    public void setPurpose(StoragePurpose purpose) {
        this.purpose = purpose;
    }

    public String getFilename() {
        return this.filename;
    }

    public StoredObject filename(String filename) {
        this.setFilename(filename);
        return this;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getStorageClass() {
        return this.storageClass;
    }

    public StoredObject storageClass(String storageClass) {
        this.setStorageClass(storageClass);
        return this;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    public String getEncryption() {
        return this.encryption;
    }

    public StoredObject encryption(String encryption) {
        this.setEncryption(encryption);
        return this;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    public String getMetadata() {
        return this.metadata;
    }

    public StoredObject metadata(String metadata) {
        this.setMetadata(metadata);
        return this;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StoredObject)) {
            return false;
        }
        return getId() != null && getId().equals(((StoredObject) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StoredObject{" +
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
