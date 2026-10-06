package com.hivecortex.entity;

/**
 * System roles for user authorization.
 *
 * PROJECT_MANAGER: Full administrative access.
 * DEVELOPER: Contributor access (view/update assigned tasks, sprint view, project brain).
 * STAKEHOLDER: Read-only access (progress, milestones, health scores).
 */
public enum Role {
    PROJECT_MANAGER,
    DEVELOPER,
    STAKEHOLDER
}
