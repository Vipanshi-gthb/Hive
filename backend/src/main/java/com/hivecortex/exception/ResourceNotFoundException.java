package com.hivecortex.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a requested resource does not exist in the database.
 *
 * <p>Maps to HTTP 404 Not Found via {@link GlobalExceptionHandler}.</p>
 *
 * <p>Usage:</p>
 * <pre>
 *   Project project = projectRepository.findById(id)
 *       .orElseThrow(() -> new ResourceNotFoundException("Project", id));
 * </pre>
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final Object resourceId;

    /**
     * @param resourceName Human-readable entity name, e.g. "Project", "Task".
     * @param resourceId   The identifier that was not found (UUID, String, etc.).
     */
    public ResourceNotFoundException(String resourceName, Object resourceId) {
        super(String.format("%s with id '%s' was not found.", resourceName, resourceId));
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }

    /**
     * Free-form message variant for cases where id-based messaging doesn't fit.
     *
     * @param message Fully formed message, e.g. "No active sprint found for project 'X'."
     */
    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = null;
        this.resourceId = null;
    }

    public String getResourceName() { return resourceName; }
    public Object getResourceId() { return resourceId; }
}
