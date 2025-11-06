package com.asynctide.turnbridge.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class UploadJobItemCriteriaTest {

    @Test
    void newUploadJobItemCriteriaHasAllFiltersNullTest() {
        var uploadJobItemCriteria = new UploadJobItemCriteria();
        assertThat(uploadJobItemCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void uploadJobItemCriteriaFluentMethodsCreatesFiltersTest() {
        var uploadJobItemCriteria = new UploadJobItemCriteria();

        setAllFilters(uploadJobItemCriteria);

        assertThat(uploadJobItemCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void uploadJobItemCriteriaCopyCreatesNullFilterTest() {
        var uploadJobItemCriteria = new UploadJobItemCriteria();
        var copy = uploadJobItemCriteria.copy();

        assertThat(uploadJobItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(uploadJobItemCriteria)
        );
    }

    @Test
    void uploadJobItemCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var uploadJobItemCriteria = new UploadJobItemCriteria();
        setAllFilters(uploadJobItemCriteria);

        var copy = uploadJobItemCriteria.copy();

        assertThat(uploadJobItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(uploadJobItemCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var uploadJobItemCriteria = new UploadJobItemCriteria();

        assertThat(uploadJobItemCriteria).hasToString("UploadJobItemCriteria{}");
    }

    private static void setAllFilters(UploadJobItemCriteria uploadJobItemCriteria) {
        uploadJobItemCriteria.id();
        uploadJobItemCriteria.lineNo();
        uploadJobItemCriteria.traceId();
        uploadJobItemCriteria.status();
        uploadJobItemCriteria.resultCode();
        uploadJobItemCriteria.resultMsg();
        uploadJobItemCriteria.buyerId();
        uploadJobItemCriteria.buyerName();
        uploadJobItemCriteria.currency();
        uploadJobItemCriteria.amountExcl();
        uploadJobItemCriteria.taxAmount();
        uploadJobItemCriteria.amountIncl();
        uploadJobItemCriteria.taxType();
        uploadJobItemCriteria.invoiceDate();
        uploadJobItemCriteria.invoiceNo();
        uploadJobItemCriteria.assignedPrefix();
        uploadJobItemCriteria.rawHash();
        uploadJobItemCriteria.profileDetected();
        uploadJobItemCriteria.jobId();
        uploadJobItemCriteria.distinct();
    }

    private static Condition<UploadJobItemCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getLineNo()) &&
                condition.apply(criteria.getTraceId()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getResultCode()) &&
                condition.apply(criteria.getResultMsg()) &&
                condition.apply(criteria.getBuyerId()) &&
                condition.apply(criteria.getBuyerName()) &&
                condition.apply(criteria.getCurrency()) &&
                condition.apply(criteria.getAmountExcl()) &&
                condition.apply(criteria.getTaxAmount()) &&
                condition.apply(criteria.getAmountIncl()) &&
                condition.apply(criteria.getTaxType()) &&
                condition.apply(criteria.getInvoiceDate()) &&
                condition.apply(criteria.getInvoiceNo()) &&
                condition.apply(criteria.getAssignedPrefix()) &&
                condition.apply(criteria.getRawHash()) &&
                condition.apply(criteria.getProfileDetected()) &&
                condition.apply(criteria.getJobId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<UploadJobItemCriteria> copyFiltersAre(
        UploadJobItemCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getLineNo(), copy.getLineNo()) &&
                condition.apply(criteria.getTraceId(), copy.getTraceId()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getResultCode(), copy.getResultCode()) &&
                condition.apply(criteria.getResultMsg(), copy.getResultMsg()) &&
                condition.apply(criteria.getBuyerId(), copy.getBuyerId()) &&
                condition.apply(criteria.getBuyerName(), copy.getBuyerName()) &&
                condition.apply(criteria.getCurrency(), copy.getCurrency()) &&
                condition.apply(criteria.getAmountExcl(), copy.getAmountExcl()) &&
                condition.apply(criteria.getTaxAmount(), copy.getTaxAmount()) &&
                condition.apply(criteria.getAmountIncl(), copy.getAmountIncl()) &&
                condition.apply(criteria.getTaxType(), copy.getTaxType()) &&
                condition.apply(criteria.getInvoiceDate(), copy.getInvoiceDate()) &&
                condition.apply(criteria.getInvoiceNo(), copy.getInvoiceNo()) &&
                condition.apply(criteria.getAssignedPrefix(), copy.getAssignedPrefix()) &&
                condition.apply(criteria.getRawHash(), copy.getRawHash()) &&
                condition.apply(criteria.getProfileDetected(), copy.getProfileDetected()) &&
                condition.apply(criteria.getJobId(), copy.getJobId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
