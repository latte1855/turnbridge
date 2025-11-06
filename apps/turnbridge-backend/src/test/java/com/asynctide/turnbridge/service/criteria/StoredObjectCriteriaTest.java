package com.asynctide.turnbridge.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class StoredObjectCriteriaTest {

    @Test
    void newStoredObjectCriteriaHasAllFiltersNullTest() {
        var storedObjectCriteria = new StoredObjectCriteria();
        assertThat(storedObjectCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void storedObjectCriteriaFluentMethodsCreatesFiltersTest() {
        var storedObjectCriteria = new StoredObjectCriteria();

        setAllFilters(storedObjectCriteria);

        assertThat(storedObjectCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void storedObjectCriteriaCopyCreatesNullFilterTest() {
        var storedObjectCriteria = new StoredObjectCriteria();
        var copy = storedObjectCriteria.copy();

        assertThat(storedObjectCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(storedObjectCriteria)
        );
    }

    @Test
    void storedObjectCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var storedObjectCriteria = new StoredObjectCriteria();
        setAllFilters(storedObjectCriteria);

        var copy = storedObjectCriteria.copy();

        assertThat(storedObjectCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(storedObjectCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var storedObjectCriteria = new StoredObjectCriteria();

        assertThat(storedObjectCriteria).hasToString("StoredObjectCriteria{}");
    }

    private static void setAllFilters(StoredObjectCriteria storedObjectCriteria) {
        storedObjectCriteria.id();
        storedObjectCriteria.bucket();
        storedObjectCriteria.objectKey();
        storedObjectCriteria.mediaType();
        storedObjectCriteria.contentLength();
        storedObjectCriteria.sha256();
        storedObjectCriteria.purpose();
        storedObjectCriteria.filename();
        storedObjectCriteria.storageClass();
        storedObjectCriteria.encryption();
        storedObjectCriteria.distinct();
    }

    private static Condition<StoredObjectCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getBucket()) &&
                condition.apply(criteria.getObjectKey()) &&
                condition.apply(criteria.getMediaType()) &&
                condition.apply(criteria.getContentLength()) &&
                condition.apply(criteria.getSha256()) &&
                condition.apply(criteria.getPurpose()) &&
                condition.apply(criteria.getFilename()) &&
                condition.apply(criteria.getStorageClass()) &&
                condition.apply(criteria.getEncryption()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<StoredObjectCriteria> copyFiltersAre(
        StoredObjectCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getBucket(), copy.getBucket()) &&
                condition.apply(criteria.getObjectKey(), copy.getObjectKey()) &&
                condition.apply(criteria.getMediaType(), copy.getMediaType()) &&
                condition.apply(criteria.getContentLength(), copy.getContentLength()) &&
                condition.apply(criteria.getSha256(), copy.getSha256()) &&
                condition.apply(criteria.getPurpose(), copy.getPurpose()) &&
                condition.apply(criteria.getFilename(), copy.getFilename()) &&
                condition.apply(criteria.getStorageClass(), copy.getStorageClass()) &&
                condition.apply(criteria.getEncryption(), copy.getEncryption()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
