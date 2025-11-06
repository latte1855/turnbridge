package com.asynctide.turnbridge.service.criteria;

import com.asynctide.turnbridge.domain.enumeration.StoragePurpose;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.asynctide.turnbridge.domain.StoredObject} entity. This class is used
 * in {@link com.asynctide.turnbridge.web.rest.StoredObjectResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /stored-objects?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StoredObjectCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StoragePurpose
     */
    public static class StoragePurposeFilter extends Filter<StoragePurpose> {

        public StoragePurposeFilter() {}

        public StoragePurposeFilter(StoragePurposeFilter filter) {
            super(filter);
        }

        @Override
        public StoragePurposeFilter copy() {
            return new StoragePurposeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter bucket;

    private StringFilter objectKey;

    private StringFilter mediaType;

    private LongFilter contentLength;

    private StringFilter sha256;

    private StoragePurposeFilter purpose;

    private StringFilter filename;

    private StringFilter storageClass;

    private StringFilter encryption;

    private Boolean distinct;

    public StoredObjectCriteria() {}

    public StoredObjectCriteria(StoredObjectCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.bucket = other.optionalBucket().map(StringFilter::copy).orElse(null);
        this.objectKey = other.optionalObjectKey().map(StringFilter::copy).orElse(null);
        this.mediaType = other.optionalMediaType().map(StringFilter::copy).orElse(null);
        this.contentLength = other.optionalContentLength().map(LongFilter::copy).orElse(null);
        this.sha256 = other.optionalSha256().map(StringFilter::copy).orElse(null);
        this.purpose = other.optionalPurpose().map(StoragePurposeFilter::copy).orElse(null);
        this.filename = other.optionalFilename().map(StringFilter::copy).orElse(null);
        this.storageClass = other.optionalStorageClass().map(StringFilter::copy).orElse(null);
        this.encryption = other.optionalEncryption().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public StoredObjectCriteria copy() {
        return new StoredObjectCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getBucket() {
        return bucket;
    }

    public Optional<StringFilter> optionalBucket() {
        return Optional.ofNullable(bucket);
    }

    public StringFilter bucket() {
        if (bucket == null) {
            setBucket(new StringFilter());
        }
        return bucket;
    }

    public void setBucket(StringFilter bucket) {
        this.bucket = bucket;
    }

    public StringFilter getObjectKey() {
        return objectKey;
    }

    public Optional<StringFilter> optionalObjectKey() {
        return Optional.ofNullable(objectKey);
    }

    public StringFilter objectKey() {
        if (objectKey == null) {
            setObjectKey(new StringFilter());
        }
        return objectKey;
    }

    public void setObjectKey(StringFilter objectKey) {
        this.objectKey = objectKey;
    }

    public StringFilter getMediaType() {
        return mediaType;
    }

    public Optional<StringFilter> optionalMediaType() {
        return Optional.ofNullable(mediaType);
    }

    public StringFilter mediaType() {
        if (mediaType == null) {
            setMediaType(new StringFilter());
        }
        return mediaType;
    }

    public void setMediaType(StringFilter mediaType) {
        this.mediaType = mediaType;
    }

    public LongFilter getContentLength() {
        return contentLength;
    }

    public Optional<LongFilter> optionalContentLength() {
        return Optional.ofNullable(contentLength);
    }

    public LongFilter contentLength() {
        if (contentLength == null) {
            setContentLength(new LongFilter());
        }
        return contentLength;
    }

    public void setContentLength(LongFilter contentLength) {
        this.contentLength = contentLength;
    }

    public StringFilter getSha256() {
        return sha256;
    }

    public Optional<StringFilter> optionalSha256() {
        return Optional.ofNullable(sha256);
    }

    public StringFilter sha256() {
        if (sha256 == null) {
            setSha256(new StringFilter());
        }
        return sha256;
    }

    public void setSha256(StringFilter sha256) {
        this.sha256 = sha256;
    }

    public StoragePurposeFilter getPurpose() {
        return purpose;
    }

    public Optional<StoragePurposeFilter> optionalPurpose() {
        return Optional.ofNullable(purpose);
    }

    public StoragePurposeFilter purpose() {
        if (purpose == null) {
            setPurpose(new StoragePurposeFilter());
        }
        return purpose;
    }

    public void setPurpose(StoragePurposeFilter purpose) {
        this.purpose = purpose;
    }

    public StringFilter getFilename() {
        return filename;
    }

    public Optional<StringFilter> optionalFilename() {
        return Optional.ofNullable(filename);
    }

    public StringFilter filename() {
        if (filename == null) {
            setFilename(new StringFilter());
        }
        return filename;
    }

    public void setFilename(StringFilter filename) {
        this.filename = filename;
    }

    public StringFilter getStorageClass() {
        return storageClass;
    }

    public Optional<StringFilter> optionalStorageClass() {
        return Optional.ofNullable(storageClass);
    }

    public StringFilter storageClass() {
        if (storageClass == null) {
            setStorageClass(new StringFilter());
        }
        return storageClass;
    }

    public void setStorageClass(StringFilter storageClass) {
        this.storageClass = storageClass;
    }

    public StringFilter getEncryption() {
        return encryption;
    }

    public Optional<StringFilter> optionalEncryption() {
        return Optional.ofNullable(encryption);
    }

    public StringFilter encryption() {
        if (encryption == null) {
            setEncryption(new StringFilter());
        }
        return encryption;
    }

    public void setEncryption(StringFilter encryption) {
        this.encryption = encryption;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final StoredObjectCriteria that = (StoredObjectCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(bucket, that.bucket) &&
            Objects.equals(objectKey, that.objectKey) &&
            Objects.equals(mediaType, that.mediaType) &&
            Objects.equals(contentLength, that.contentLength) &&
            Objects.equals(sha256, that.sha256) &&
            Objects.equals(purpose, that.purpose) &&
            Objects.equals(filename, that.filename) &&
            Objects.equals(storageClass, that.storageClass) &&
            Objects.equals(encryption, that.encryption) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bucket, objectKey, mediaType, contentLength, sha256, purpose, filename, storageClass, encryption, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StoredObjectCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalBucket().map(f -> "bucket=" + f + ", ").orElse("") +
            optionalObjectKey().map(f -> "objectKey=" + f + ", ").orElse("") +
            optionalMediaType().map(f -> "mediaType=" + f + ", ").orElse("") +
            optionalContentLength().map(f -> "contentLength=" + f + ", ").orElse("") +
            optionalSha256().map(f -> "sha256=" + f + ", ").orElse("") +
            optionalPurpose().map(f -> "purpose=" + f + ", ").orElse("") +
            optionalFilename().map(f -> "filename=" + f + ", ").orElse("") +
            optionalStorageClass().map(f -> "storageClass=" + f + ", ").orElse("") +
            optionalEncryption().map(f -> "encryption=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
