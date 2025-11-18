package com.asynctide.turnbridge.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ImportFileItemErrorCriteriaTest {

    @Test
    void newImportFileItemErrorCriteriaHasAllFiltersNullTest() {
        var importFileItemErrorCriteria = new ImportFileItemErrorCriteria();
        assertThat(importFileItemErrorCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void importFileItemErrorCriteriaFluentMethodsCreatesFiltersTest() {
        var importFileItemErrorCriteria = new ImportFileItemErrorCriteria();

        setAllFilters(importFileItemErrorCriteria);

        assertThat(importFileItemErrorCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void importFileItemErrorCriteriaCopyCreatesNullFilterTest() {
        var importFileItemErrorCriteria = new ImportFileItemErrorCriteria();
        var copy = importFileItemErrorCriteria.copy();

        assertThat(importFileItemErrorCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(importFileItemErrorCriteria)
        );
    }

    @Test
    void importFileItemErrorCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var importFileItemErrorCriteria = new ImportFileItemErrorCriteria();
        setAllFilters(importFileItemErrorCriteria);

        var copy = importFileItemErrorCriteria.copy();

        assertThat(importFileItemErrorCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(importFileItemErrorCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var importFileItemErrorCriteria = new ImportFileItemErrorCriteria();

        assertThat(importFileItemErrorCriteria).hasToString("ImportFileItemErrorCriteria{}");
    }

    private static void setAllFilters(ImportFileItemErrorCriteria importFileItemErrorCriteria) {
        importFileItemErrorCriteria.id();
        importFileItemErrorCriteria.columnIndex();
        importFileItemErrorCriteria.fieldName();
        importFileItemErrorCriteria.errorCode();
        importFileItemErrorCriteria.message();
        importFileItemErrorCriteria.severity();
        importFileItemErrorCriteria.occurredAt();
        importFileItemErrorCriteria.importFileItemId();
        importFileItemErrorCriteria.distinct();
    }

    private static Condition<ImportFileItemErrorCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getColumnIndex()) &&
                condition.apply(criteria.getFieldName()) &&
                condition.apply(criteria.getErrorCode()) &&
                condition.apply(criteria.getMessage()) &&
                condition.apply(criteria.getSeverity()) &&
                condition.apply(criteria.getOccurredAt()) &&
                condition.apply(criteria.getImportFileItemId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ImportFileItemErrorCriteria> copyFiltersAre(
        ImportFileItemErrorCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getColumnIndex(), copy.getColumnIndex()) &&
                condition.apply(criteria.getFieldName(), copy.getFieldName()) &&
                condition.apply(criteria.getErrorCode(), copy.getErrorCode()) &&
                condition.apply(criteria.getMessage(), copy.getMessage()) &&
                condition.apply(criteria.getSeverity(), copy.getSeverity()) &&
                condition.apply(criteria.getOccurredAt(), copy.getOccurredAt()) &&
                condition.apply(criteria.getImportFileItemId(), copy.getImportFileItemId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
