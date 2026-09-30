package com.hivecortex.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a business rule prevents completing an operation —
 * the request is syntactically valid but violates domain logic.
 *
 * <p>Maps to HTTP 409 Conflict via {@link GlobalExceptionHandler}.</p>
 *
 * <p>Examples of business rule violations:</p>
 * <ul>
 *   <li>Attempting to register with an email address already in use.</li>
 *   <li>Adding a task dependency that would create a circular dependency cycle.</li>
 *   <li>Adding a user to a project they are already a member of.</li>
 * </ul>
 *
 * <p>Usage:</p>
 * <pre>
 *   if (userRepository.existsByEmail(email)) {
 *       throw new BusinessRuleViolationException(
 *           "DUPLICATE_EMAIL",
 *           "An account with this email address already exists."
 *       );
 *   }
 * </pre>
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class BusinessRuleViolationException extends RuntimeException {

    private final String violationCode;

    /**
     * @param violationCode  Machine-readable code for the specific rule violated,
     *                       e.g. "DUPLICATE_EMAIL", "CIRCULAR_DEPENDENCY".
     *                       Exposed in the error response {@code code} field.
     * @param message        Human-readable description safe to return to the client.
     */
    public BusinessRuleViolationException(String violationCode, String message) {
        super(message);
        this.violationCode = violationCode;
    }

    public String getViolationCode() { return violationCode; }
}
