package com.asynctide.turnbridge.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class InvoiceItemCriteriaTest {

    @Test
    void newInvoiceItemCriteriaHasAllFiltersNullTest() {
        var invoiceItemCriteria = new InvoiceItemCriteria();
        assertThat(invoiceItemCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void invoiceItemCriteriaFluentMethodsCreatesFiltersTest() {
        var invoiceItemCriteria = new InvoiceItemCriteria();

        setAllFilters(invoiceItemCriteria);

        assertThat(invoiceItemCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void invoiceItemCriteriaCopyCreatesNullFilterTest() {
        var invoiceItemCriteria = new InvoiceItemCriteria();
        var copy = invoiceItemCriteria.copy();

        assertThat(invoiceItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(invoiceItemCriteria)
        );
    }

    @Test
    void invoiceItemCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var invoiceItemCriteria = new InvoiceItemCriteria();
        setAllFilters(invoiceItemCriteria);

        var copy = invoiceItemCriteria.copy();

        assertThat(invoiceItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(invoiceItemCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var invoiceItemCriteria = new InvoiceItemCriteria();

        assertThat(invoiceItemCriteria).hasToString("InvoiceItemCriteria{}");
    }

    private static void setAllFilters(InvoiceItemCriteria invoiceItemCriteria) {
        invoiceItemCriteria.id();
        invoiceItemCriteria.description();
        invoiceItemCriteria.quantity();
        invoiceItemCriteria.unitPrice();
        invoiceItemCriteria.amount();
        invoiceItemCriteria.sequence();
        invoiceItemCriteria.invoiceId();
        invoiceItemCriteria.distinct();
    }

    private static Condition<InvoiceItemCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getQuantity()) &&
                condition.apply(criteria.getUnitPrice()) &&
                condition.apply(criteria.getAmount()) &&
                condition.apply(criteria.getSequence()) &&
                condition.apply(criteria.getInvoiceId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<InvoiceItemCriteria> copyFiltersAre(InvoiceItemCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getQuantity(), copy.getQuantity()) &&
                condition.apply(criteria.getUnitPrice(), copy.getUnitPrice()) &&
                condition.apply(criteria.getAmount(), copy.getAmount()) &&
                condition.apply(criteria.getSequence(), copy.getSequence()) &&
                condition.apply(criteria.getInvoiceId(), copy.getInvoiceId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
