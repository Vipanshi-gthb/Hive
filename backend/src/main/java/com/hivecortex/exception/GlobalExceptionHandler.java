package com.hivecortex.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

/**
 * Centralized exception handler — maps every exception type to a consistent
 * {@link ErrorResponse} JSON body and the correct HTTP status code.
 *
 * <p>Response shape (always):</p>
 * <pre>
 * {
 *   "error": {
 *     "code":    "NOT_FOUND",
 *     "message": "Project with id 'abc' was not found.",
 *     "details": null          // omitted for non-validation errors
 *   }
 * }
 * </pre>
 *
 * <p><strong>Security note:</strong> The catch-all handler logs the full
 * exception server-side but returns only a safe generic message to the client.
 * Stack traces are never included in any response body.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // -------------------------------------------------------------------------
    // 400 — Validation / Bad Request
    // -------------------------------------------------------------------------

    /**
     * Handles {@code @Valid} / {@code @Validated} DTO validation failures.
     * Extracts per-field errors and returns them in the {@code details} array.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {

        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new ErrorResponse.FieldError(
                        fe.getField(),
                        fe.getDefaultMessage()))
                .toList();

        log.debug("Validation failed: {}", fieldErrors);
        return ErrorResponse.validationError(fieldErrors)
                .toResponse(HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles Jakarta constraint violations on method parameters
     * (e.g. {@code @PathVariable}, {@code @RequestParam} with constraints).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex) {

        List<ErrorResponse.FieldError> fieldErrors = ex.getConstraintViolations()
                .stream()
                .map(cv -> {
                    // Extract just the last segment of the property path for readability
                    String path = cv.getPropertyPath().toString();
                    String field = path.contains(".")
                            ? path.substring(path.lastIndexOf('.') + 1)
                            : path;
                    return new ErrorResponse.FieldError(field, cv.getMessage());
                })
                .toList();

        log.debug("Constraint violations: {}", fieldErrors);
        return ErrorResponse.validationError(fieldErrors)
                .toResponse(HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles malformed / unreadable JSON request bodies.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(
            HttpMessageNotReadableException ex) {

        log.debug("Unreadable HTTP message: {}", ex.getMessage());
        return ErrorResponse.of(
                "BAD_REQUEST",
                "The request body is missing or malformed. Please send valid JSON."
        ).toResponse(HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles path variables of the wrong type (e.g. non-UUID string where UUID expected).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        String expected = ex.getRequiredType() != null
                ? ex.getRequiredType().getSimpleName()
                : "unknown";

        log.debug("Type mismatch on parameter '{}': expected {}", ex.getName(), expected);
        return ErrorResponse.of(
                "BAD_REQUEST",
                String.format("Parameter '%s' must be of type %s.", ex.getName(), expected)
        ).toResponse(HttpStatus.BAD_REQUEST);
    }

    // -------------------------------------------------------------------------
    // 404 — Not Found
    // -------------------------------------------------------------------------

    /**
     * Handles our domain-level not-found exceptions.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {

        log.debug("Resource not found: {}", ex.getMessage());
        return ErrorResponse.of("NOT_FOUND", ex.getMessage())
                .toResponse(HttpStatus.NOT_FOUND);
    }

    /**
     * Handles requests to URLs that don't match any mapped route.
     * Spring 6+ throws {@link NoResourceFoundException} (replaces deprecated NoHandlerFoundException).
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(
            NoResourceFoundException ex) {

        log.debug("No handler found for {} {}", ex.getHttpMethod(), ex.getResourcePath());
        return ErrorResponse.of(
                "NOT_FOUND",
                String.format("No endpoint found for %s %s.", ex.getHttpMethod(), ex.getResourcePath())
        ).toResponse(HttpStatus.NOT_FOUND);
    }

    // -------------------------------------------------------------------------
    // 405 — Method Not Allowed
    // -------------------------------------------------------------------------

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex) {

        log.debug("Method not allowed: {}", ex.getMessage());
        return ErrorResponse.of(
                "METHOD_NOT_ALLOWED",
                String.format("HTTP method '%s' is not supported for this endpoint.", ex.getMethod())
        ).toResponse(HttpStatus.METHOD_NOT_ALLOWED);
    }

    // -------------------------------------------------------------------------
    // 409 — Conflict / Business Rule Violation
    // -------------------------------------------------------------------------

    /**
     * Handles business rule violations (duplicate email, circular dependency, etc.).
     * Uses the exception's {@code violationCode} as the response error code.
     */
    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleViolation(
            BusinessRuleViolationException ex) {

        log.debug("Business rule violated [{}]: {}", ex.getViolationCode(), ex.getMessage());
        return ErrorResponse.of(ex.getViolationCode(), ex.getMessage())
                .toResponse(HttpStatus.CONFLICT);
    }

    // -------------------------------------------------------------------------
    // 500 — Unhandled / Internal Server Error
    // -------------------------------------------------------------------------

    /**
     * Catch-all handler for any exception not matched by a more specific handler above.
     *
     * <p><strong>Security:</strong> The exception is logged with full detail server-side,
     * but the client receives only a generic safe message — no stack trace, no internal
     * class names, no database error details are exposed.</p>
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        // Log the full exception server-side so it can be investigated.
        log.error("Unhandled exception: {}", ex.getMessage(), ex);

        return ErrorResponse.of(
                "INTERNAL_ERROR",
                "An unexpected error occurred. Please try again later or contact support."
        ).toResponse(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
