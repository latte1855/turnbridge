package com.asynctide.turnbridge.domain;

import com.asynctide.turnbridge.domain.enumeration.UploadJobStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 上傳批次主檔（UploadJob）
 * 追蹤每次上傳的狀態、統計、來源檔資訊與回饋檔關聯。
 */
@Entity
@Table(name = "upload_job")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UploadJob extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 批次識別碼（系統產生，對外曝光）；唯一
     */
    @NotNull
    @Size(min = 8, max = 64)
    @Column(name = "job_id", length = 64, nullable = false, unique = true)
    private String jobId;

    /**
     * 賣方統編或客戶識別碼（上傳端提供）
     */
    @NotNull
    @Size(min = 3, max = 32)
    @Column(name = "seller_id", length = 32, nullable = false)
    private String sellerId;

    /**
     * 賣方名稱（可選；來自客戶資料或 CSV 抬頭）
     */
    @Size(max = 128)
    @Column(name = "seller_name", length = 128)
    private String sellerName;

    /**
     * 期別 YYYYMM（雙月分區用；可選）
     */
    @Pattern(regexp = "[0-9]{6}")
    @Column(name = "period")
    private String period;

    /**
     * 解析 Profile 名稱（Legacy/Canonical 或其他）
     */
    @Size(min = 3, max = 64)
    @Column(name = "profile", length = 64)
    private String profile;

    /**
     * 原始上傳檔案名稱（僅中繼資訊，實體位於 StoredObject）
     */
    @Size(max = 255)
    @Column(name = "source_filename", length = 255)
    private String sourceFilename;

    /**
     * 原始上傳檔案 MIME 類型（text/csv、application/zip…）
     */
    @Size(max = 128)
    @Column(name = "source_media_type", length = 128)
    private String sourceMediaType;

    /**
     * 批次處理狀態
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UploadJobStatus status;

    /**
     * 批次總筆數
     */
    @NotNull
    @Min(value = 0)
    @Column(name = "total", nullable = false)
    private Integer total;

    /**
     * 驗證通過數
     */
    @NotNull
    @Min(value = 0)
    @Column(name = "accepted", nullable = false)
    private Integer accepted;

    /**
     * 驗證失敗數
     */
    @NotNull
    @Min(value = 0)
    @Column(name = "failed", nullable = false)
    private Integer failed;

    /**
     * 已送 Turnkey 數
     */
    @NotNull
    @Min(value = 0)
    @Column(name = "sent", nullable = false)
    private Integer sent;

    /**
     * 備註（營運人員補充或系統說明）
     */
    @Size(max = 1024)
    @Column(name = "remark", length = 1024)
    private String remark;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "job")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "job" }, allowSetters = true)
    private Set<UploadJobItem> items = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    private StoredObject originalFile;

    @ManyToOne(fetch = FetchType.LAZY)
    private StoredObject resultFile;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UploadJob id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobId() {
        return this.jobId;
    }

    public UploadJob jobId(String jobId) {
        this.setJobId(jobId);
        return this;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getSellerId() {
        return this.sellerId;
    }

    public UploadJob sellerId(String sellerId) {
        this.setSellerId(sellerId);
        return this;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return this.sellerName;
    }

    public UploadJob sellerName(String sellerName) {
        this.setSellerName(sellerName);
        return this;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getPeriod() {
        return this.period;
    }

    public UploadJob period(String period) {
        this.setPeriod(period);
        return this;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getProfile() {
        return this.profile;
    }

    public UploadJob profile(String profile) {
        this.setProfile(profile);
        return this;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getSourceFilename() {
        return this.sourceFilename;
    }

    public UploadJob sourceFilename(String sourceFilename) {
        this.setSourceFilename(sourceFilename);
        return this;
    }

    public void setSourceFilename(String sourceFilename) {
        this.sourceFilename = sourceFilename;
    }

    public String getSourceMediaType() {
        return this.sourceMediaType;
    }

    public UploadJob sourceMediaType(String sourceMediaType) {
        this.setSourceMediaType(sourceMediaType);
        return this;
    }

    public void setSourceMediaType(String sourceMediaType) {
        this.sourceMediaType = sourceMediaType;
    }

    public UploadJobStatus getStatus() {
        return this.status;
    }

    public UploadJob status(UploadJobStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(UploadJobStatus status) {
        this.status = status;
    }

    public Integer getTotal() {
        return this.total;
    }

    public UploadJob total(Integer total) {
        this.setTotal(total);
        return this;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getAccepted() {
        return this.accepted;
    }

    public UploadJob accepted(Integer accepted) {
        this.setAccepted(accepted);
        return this;
    }

    public void setAccepted(Integer accepted) {
        this.accepted = accepted;
    }

    public Integer getFailed() {
        return this.failed;
    }

    public UploadJob failed(Integer failed) {
        this.setFailed(failed);
        return this;
    }

    public void setFailed(Integer failed) {
        this.failed = failed;
    }

    public Integer getSent() {
        return this.sent;
    }

    public UploadJob sent(Integer sent) {
        this.setSent(sent);
        return this;
    }

    public void setSent(Integer sent) {
        this.sent = sent;
    }

    public String getRemark() {
        return this.remark;
    }

    public UploadJob remark(String remark) {
        this.setRemark(remark);
        return this;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Set<UploadJobItem> getItems() {
        return this.items;
    }

    public void setItems(Set<UploadJobItem> uploadJobItems) {
        if (this.items != null) {
            this.items.forEach(i -> i.setJob(null));
        }
        if (uploadJobItems != null) {
            uploadJobItems.forEach(i -> i.setJob(this));
        }
        this.items = uploadJobItems;
    }

    public UploadJob items(Set<UploadJobItem> uploadJobItems) {
        this.setItems(uploadJobItems);
        return this;
    }

    public UploadJob addItems(UploadJobItem uploadJobItem) {
        this.items.add(uploadJobItem);
        uploadJobItem.setJob(this);
        return this;
    }

    public UploadJob removeItems(UploadJobItem uploadJobItem) {
        this.items.remove(uploadJobItem);
        uploadJobItem.setJob(null);
        return this;
    }

    public StoredObject getOriginalFile() {
        return this.originalFile;
    }

    public void setOriginalFile(StoredObject storedObject) {
        this.originalFile = storedObject;
    }

    public UploadJob originalFile(StoredObject storedObject) {
        this.setOriginalFile(storedObject);
        return this;
    }

    public StoredObject getResultFile() {
        return this.resultFile;
    }

    public void setResultFile(StoredObject storedObject) {
        this.resultFile = storedObject;
    }

    public UploadJob resultFile(StoredObject storedObject) {
        this.setResultFile(storedObject);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UploadJob)) {
            return false;
        }
        return getId() != null && getId().equals(((UploadJob) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UploadJob{" +
            "id=" + getId() +
            ", jobId='" + getJobId() + "'" +
            ", sellerId='" + getSellerId() + "'" +
            ", sellerName='" + getSellerName() + "'" +
            ", period='" + getPeriod() + "'" +
            ", profile='" + getProfile() + "'" +
            ", sourceFilename='" + getSourceFilename() + "'" +
            ", sourceMediaType='" + getSourceMediaType() + "'" +
            ", status='" + getStatus() + "'" +
            ", total=" + getTotal() +
            ", accepted=" + getAccepted() +
            ", failed=" + getFailed() +
            ", sent=" + getSent() +
            ", remark='" + getRemark() + "'" +
            "}";
    }
}
