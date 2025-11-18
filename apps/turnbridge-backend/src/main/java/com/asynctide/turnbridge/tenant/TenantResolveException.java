package com.asynctide.turnbridge.tenant;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause.ProblemDetailWithCauseBuilder;

/**
 * 當無法解析請求的租戶資訊時丟出（回應 ProblemDetail）。
 */
public class TenantResolveException extends ErrorResponseException {

    public TenantResolveException(String message, String hint) {
        super(HttpStatus.BAD_REQUEST, problemDetail(message, hint), null);
    }

    private static ProblemDetailWithCause problemDetail(String message, String hint) {
        return ProblemDetailWithCauseBuilder
            .instance()
            .withStatus(HttpStatus.BAD_REQUEST.value())
            .withType(URI.create("https://turnbridge.example.com/problem/tenant-resolve"))
            .withTitle("租戶解析失敗")
            .withDetail(message)
            .withProperty("hint", hint)
            .build();
    }
}
