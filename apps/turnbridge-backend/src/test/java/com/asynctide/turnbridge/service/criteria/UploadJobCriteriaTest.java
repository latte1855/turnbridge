package com.asynctide.turnbridge.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class UploadJobCriteriaTest {

    @Test
    void newUploadJobCriteriaHasAllFiltersNullTest() {
        var uploadJobCriteria = new UploadJobCriteria();
        assertThat(uploadJobCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void uploadJobCriteriaFluentMethodsCreatesFiltersTest() {
        var uploadJobCriteria = new UploadJobCriteria();

        setAllFilters(uploadJobCriteria);

        assertThat(uploadJobCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void uploadJobCriteriaCopyCreatesNullFilterTest() {
        var uploadJobCriteria = new UploadJobCriteria();
        var copy = uploadJobCriteria.copy();

        assertThat(uploadJobCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(uploadJobCriteria)
        );
    }

    @Test
    void uploadJobCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var uploadJobCriteria = new UploadJobCriteria();
        setAllFilters(uploadJobCriteria);

        var copy = uploadJobCriteria.copy();

        assertThat(uploadJobCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(uploadJobCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var uploadJobCriteria = new UploadJobCriteria();

        assertThat(uploadJobCriteria).hasToString("UploadJobCriteria{}");
    }

    private static void setAllFilters(UploadJobCriteria uploadJobCriteria) {
        uploadJobCriteria.id();
        uploadJobCriteria.jobId();
        uploadJobCriteria.sellerId();
        uploadJobCriteria.sellerName();
        uploadJobCriteria.period();
        uploadJobCriteria.profile();
        uploadJobCriteria.sourceFilename();
        uploadJobCriteria.sourceMediaType();
        uploadJobCriteria.status();
        uploadJobCriteria.total();
        uploadJobCriteria.accepted();
        uploadJobCriteria.failed();
        uploadJobCriteria.sent();
        uploadJobCriteria.remark();
        uploadJobCriteria.itemsId();
        uploadJobCriteria.originalFileId();
        uploadJobCriteria.resultFileId();
        uploadJobCriteria.distinct();
    }

    private static Condition<UploadJobCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getJobId()) &&
                condition.apply(criteria.getSellerId()) &&
                condition.apply(criteria.getSellerName()) &&
                condition.apply(criteria.getPeriod()) &&
                condition.apply(criteria.getProfile()) &&
                condition.apply(criteria.getSourceFilename()) &&
                condition.apply(criteria.getSourceMediaType()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getTotal()) &&
                condition.apply(criteria.getAccepted()) &&
                condition.apply(criteria.getFailed()) &&
                condition.apply(criteria.getSent()) &&
                condition.apply(criteria.getRemark()) &&
                condition.apply(criteria.getItemsId()) &&
                condition.apply(criteria.getOriginalFileId()) &&
                condition.apply(criteria.getResultFileId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<UploadJobCriteria> copyFiltersAre(UploadJobCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getJobId(), copy.getJobId()) &&
                condition.apply(criteria.getSellerId(), copy.getSellerId()) &&
                condition.apply(criteria.getSellerName(), copy.getSellerName()) &&
                condition.apply(criteria.getPeriod(), copy.getPeriod()) &&
                condition.apply(criteria.getProfile(), copy.getProfile()) &&
                condition.apply(criteria.getSourceFilename(), copy.getSourceFilename()) &&
                condition.apply(criteria.getSourceMediaType(), copy.getSourceMediaType()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getTotal(), copy.getTotal()) &&
                condition.apply(criteria.getAccepted(), copy.getAccepted()) &&
                condition.apply(criteria.getFailed(), copy.getFailed()) &&
                condition.apply(criteria.getSent(), copy.getSent()) &&
                condition.apply(criteria.getRemark(), copy.getRemark()) &&
                condition.apply(criteria.getItemsId(), copy.getItemsId()) &&
                condition.apply(criteria.getOriginalFileId(), copy.getOriginalFileId()) &&
                condition.apply(criteria.getResultFileId(), copy.getResultFileId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
