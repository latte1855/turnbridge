package com.asynctide.turnbridge.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TenantCriteriaTest {

    @Test
    void newTenantCriteriaHasAllFiltersNullTest() {
        var tenantCriteria = new TenantCriteria();
        assertThat(tenantCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void tenantCriteriaFluentMethodsCreatesFiltersTest() {
        var tenantCriteria = new TenantCriteria();

        setAllFilters(tenantCriteria);

        assertThat(tenantCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void tenantCriteriaCopyCreatesNullFilterTest() {
        var tenantCriteria = new TenantCriteria();
        var copy = tenantCriteria.copy();

        assertThat(tenantCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(tenantCriteria)
        );
    }

    @Test
    void tenantCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var tenantCriteria = new TenantCriteria();
        setAllFilters(tenantCriteria);

        var copy = tenantCriteria.copy();

        assertThat(tenantCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(tenantCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var tenantCriteria = new TenantCriteria();

        assertThat(tenantCriteria).hasToString("TenantCriteria{}");
    }

    private static void setAllFilters(TenantCriteria tenantCriteria) {
        tenantCriteria.id();
        tenantCriteria.name();
        tenantCriteria.code();
        tenantCriteria.status();
        tenantCriteria.importFilesId();
        tenantCriteria.invoicesId();
        tenantCriteria.webhookEndpointsId();
        tenantCriteria.distinct();
    }

    private static Condition<TenantCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getImportFilesId()) &&
                condition.apply(criteria.getInvoicesId()) &&
                condition.apply(criteria.getWebhookEndpointsId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TenantCriteria> copyFiltersAre(TenantCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getImportFilesId(), copy.getImportFilesId()) &&
                condition.apply(criteria.getInvoicesId(), copy.getInvoicesId()) &&
                condition.apply(criteria.getWebhookEndpointsId(), copy.getWebhookEndpointsId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
