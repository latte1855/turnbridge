package com.asynctide.turnbridge.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class InvoiceAssignNoCriteriaTest {

    @Test
    void newInvoiceAssignNoCriteriaHasAllFiltersNullTest() {
        var invoiceAssignNoCriteria = new InvoiceAssignNoCriteria();
        assertThat(invoiceAssignNoCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void invoiceAssignNoCriteriaFluentMethodsCreatesFiltersTest() {
        var invoiceAssignNoCriteria = new InvoiceAssignNoCriteria();

        setAllFilters(invoiceAssignNoCriteria);

        assertThat(invoiceAssignNoCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void invoiceAssignNoCriteriaCopyCreatesNullFilterTest() {
        var invoiceAssignNoCriteria = new InvoiceAssignNoCriteria();
        var copy = invoiceAssignNoCriteria.copy();

        assertThat(invoiceAssignNoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(invoiceAssignNoCriteria)
        );
    }

    @Test
    void invoiceAssignNoCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var invoiceAssignNoCriteria = new InvoiceAssignNoCriteria();
        setAllFilters(invoiceAssignNoCriteria);

        var copy = invoiceAssignNoCriteria.copy();

        assertThat(invoiceAssignNoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(invoiceAssignNoCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var invoiceAssignNoCriteria = new InvoiceAssignNoCriteria();

        assertThat(invoiceAssignNoCriteria).hasToString("InvoiceAssignNoCriteria{}");
    }

    private static void setAllFilters(InvoiceAssignNoCriteria invoiceAssignNoCriteria) {
        invoiceAssignNoCriteria.id();
        invoiceAssignNoCriteria.track();
        invoiceAssignNoCriteria.period();
        invoiceAssignNoCriteria.fromNo();
        invoiceAssignNoCriteria.toNo();
        invoiceAssignNoCriteria.usedCount();
        invoiceAssignNoCriteria.rollSize();
        invoiceAssignNoCriteria.status();
        invoiceAssignNoCriteria.tenantId();
        invoiceAssignNoCriteria.distinct();
    }

    private static Condition<InvoiceAssignNoCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTrack()) &&
                condition.apply(criteria.getPeriod()) &&
                condition.apply(criteria.getFromNo()) &&
                condition.apply(criteria.getToNo()) &&
                condition.apply(criteria.getUsedCount()) &&
                condition.apply(criteria.getRollSize()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<InvoiceAssignNoCriteria> copyFiltersAre(
        InvoiceAssignNoCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTrack(), copy.getTrack()) &&
                condition.apply(criteria.getPeriod(), copy.getPeriod()) &&
                condition.apply(criteria.getFromNo(), copy.getFromNo()) &&
                condition.apply(criteria.getToNo(), copy.getToNo()) &&
                condition.apply(criteria.getUsedCount(), copy.getUsedCount()) &&
                condition.apply(criteria.getRollSize(), copy.getRollSize()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
