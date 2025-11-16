package com.asynctide.turnbridge.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TurnkeyMessageCriteriaTest {

    @Test
    void newTurnkeyMessageCriteriaHasAllFiltersNullTest() {
        var turnkeyMessageCriteria = new TurnkeyMessageCriteria();
        assertThat(turnkeyMessageCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void turnkeyMessageCriteriaFluentMethodsCreatesFiltersTest() {
        var turnkeyMessageCriteria = new TurnkeyMessageCriteria();

        setAllFilters(turnkeyMessageCriteria);

        assertThat(turnkeyMessageCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void turnkeyMessageCriteriaCopyCreatesNullFilterTest() {
        var turnkeyMessageCriteria = new TurnkeyMessageCriteria();
        var copy = turnkeyMessageCriteria.copy();

        assertThat(turnkeyMessageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(turnkeyMessageCriteria)
        );
    }

    @Test
    void turnkeyMessageCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var turnkeyMessageCriteria = new TurnkeyMessageCriteria();
        setAllFilters(turnkeyMessageCriteria);

        var copy = turnkeyMessageCriteria.copy();

        assertThat(turnkeyMessageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(turnkeyMessageCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var turnkeyMessageCriteria = new TurnkeyMessageCriteria();

        assertThat(turnkeyMessageCriteria).hasToString("TurnkeyMessageCriteria{}");
    }

    private static void setAllFilters(TurnkeyMessageCriteria turnkeyMessageCriteria) {
        turnkeyMessageCriteria.id();
        turnkeyMessageCriteria.messageId();
        turnkeyMessageCriteria.messageFamily();
        turnkeyMessageCriteria.type();
        turnkeyMessageCriteria.code();
        turnkeyMessageCriteria.payloadPath();
        turnkeyMessageCriteria.receivedAt();
        turnkeyMessageCriteria.invoiceId();
        turnkeyMessageCriteria.distinct();
    }

    private static Condition<TurnkeyMessageCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getMessageId()) &&
                condition.apply(criteria.getMessageFamily()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getPayloadPath()) &&
                condition.apply(criteria.getReceivedAt()) &&
                condition.apply(criteria.getInvoiceId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TurnkeyMessageCriteria> copyFiltersAre(
        TurnkeyMessageCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getMessageId(), copy.getMessageId()) &&
                condition.apply(criteria.getMessageFamily(), copy.getMessageFamily()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getPayloadPath(), copy.getPayloadPath()) &&
                condition.apply(criteria.getReceivedAt(), copy.getReceivedAt()) &&
                condition.apply(criteria.getInvoiceId(), copy.getInvoiceId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
