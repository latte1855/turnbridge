package com.asynctide.turnbridge.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class InvoiceCriteriaTest {

    @Test
    void newInvoiceCriteriaHasAllFiltersNullTest() {
        var invoiceCriteria = new InvoiceCriteria();
        assertThat(invoiceCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void invoiceCriteriaFluentMethodsCreatesFiltersTest() {
        var invoiceCriteria = new InvoiceCriteria();

        setAllFilters(invoiceCriteria);

        assertThat(invoiceCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void invoiceCriteriaCopyCreatesNullFilterTest() {
        var invoiceCriteria = new InvoiceCriteria();
        var copy = invoiceCriteria.copy();

        assertThat(invoiceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(invoiceCriteria)
        );
    }

    @Test
    void invoiceCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var invoiceCriteria = new InvoiceCriteria();
        setAllFilters(invoiceCriteria);

        var copy = invoiceCriteria.copy();

        assertThat(invoiceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(invoiceCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var invoiceCriteria = new InvoiceCriteria();

        assertThat(invoiceCriteria).hasToString("InvoiceCriteria{}");
    }

    private static void setAllFilters(InvoiceCriteria invoiceCriteria) {
        invoiceCriteria.id();
        invoiceCriteria.invoiceNo();
        invoiceCriteria.messageFamily();
        invoiceCriteria.buyerId();
        invoiceCriteria.buyerName();
        invoiceCriteria.sellerId();
        invoiceCriteria.sellerName();
        invoiceCriteria.salesAmount();
        invoiceCriteria.taxAmount();
        invoiceCriteria.totalAmount();
        invoiceCriteria.taxType();
        invoiceCriteria.invoiceStatus();
        invoiceCriteria.issuedAt();
        invoiceCriteria.legacyType();
        invoiceCriteria.importFileId();
        invoiceCriteria.tenantId();
        invoiceCriteria.distinct();
    }

    private static Condition<InvoiceCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getInvoiceNo()) &&
                condition.apply(criteria.getMessageFamily()) &&
                condition.apply(criteria.getBuyerId()) &&
                condition.apply(criteria.getBuyerName()) &&
                condition.apply(criteria.getSellerId()) &&
                condition.apply(criteria.getSellerName()) &&
                condition.apply(criteria.getSalesAmount()) &&
                condition.apply(criteria.getTaxAmount()) &&
                condition.apply(criteria.getTotalAmount()) &&
                condition.apply(criteria.getTaxType()) &&
                condition.apply(criteria.getInvoiceStatus()) &&
                condition.apply(criteria.getIssuedAt()) &&
                condition.apply(criteria.getLegacyType()) &&
                condition.apply(criteria.getImportFileId()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<InvoiceCriteria> copyFiltersAre(InvoiceCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getInvoiceNo(), copy.getInvoiceNo()) &&
                condition.apply(criteria.getMessageFamily(), copy.getMessageFamily()) &&
                condition.apply(criteria.getBuyerId(), copy.getBuyerId()) &&
                condition.apply(criteria.getBuyerName(), copy.getBuyerName()) &&
                condition.apply(criteria.getSellerId(), copy.getSellerId()) &&
                condition.apply(criteria.getSellerName(), copy.getSellerName()) &&
                condition.apply(criteria.getSalesAmount(), copy.getSalesAmount()) &&
                condition.apply(criteria.getTaxAmount(), copy.getTaxAmount()) &&
                condition.apply(criteria.getTotalAmount(), copy.getTotalAmount()) &&
                condition.apply(criteria.getTaxType(), copy.getTaxType()) &&
                condition.apply(criteria.getInvoiceStatus(), copy.getInvoiceStatus()) &&
                condition.apply(criteria.getIssuedAt(), copy.getIssuedAt()) &&
                condition.apply(criteria.getLegacyType(), copy.getLegacyType()) &&
                condition.apply(criteria.getImportFileId(), copy.getImportFileId()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
