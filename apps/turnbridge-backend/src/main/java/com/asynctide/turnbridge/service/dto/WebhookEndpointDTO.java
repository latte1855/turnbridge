package com.asynctide.turnbridge.service.dto;

import com.asynctide.turnbridge.domain.enumeration.WebhookStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.asynctide.turnbridge.domain.WebhookEndpoint} entity.
 */
@Schema(description = "Webhook 端點設定")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WebhookEndpointDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 64)
    @Schema(description = "名稱", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull
    @Size(max = 512)
    @Schema(description = "目標 URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String targetUrl;

    @Size(max = 128)
    @Schema(description = "Secret")
    private String secret;

    @NotNull
    @Size(max = 512)
    @Schema(description = "訂閱事件", requiredMode = Schema.RequiredMode.REQUIRED)
    private String events;

    @NotNull
    @Schema(description = "狀態", requiredMode = Schema.RequiredMode.REQUIRED)
    private WebhookStatus status;

    @Schema(description = "租戶")
    private TenantDTO tenant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getEvents() {
        return events;
    }

    public void setEvents(String events) {
        this.events = events;
    }

    public WebhookStatus getStatus() {
        return status;
    }

    public void setStatus(WebhookStatus status) {
        this.status = status;
    }

    public TenantDTO getTenant() {
        return tenant;
    }

    public void setTenant(TenantDTO tenant) {
        this.tenant = tenant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WebhookEndpointDTO)) {
            return false;
        }

        WebhookEndpointDTO webhookEndpointDTO = (WebhookEndpointDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, webhookEndpointDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WebhookEndpointDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", targetUrl='" + getTargetUrl() + "'" +
            ", secret='" + getSecret() + "'" +
            ", events='" + getEvents() + "'" +
            ", status='" + getStatus() + "'" +
            ", tenant=" + getTenant() +
            "}";
    }
}
