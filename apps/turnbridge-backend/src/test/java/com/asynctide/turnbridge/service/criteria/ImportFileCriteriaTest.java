package com.asynctide.turnbridge.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ImportFileCriteriaTest {

    @Test
    void newImportFileCriteriaHasAllFiltersNullTest() {
        var importFileCriteria = new ImportFileCriteria();
        assertThat(importFileCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void importFileCriteriaFluentMethodsCreatesFiltersTest() {
        var importFileCriteria = new ImportFileCriteria();

        setAllFilters(importFileCriteria);

        assertThat(importFileCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void importFileCriteriaCopyCreatesNullFilterTest() {
        var importFileCriteria = new ImportFileCriteria();
        var copy = importFileCriteria.copy();

        assertThat(importFileCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(importFileCriteria)
        );
    }

    @Test
    void importFileCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var importFileCriteria = new ImportFileCriteria();
        setAllFilters(importFileCriteria);

        var copy = importFileCriteria.copy();

        assertThat(importFileCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(importFileCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var importFileCriteria = new ImportFileCriteria();

        assertThat(importFileCriteria).hasToString("ImportFileCriteria{}");
    }

    private static void setAllFilters(ImportFileCriteria importFileCriteria) {
        importFileCriteria.id();
        importFileCriteria.importType();
        importFileCriteria.originalFilename();
        importFileCriteria.sha256();
        importFileCriteria.totalCount();
        importFileCriteria.successCount();
        importFileCriteria.errorCount();
        importFileCriteria.status();
        importFileCriteria.legacyType();
        importFileCriteria.tenantId();
        importFileCriteria.distinct();
    }

    private static Condition<ImportFileCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getImportType()) &&
                condition.apply(criteria.getOriginalFilename()) &&
                condition.apply(criteria.getSha256()) &&
                condition.apply(criteria.getTotalCount()) &&
                condition.apply(criteria.getSuccessCount()) &&
                condition.apply(criteria.getErrorCount()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getLegacyType()) &&
                condition.apply(criteria.getTenantId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ImportFileCriteria> copyFiltersAre(ImportFileCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getImportType(), copy.getImportType()) &&
                condition.apply(criteria.getOriginalFilename(), copy.getOriginalFilename()) &&
                condition.apply(criteria.getSha256(), copy.getSha256()) &&
                condition.apply(criteria.getTotalCount(), copy.getTotalCount()) &&
                condition.apply(criteria.getSuccessCount(), copy.getSuccessCount()) &&
                condition.apply(criteria.getErrorCount(), copy.getErrorCount()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getLegacyType(), copy.getLegacyType()) &&
                condition.apply(criteria.getTenantId(), copy.getTenantId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
