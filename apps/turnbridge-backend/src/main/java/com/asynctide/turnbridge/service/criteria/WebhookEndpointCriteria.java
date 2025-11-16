package com.asynctide.turnbridge.service.criteria;

import com.asynctide.turnbridge.domain.enumeration.WebhookStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.asynctide.turnbridge.domain.WebhookEndpoint} entity. This class is used
 * in {@link com.asynctide.turnbridge.web.rest.WebhookEndpointResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /webhook-endpoints?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WebhookEndpointCriteria implements Serializable, Criteria {

    /**
     * Class for filtering WebhookStatus
     */
    public static class WebhookStatusFilter extends Filter<WebhookStatus> {

        public WebhookStatusFilter() {}

        public WebhookStatusFilter(WebhookStatusFilter filter) {
            super(filter);
        }

        @Override
        public WebhookStatusFilter copy() {
            return new WebhookStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter targetUrl;

    private StringFilter secret;

    private StringFilter events;

    private WebhookStatusFilter status;

    private LongFilter tenantId;

    private Boolean distinct;

    public WebhookEndpointCriteria() {}

    public WebhookEndpointCriteria(WebhookEndpointCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.targetUrl = other.optionalTargetUrl().map(StringFilter::copy).orElse(null);
        this.secret = other.optionalSecret().map(StringFilter::copy).orElse(null);
        this.events = other.optionalEvents().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(WebhookStatusFilter::copy).orElse(null);
        this.tenantId = other.optionalTenantId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public WebhookEndpointCriteria copy() {
        return new WebhookEndpointCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getTargetUrl() {
        return targetUrl;
    }

    public Optional<StringFilter> optionalTargetUrl() {
        return Optional.ofNullable(targetUrl);
    }

    public StringFilter targetUrl() {
        if (targetUrl == null) {
            setTargetUrl(new StringFilter());
        }
        return targetUrl;
    }

    public void setTargetUrl(StringFilter targetUrl) {
        this.targetUrl = targetUrl;
    }

    public StringFilter getSecret() {
        return secret;
    }

    public Optional<StringFilter> optionalSecret() {
        return Optional.ofNullable(secret);
    }

    public StringFilter secret() {
        if (secret == null) {
            setSecret(new StringFilter());
        }
        return secret;
    }

    public void setSecret(StringFilter secret) {
        this.secret = secret;
    }

    public StringFilter getEvents() {
        return events;
    }

    public Optional<StringFilter> optionalEvents() {
        return Optional.ofNullable(events);
    }

    public StringFilter events() {
        if (events == null) {
            setEvents(new StringFilter());
        }
        return events;
    }

    public void setEvents(StringFilter events) {
        this.events = events;
    }

    public WebhookStatusFilter getStatus() {
        return status;
    }

    public Optional<WebhookStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public WebhookStatusFilter status() {
        if (status == null) {
            setStatus(new WebhookStatusFilter());
        }
        return status;
    }

    public void setStatus(WebhookStatusFilter status) {
        this.status = status;
    }

    public LongFilter getTenantId() {
        return tenantId;
    }

    public Optional<LongFilter> optionalTenantId() {
        return Optional.ofNullable(tenantId);
    }

    public LongFilter tenantId() {
        if (tenantId == null) {
            setTenantId(new LongFilter());
        }
        return tenantId;
    }

    public void setTenantId(LongFilter tenantId) {
        this.tenantId = tenantId;
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
        final WebhookEndpointCriteria that = (WebhookEndpointCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(targetUrl, that.targetUrl) &&
            Objects.equals(secret, that.secret) &&
            Objects.equals(events, that.events) &&
            Objects.equals(status, that.status) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, targetUrl, secret, events, status, tenantId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WebhookEndpointCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalTargetUrl().map(f -> "targetUrl=" + f + ", ").orElse("") +
            optionalSecret().map(f -> "secret=" + f + ", ").orElse("") +
            optionalEvents().map(f -> "events=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalTenantId().map(f -> "tenantId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
