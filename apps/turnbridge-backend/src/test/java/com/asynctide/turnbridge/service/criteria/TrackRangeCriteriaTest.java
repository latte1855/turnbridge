package com.asynctide.turnbridge.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TrackRangeCriteriaTest {

    @Test
    void newTrackRangeCriteriaHasAllFiltersNullTest() {
        var trackRangeCriteria = new TrackRangeCriteria();
        assertThat(trackRangeCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void trackRangeCriteriaFluentMethodsCreatesFiltersTest() {
        var trackRangeCriteria = new TrackRangeCriteria();

        setAllFilters(trackRangeCriteria);

        assertThat(trackRangeCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void trackRangeCriteriaCopyCreatesNullFilterTest() {
        var trackRangeCriteria = new TrackRangeCriteria();
        var copy = trackRangeCriteria.copy();

        assertThat(trackRangeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(trackRangeCriteria)
        );
    }

    @Test
    void trackRangeCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var trackRangeCriteria = new TrackRangeCriteria();
        setAllFilters(trackRangeCriteria);

        var copy = trackRangeCriteria.copy();

        assertThat(trackRangeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(trackRangeCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var trackRangeCriteria = new TrackRangeCriteria();

        assertThat(trackRangeCriteria).hasToString("TrackRangeCriteria{}");
    }

    private static void setAllFilters(TrackRangeCriteria trackRangeCriteria) {
        trackRangeCriteria.id();
        trackRangeCriteria.sellerId();
        trackRangeCriteria.period();
        trackRangeCriteria.prefix();
        trackRangeCriteria.startNo();
        trackRangeCriteria.endNo();
        trackRangeCriteria.currentNo();
        trackRangeCriteria.status();
        trackRangeCriteria.version();
        trackRangeCriteria.lockOwner();
        trackRangeCriteria.lockAt();
        trackRangeCriteria.distinct();
    }

    private static Condition<TrackRangeCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getSellerId()) &&
                condition.apply(criteria.getPeriod()) &&
                condition.apply(criteria.getPrefix()) &&
                condition.apply(criteria.getStartNo()) &&
                condition.apply(criteria.getEndNo()) &&
                condition.apply(criteria.getCurrentNo()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getVersion()) &&
                condition.apply(criteria.getLockOwner()) &&
                condition.apply(criteria.getLockAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TrackRangeCriteria> copyFiltersAre(TrackRangeCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getSellerId(), copy.getSellerId()) &&
                condition.apply(criteria.getPeriod(), copy.getPeriod()) &&
                condition.apply(criteria.getPrefix(), copy.getPrefix()) &&
                condition.apply(criteria.getStartNo(), copy.getStartNo()) &&
                condition.apply(criteria.getEndNo(), copy.getEndNo()) &&
                condition.apply(criteria.getCurrentNo(), copy.getCurrentNo()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getVersion(), copy.getVersion()) &&
                condition.apply(criteria.getLockOwner(), copy.getLockOwner()) &&
                condition.apply(criteria.getLockAt(), copy.getLockAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
