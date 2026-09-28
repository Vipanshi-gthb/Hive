/**
 * API Client — central module for all backend HTTP calls.
 *
 * Base URL is read from the VITE_API_BASE_URL environment variable.
 * In development this is http://localhost:8080 (set in frontend/.env).
 * In production it is the deployed backend URL (set in the host's env config).
 *
 * Usage:
 *   import { healthCheck } from './api/client'
 *   const { status } = await healthCheck()
 */

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

/**
 * Core fetch wrapper. Throws a structured Error on non-2xx responses
 * so callers get a consistent failure shape regardless of HTTP status.
 *
 * @param {string} path - Path relative to BASE_URL, e.g. '/api/v1/health'
 * @param {RequestInit} [options] - Standard fetch options
 * @returns {Promise<any>} Parsed JSON response body
 */
async function request(path, options = {}) {
  const url = `${BASE_URL}${path}`

  const defaults = {
    headers: {
      'Content-Type': 'application/json',
      Accept: 'application/json',
      ...options.headers,
    },
  }

  let response
  try {
    response = await fetch(url, { ...defaults, ...options })
  } catch (networkError) {
    // Network-level failure (backend unreachable, DNS failure, etc.)
    throw new Error(
      `Cannot reach the backend at ${BASE_URL}. Is the server running? (${networkError.message})`
    )
  }

  if (!response.ok) {
    // Attempt to parse a structured error body from the backend.
    let errorBody
    try {
      errorBody = await response.json()
    } catch {
      errorBody = null
    }

    const message =
      errorBody?.error?.message ??
      errorBody?.message ??
      `HTTP ${response.status} ${response.statusText}`

    const err = new Error(message)
    err.status = response.status
    err.body = errorBody
    throw err
  }

  // 204 No Content — return null instead of trying to parse an empty body.
  if (response.status === 204) return null

  return response.json()
}

// ---------------------------------------------------------------------------
// Health
// ---------------------------------------------------------------------------

/**
 * GET /api/v1/health
 * @returns {Promise<{ status: string }>}
 */
export async function healthCheck() {
  return request('/api/v1/health')
}

// ---------------------------------------------------------------------------
// Auth (added in Prompt 009)
// ---------------------------------------------------------------------------

// export async function register(data) { ... }
// export async function login(data) { ... }

export default { healthCheck }
