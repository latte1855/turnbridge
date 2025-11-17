package com.asynctide.turnbridge.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ImportFileItemCriteriaTest {

    @Test
    void newImportFileItemCriteriaHasAllFiltersNullTest() {
        var importFileItemCriteria = new ImportFileItemCriteria();
        assertThat(importFileItemCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void importFileItemCriteriaFluentMethodsCreatesFiltersTest() {
        var importFileItemCriteria = new ImportFileItemCriteria();

        setAllFilters(importFileItemCriteria);

        assertThat(importFileItemCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void importFileItemCriteriaCopyCreatesNullFilterTest() {
        var importFileItemCriteria = new ImportFileItemCriteria();
        var copy = importFileItemCriteria.copy();

        assertThat(importFileItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(importFileItemCriteria)
        );
    }

    @Test
    void importFileItemCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var importFileItemCriteria = new ImportFileItemCriteria();
        setAllFilters(importFileItemCriteria);

        var copy = importFileItemCriteria.copy();

        assertThat(importFileItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(importFileItemCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var importFileItemCriteria = new ImportFileItemCriteria();

        assertThat(importFileItemCriteria).hasToString("ImportFileItemCriteria{}");
    }

    private static void setAllFilters(ImportFileItemCriteria importFileItemCriteria) {
        importFileItemCriteria.id();
        importFileItemCriteria.lineIndex();
        importFileItemCriteria.rawHash();
        importFileItemCriteria.sourceFamily();
        importFileItemCriteria.normalizedFamily();
        importFileItemCriteria.status();
        importFileItemCriteria.errorCode();
        importFileItemCriteria.errorMessage();
        importFileItemCriteria.importFileId();
        importFileItemCriteria.invoiceId();
        importFileItemCriteria.distinct();
    }

    private static Condition<ImportFileItemCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getLineIndex()) &&
                condition.apply(criteria.getRawHash()) &&
                condition.apply(criteria.getSourceFamily()) &&
                condition.apply(criteria.getNormalizedFamily()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getErrorCode()) &&
                condition.apply(criteria.getErrorMessage()) &&
                condition.apply(criteria.getImportFileId()) &&
                condition.apply(criteria.getInvoiceId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ImportFileItemCriteria> copyFiltersAre(
        ImportFileItemCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getLineIndex(), copy.getLineIndex()) &&
                condition.apply(criteria.getRawHash(), copy.getRawHash()) &&
                condition.apply(criteria.getSourceFamily(), copy.getSourceFamily()) &&
                condition.apply(criteria.getNormalizedFamily(), copy.getNormalizedFamily()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getErrorCode(), copy.getErrorCode()) &&
                condition.apply(criteria.getErrorMessage(), copy.getErrorMessage()) &&
                condition.apply(criteria.getImportFileId(), copy.getImportFileId()) &&
                condition.apply(criteria.getInvoiceId(), copy.getInvoiceId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
