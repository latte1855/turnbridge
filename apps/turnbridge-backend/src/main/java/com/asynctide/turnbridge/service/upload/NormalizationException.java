package com.asynctide.turnbridge.service.upload;

import com.asynctide.turnbridge.web.rest.errors.ErrorConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause.ProblemDetailWithCauseBuilder;

/**
 * Normalize 過程中的錯誤描述，並以 ProblemDetail 形式回傳。
 */
public class NormalizationException extends ErrorResponseException {

    private final String errorCode;
    private final String field;
    private final String normalizedFamily;

    public NormalizationException(String message, String errorCode, String field, String normalizedFamily) {
        super(HttpStatus.BAD_REQUEST, buildProblem(message, errorCode, field, normalizedFamily), null);
        this.errorCode = errorCode;
        this.field = field;
        this.normalizedFamily = normalizedFamily;
    }

    private static ProblemDetailWithCause buildProblem(String message, String errorCode, String field, String normalizedFamily) {
        return ProblemDetailWithCauseBuilder.instance()
            .withStatus(HttpStatus.BAD_REQUEST.value())
            .withType(ErrorConstants.NORMALIZATION_FAILURE_TYPE)
            .withTitle(message)
            .withProperty("errorCode", errorCode)
            .withProperty("field", field)
            .withProperty("normalizedFamily", normalizedFamily)
            .build();
    }

    /** 錯誤代碼（如 MIG 驗證）。 */
    public String getErrorCode() {
        return errorCode;
    }

    /** 觸發錯誤的欄位名稱。 */
    public String getField() {
        return field;
    }

    /** 觸發錯誤的正規化訊息別。 */
    public String getNormalizedFamily() {
        return normalizedFamily;
    }

    /** 取得 ProblemDetail 內容，利於測試與序列化。 */
    public ProblemDetailWithCause getProblemDetailWithCause() {
        return (ProblemDetailWithCause) this.getBody();
    }
}
