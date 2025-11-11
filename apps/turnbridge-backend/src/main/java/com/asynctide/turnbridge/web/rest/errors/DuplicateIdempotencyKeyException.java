package com.asynctide.turnbridge.web.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause.ProblemDetailWithCauseBuilder;

public class DuplicateIdempotencyKeyException extends ErrorResponseException {
    private static final long serialVersionUID = 1L;

	public DuplicateIdempotencyKeyException(String message) {
        super(
            HttpStatus.CONFLICT,
            ProblemDetailWithCauseBuilder.instance()
                .withStatus(HttpStatus.CONFLICT.value())
                .withTitle(message)
                .withProperty("message", "error.idempotency.duplicate")
                .withProperty("params", "uploadJob")
                .build(),
            null
        );
    }
}
