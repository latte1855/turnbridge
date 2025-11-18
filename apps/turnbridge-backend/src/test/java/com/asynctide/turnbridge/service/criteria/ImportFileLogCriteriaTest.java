package com.asynctide.turnbridge.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ImportFileLogCriteriaTest {

    @Test
    void newImportFileLogCriteriaHasAllFiltersNullTest() {
        var importFileLogCriteria = new ImportFileLogCriteria();
        assertThat(importFileLogCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void importFileLogCriteriaFluentMethodsCreatesFiltersTest() {
        var importFileLogCriteria = new ImportFileLogCriteria();

        setAllFilters(importFileLogCriteria);

        assertThat(importFileLogCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void importFileLogCriteriaCopyCreatesNullFilterTest() {
        var importFileLogCriteria = new ImportFileLogCriteria();
        var copy = importFileLogCriteria.copy();

        assertThat(importFileLogCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(importFileLogCriteria)
        );
    }

    @Test
    void importFileLogCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var importFileLogCriteria = new ImportFileLogCriteria();
        setAllFilters(importFileLogCriteria);

        var copy = importFileLogCriteria.copy();

        assertThat(importFileLogCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(importFileLogCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var importFileLogCriteria = new ImportFileLogCriteria();

        assertThat(importFileLogCriteria).hasToString("ImportFileLogCriteria{}");
    }

    private static void setAllFilters(ImportFileLogCriteria importFileLogCriteria) {
        importFileLogCriteria.id();
        importFileLogCriteria.eventCode();
        importFileLogCriteria.level();
        importFileLogCriteria.message();
        importFileLogCriteria.occurredAt();
        importFileLogCriteria.importFileId();
        importFileLogCriteria.distinct();
    }

    private static Condition<ImportFileLogCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getEventCode()) &&
                condition.apply(criteria.getLevel()) &&
                condition.apply(criteria.getMessage()) &&
                condition.apply(criteria.getOccurredAt()) &&
                condition.apply(criteria.getImportFileId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ImportFileLogCriteria> copyFiltersAre(
        ImportFileLogCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getEventCode(), copy.getEventCode()) &&
                condition.apply(criteria.getLevel(), copy.getLevel()) &&
                condition.apply(criteria.getMessage(), copy.getMessage()) &&
                condition.apply(criteria.getOccurredAt(), copy.getOccurredAt()) &&
                condition.apply(criteria.getImportFileId(), copy.getImportFileId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
