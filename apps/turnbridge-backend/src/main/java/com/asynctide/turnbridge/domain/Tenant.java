package com.asynctide.turnbridge.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 租戶主檔：對應加值中心/客戶
 */
@Entity
@Table(name = "tenant")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Tenant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 名稱
     */
    @NotNull
    @Size(max = 128)
    @Column(name = "name", length = 128, nullable = false)
    private String name;

    /**
     * 唯一代碼
     */
    @NotNull
    @Size(max = 32)
    @Column(name = "code", length = 32, nullable = false, unique = true)
    private String code;

    /**
     * 狀態
     */
    @Size(max = 32)
    @Column(name = "status", length = 32)
    private String status;

    /**
     * 匯入檔主檔
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "tenant" }, allowSetters = true)
    private Set<ImportFile> importFiles = new HashSet<>();

    /**
     * 發票主檔
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "importFile", "tenant" }, allowSetters = true)
    private Set<Invoice> invoices = new HashSet<>();

    /**
     * Webhook 端點設定
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "tenant" }, allowSetters = true)
    private Set<WebhookEndpoint> webhookEndpoints = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Tenant id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Tenant name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public Tenant code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return this.status;
    }

    public Tenant status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<ImportFile> getImportFiles() {
        return this.importFiles;
    }

    public void setImportFiles(Set<ImportFile> importFiles) {
        if (this.importFiles != null) {
            this.importFiles.forEach(i -> i.setTenant(null));
        }
        if (importFiles != null) {
            importFiles.forEach(i -> i.setTenant(this));
        }
        this.importFiles = importFiles;
    }

    public Tenant importFiles(Set<ImportFile> importFiles) {
        this.setImportFiles(importFiles);
        return this;
    }

    public Tenant addImportFiles(ImportFile importFile) {
        this.importFiles.add(importFile);
        importFile.setTenant(this);
        return this;
    }

    public Tenant removeImportFiles(ImportFile importFile) {
        this.importFiles.remove(importFile);
        importFile.setTenant(null);
        return this;
    }

    public Set<Invoice> getInvoices() {
        return this.invoices;
    }

    public void setInvoices(Set<Invoice> invoices) {
        if (this.invoices != null) {
            this.invoices.forEach(i -> i.setTenant(null));
        }
        if (invoices != null) {
            invoices.forEach(i -> i.setTenant(this));
        }
        this.invoices = invoices;
    }

    public Tenant invoices(Set<Invoice> invoices) {
        this.setInvoices(invoices);
        return this;
    }

    public Tenant addInvoices(Invoice invoice) {
        this.invoices.add(invoice);
        invoice.setTenant(this);
        return this;
    }

    public Tenant removeInvoices(Invoice invoice) {
        this.invoices.remove(invoice);
        invoice.setTenant(null);
        return this;
    }

    public Set<WebhookEndpoint> getWebhookEndpoints() {
        return this.webhookEndpoints;
    }

    public void setWebhookEndpoints(Set<WebhookEndpoint> webhookEndpoints) {
        if (this.webhookEndpoints != null) {
            this.webhookEndpoints.forEach(i -> i.setTenant(null));
        }
        if (webhookEndpoints != null) {
            webhookEndpoints.forEach(i -> i.setTenant(this));
        }
        this.webhookEndpoints = webhookEndpoints;
    }

    public Tenant webhookEndpoints(Set<WebhookEndpoint> webhookEndpoints) {
        this.setWebhookEndpoints(webhookEndpoints);
        return this;
    }

    public Tenant addWebhookEndpoints(WebhookEndpoint webhookEndpoint) {
        this.webhookEndpoints.add(webhookEndpoint);
        webhookEndpoint.setTenant(this);
        return this;
    }

    public Tenant removeWebhookEndpoints(WebhookEndpoint webhookEndpoint) {
        this.webhookEndpoints.remove(webhookEndpoint);
        webhookEndpoint.setTenant(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tenant)) {
            return false;
        }
        return getId() != null && getId().equals(((Tenant) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tenant{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", code='" + getCode() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
