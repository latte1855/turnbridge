package com.asynctide.turnbridge.service.turnkey;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause.ProblemDetailWithCauseBuilder;

/**
 * 產生 Turnkey XML 失敗時拋出的例外（以 ProblemDetail 回傳）。
 */
public class TurnkeyXmlException extends ErrorResponseException {

    public TurnkeyXmlException(String message) {
        this(message, null);
    }

    public TurnkeyXmlException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, problem(message), cause);
    }

    private static ProblemDetailWithCause problem(String message) {
        return ProblemDetailWithCauseBuilder.instance().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()).withTitle(message).build();
    }
}
