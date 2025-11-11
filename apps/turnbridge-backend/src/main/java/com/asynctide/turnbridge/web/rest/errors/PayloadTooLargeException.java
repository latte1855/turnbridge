package com.asynctide.turnbridge.web.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause.ProblemDetailWithCauseBuilder;

public class PayloadTooLargeException extends ErrorResponseException {
    private static final long serialVersionUID = 1L;

	public PayloadTooLargeException(String message) {
        super(
            HttpStatus.PAYLOAD_TOO_LARGE,
            ProblemDetailWithCauseBuilder.instance()
                .withStatus(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .withTitle(message)
                .withProperty("message", "error.file.tooLarge")
                .withProperty("params", "uploadJob")
                .build(),
            null
        );
    }
}
