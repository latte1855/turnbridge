package com.asynctide.turnbridge.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class WebhookDeliveryLogCriteriaTest {

    @Test
    void newWebhookDeliveryLogCriteriaHasAllFiltersNullTest() {
        var webhookDeliveryLogCriteria = new WebhookDeliveryLogCriteria();
        assertThat(webhookDeliveryLogCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void webhookDeliveryLogCriteriaFluentMethodsCreatesFiltersTest() {
        var webhookDeliveryLogCriteria = new WebhookDeliveryLogCriteria();

        setAllFilters(webhookDeliveryLogCriteria);

        assertThat(webhookDeliveryLogCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void webhookDeliveryLogCriteriaCopyCreatesNullFilterTest() {
        var webhookDeliveryLogCriteria = new WebhookDeliveryLogCriteria();
        var copy = webhookDeliveryLogCriteria.copy();

        assertThat(webhookDeliveryLogCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(webhookDeliveryLogCriteria)
        );
    }

    @Test
    void webhookDeliveryLogCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var webhookDeliveryLogCriteria = new WebhookDeliveryLogCriteria();
        setAllFilters(webhookDeliveryLogCriteria);

        var copy = webhookDeliveryLogCriteria.copy();

        assertThat(webhookDeliveryLogCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(webhookDeliveryLogCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var webhookDeliveryLogCriteria = new WebhookDeliveryLogCriteria();

        assertThat(webhookDeliveryLogCriteria).hasToString("WebhookDeliveryLogCriteria{}");
    }

    private static void setAllFilters(WebhookDeliveryLogCriteria webhookDeliveryLogCriteria) {
        webhookDeliveryLogCriteria.id();
        webhookDeliveryLogCriteria.deliveryId();
        webhookDeliveryLogCriteria.event();
        webhookDeliveryLogCriteria.status();
        webhookDeliveryLogCriteria.httpStatus();
        webhookDeliveryLogCriteria.attempts();
        webhookDeliveryLogCriteria.lastError();
        webhookDeliveryLogCriteria.deliveredAt();
        webhookDeliveryLogCriteria.webhookEndpointId();
        webhookDeliveryLogCriteria.distinct();
    }

    private static Condition<WebhookDeliveryLogCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getDeliveryId()) &&
                condition.apply(criteria.getEvent()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getHttpStatus()) &&
                condition.apply(criteria.getAttempts()) &&
                condition.apply(criteria.getLastError()) &&
                condition.apply(criteria.getDeliveredAt()) &&
                condition.apply(criteria.getWebhookEndpointId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<WebhookDeliveryLogCriteria> copyFiltersAre(
        WebhookDeliveryLogCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getDeliveryId(), copy.getDeliveryId()) &&
                condition.apply(criteria.getEvent(), copy.getEvent()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getHttpStatus(), copy.getHttpStatus()) &&
                condition.apply(criteria.getAttempts(), copy.getAttempts()) &&
                condition.apply(criteria.getLastError(), copy.getLastError()) &&
                condition.apply(criteria.getDeliveredAt(), copy.getDeliveredAt()) &&
                condition.apply(criteria.getWebhookEndpointId(), copy.getWebhookEndpointId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
