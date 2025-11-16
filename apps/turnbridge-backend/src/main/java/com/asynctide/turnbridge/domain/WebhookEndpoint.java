package com.asynctide.turnbridge.domain;

import com.asynctide.turnbridge.domain.enumeration.WebhookStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Webhook 端點設定
 */
@Entity
@Table(name = "webhook_endpoint")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WebhookEndpoint implements Serializable {

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
    @Size(max = 64)
    @Column(name = "name", length = 64, nullable = false)
    private String name;

    /**
     * 目標 URL
     */
    @NotNull
    @Size(max = 512)
    @Column(name = "target_url", length = 512, nullable = false)
    private String targetUrl;

    /**
     * Secret
     */
    @Size(max = 128)
    @Column(name = "secret", length = 128)
    private String secret;

    /**
     * 訂閱事件
     */
    @NotNull
    @Size(max = 512)
    @Column(name = "events", length = 512, nullable = false)
    private String events;

    /**
     * 狀態
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WebhookStatus status;

    /**
     * 租戶
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "importFiles", "invoices", "webhookEndpoints" }, allowSetters = true)
    private Tenant tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WebhookEndpoint id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public WebhookEndpoint name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTargetUrl() {
        return this.targetUrl;
    }

    public WebhookEndpoint targetUrl(String targetUrl) {
        this.setTargetUrl(targetUrl);
        return this;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getSecret() {
        return this.secret;
    }

    public WebhookEndpoint secret(String secret) {
        this.setSecret(secret);
        return this;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getEvents() {
        return this.events;
    }

    public WebhookEndpoint events(String events) {
        this.setEvents(events);
        return this;
    }

    public void setEvents(String events) {
        this.events = events;
    }

    public WebhookStatus getStatus() {
        return this.status;
    }

    public WebhookEndpoint status(WebhookStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(WebhookStatus status) {
        this.status = status;
    }

    public Tenant getTenant() {
        return this.tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public WebhookEndpoint tenant(Tenant tenant) {
        this.setTenant(tenant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WebhookEndpoint)) {
            return false;
        }
        return getId() != null && getId().equals(((WebhookEndpoint) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WebhookEndpoint{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", targetUrl='" + getTargetUrl() + "'" +
            ", secret='" + getSecret() + "'" +
            ", events='" + getEvents() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
