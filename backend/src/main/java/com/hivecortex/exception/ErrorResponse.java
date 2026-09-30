package com.hivecortex.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Canonical API error envelope — every non-2xx response body has this shape:
 *
 * <pre>
 * {
 *   "error": {
 *     "code":    "NOT_FOUND",
 *     "message": "Project with id 'abc' was not found.",
 *     "details": null                          // or a list of FieldError for 400
 *   }
 * }
 * </pre>
 *
 * <p>Use the static factory methods to build instances consistently:</p>
 * <pre>
 *   ErrorResponse.of("NOT_FOUND", "…")
 *   ErrorResponse.validationError(fieldErrors)
 * </pre>
 */
public record ErrorResponse(Body error) {

    /**
     * Inner body — {@code details} is omitted from JSON when null
     * to keep non-validation error payloads clean.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Body(String code, String message, List<FieldError> details) {}

    /**
     * A single field-level validation error, used in 400 responses.
     */
    public record FieldError(String field, String message) {}

    // ---------------------------------------------------------------------------
    // Static factories
    // ---------------------------------------------------------------------------

    /**
     * Generic single-message error (no field details).
     */
    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(new Body(code, message, null));
    }

    /**
     * Validation error with per-field details list.
     */
    public static ErrorResponse validationError(List<FieldError> fieldErrors) {
        return new ErrorResponse(new Body(
                "VALIDATION_FAILED",
                "Request validation failed. Check the 'details' field for per-field errors.",
                fieldErrors
        ));
    }

    // ---------------------------------------------------------------------------
    // ResponseEntity convenience builders
    // ---------------------------------------------------------------------------

    public ResponseEntity<ErrorResponse> toResponse(org.springframework.http.HttpStatus status) {
        return ResponseEntity.status(status).body(this);
    }
}
