package com.asynctide.turnbridge.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ManualActionCriteriaTest {

    @Test
    void newManualActionCriteriaHasAllFiltersNullTest() {
        var manualActionCriteria = new ManualActionCriteria();
        assertThat(manualActionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void manualActionCriteriaFluentMethodsCreatesFiltersTest() {
        var manualActionCriteria = new ManualActionCriteria();

        setAllFilters(manualActionCriteria);

        assertThat(manualActionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void manualActionCriteriaCopyCreatesNullFilterTest() {
        var manualActionCriteria = new ManualActionCriteria();
        var copy = manualActionCriteria.copy();

        assertThat(manualActionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(manualActionCriteria)
        );
    }

    @Test
    void manualActionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var manualActionCriteria = new ManualActionCriteria();
        setAllFilters(manualActionCriteria);

        var copy = manualActionCriteria.copy();

        assertThat(manualActionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(manualActionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var manualActionCriteria = new ManualActionCriteria();

        assertThat(manualActionCriteria).hasToString("ManualActionCriteria{}");
    }

    private static void setAllFilters(ManualActionCriteria manualActionCriteria) {
        manualActionCriteria.id();
        manualActionCriteria.actionType();
        manualActionCriteria.reason();
        manualActionCriteria.status();
        manualActionCriteria.requestedBy();
        manualActionCriteria.requestedAt();
        manualActionCriteria.approvedBy();
        manualActionCriteria.approvedAt();
        manualActionCriteria.tenantId();
        manualActionCriteria.invoiceId();
        manualActionCriteria.importFileId();
        manualActionCriteria.distinct();
    }

    private static Condition<ManualActionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getActionType()) &&
                condition.apply(criteria.getReason()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getRequestedBy()) &&
                condition.apply(criteria.getRequestedAt()) &&
                condition.apply(criteria.getApprovedBy()) &&
                condition.apply(criteria.getApprovedAt()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getInvoiceId()) &&
                condition.apply(criteria.getImportFileId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ManualActionCriteria> copyFiltersAre(
        ManualActionCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getActionType(), copy.getActionType()) &&
                condition.apply(criteria.getReason(), copy.getReason()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getRequestedBy(), copy.getRequestedBy()) &&
                condition.apply(criteria.getRequestedAt(), copy.getRequestedAt()) &&
                condition.apply(criteria.getApprovedBy(), copy.getApprovedBy()) &&
                condition.apply(criteria.getApprovedAt(), copy.getApprovedAt()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getInvoiceId(), copy.getInvoiceId()) &&
                condition.apply(criteria.getImportFileId(), copy.getImportFileId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
