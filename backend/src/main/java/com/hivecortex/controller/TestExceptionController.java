package com.hivecortex.controller;

import com.hivecortex.exception.BusinessRuleViolationException;
import com.hivecortex.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ============================================================
 * TEMPORARY — PROMPT 006 TESTING ONLY.
 * Remove this controller after verifying exception handling.
 * TODO: DELETE before Prompt 007.
 * ============================================================
 *
 * Test endpoints for manually verifying GlobalExceptionHandler
 * maps each exception to the correct HTTP status and JSON shape.
 *
 * Test commands (run with backend started):
 *
 *   # 404 — ResourceNotFoundException
 *   curl http://localhost:8080/api/v1/test-errors/not-found
 *
 *   # 409 — BusinessRuleViolationException
 *   curl http://localhost:8080/api/v1/test-errors/conflict
 *
 *   # 500 — unhandled Exception
 *   curl http://localhost:8080/api/v1/test-errors/server-error
 *
 *   # 400 — Bean Validation failure (MethodArgumentNotValidException)
 *   curl -X POST http://localhost:8080/api/v1/test-errors/validation \
 *        -H "Content-Type: application/json" \
 *        -d '{"email":"not-an-email","name":""}'
 *
 *   # 400 — malformed JSON body
 *   curl -X POST http://localhost:8080/api/v1/test-errors/validation \
 *        -H "Content-Type: application/json" \
 *        -d 'INVALID JSON'
 *
 *   # 404 — unmapped route (any path not registered)
 *   curl http://localhost:8080/api/v1/does-not-exist
 *
 *   # 405 — wrong HTTP method
 *   curl -X DELETE http://localhost:8080/api/v1/health
 */
@RestController
@RequestMapping("/api/v1/test-errors")
public class TestExceptionController {

    // DTO used only by the validation test endpoint
    record TestRequest(
            @NotBlank(message = "name must not be blank")
            @Size(min = 2, max = 50, message = "name must be between 2 and 50 characters")
            String name,

            @NotBlank(message = "email must not be blank")
            @Email(message = "email must be a valid email address")
            String email
    ) {}

    @GetMapping("/not-found")
    public ResponseEntity<?> triggerNotFound() {
        throw new ResourceNotFoundException("Project", "nonexistent-id-123");
    }

    @GetMapping("/conflict")
    public ResponseEntity<?> triggerConflict() {
        throw new BusinessRuleViolationException(
                "DUPLICATE_EMAIL",
                "An account with this email address already exists."
        );
    }

    @GetMapping("/server-error")
    public ResponseEntity<?> triggerServerError() {
        // Simulates an unhandled exception — only safe generic message should reach client.
        throw new RuntimeException("Sensitive internal detail that must NOT appear in the API response");
    }

    @PostMapping("/validation")
    public ResponseEntity<?> triggerValidation(@Valid @RequestBody TestRequest request) {
        // If validation passes, echo back the request to confirm well-formed input.
        return ResponseEntity.ok(Map.of(
                "message", "Validation passed",
                "received", request
        ));
    }
}
