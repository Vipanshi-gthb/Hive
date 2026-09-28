import React, { useEffect, useState } from 'react'
import { healthCheck } from '../api/client'

// Status badge config — maps backend status string to visual treatment
const STATUS_CONFIG = {
  UP: {
    dot: 'bg-emerald-400',
    badge: 'bg-emerald-500/10 border-emerald-500/20 text-emerald-400',
    label: 'Backend Connected',
    icon: '✓',
  },
  DOWN: {
    dot: 'bg-red-400',
    badge: 'bg-red-500/10 border-red-500/20 text-red-400',
    label: 'Backend Unreachable',
    icon: '✕',
  },
  LOADING: {
    dot: 'bg-amber-400 animate-pulse',
    badge: 'bg-amber-500/10 border-amber-500/20 text-amber-400',
    label: 'Connecting…',
    icon: '○',
  },
}

export default function HomePage() {
  const [healthStatus, setHealthStatus] = useState('LOADING')
  const [errorMessage, setErrorMessage] = useState(null)
  const [lastChecked, setLastChecked] = useState(null)

  useEffect(() => {
    let cancelled = false

    async function fetchHealth() {
      setHealthStatus('LOADING')
      setErrorMessage(null)
      try {
        const data = await healthCheck()
        if (!cancelled) {
          // Backend returns { "status": "UP" }
          setHealthStatus(data?.status === 'UP' ? 'UP' : 'DOWN')
          setLastChecked(new Date())
        }
      } catch (err) {
        if (!cancelled) {
          setHealthStatus('DOWN')
          setErrorMessage(err.message)
          setLastChecked(new Date())
        }
      }
    }

    fetchHealth()
    return () => { cancelled = true }
  }, [])

  const cfg = STATUS_CONFIG[healthStatus]

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col items-center justify-center p-6">
      <div className="max-w-2xl w-full bg-slate-900/80 border border-slate-800 rounded-2xl p-8 shadow-2xl backdrop-blur-sm text-center space-y-6">

        {/* Status badge */}
        <div className={`inline-flex items-center gap-2 px-3 py-1 rounded-full border text-xs font-semibold uppercase tracking-wider ${cfg.badge}`}>
          <span className={`w-2 h-2 rounded-full ${cfg.dot}`} />
          {cfg.label}
        </div>

        {/* Title */}
        <h1 className="text-4xl md:text-5xl font-extrabold tracking-tight bg-gradient-to-r from-white via-slate-200 to-indigo-300 bg-clip-text text-transparent">
          Project Intelligence Platform
        </h1>

        <p className="text-slate-400 text-base md:text-lg leading-relaxed">
          AI-augmented project management with deterministic core intelligence and reviewable AI pipelines.
        </p>

        {/* Health card */}
        <div className="rounded-xl bg-slate-800/60 border border-slate-700/50 p-5 text-left space-y-3">
          <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider">
            Backend Health Check — <code className="text-indigo-400">GET /api/v1/health</code>
          </p>

          {healthStatus === 'LOADING' && (
            <div className="flex items-center gap-3 text-amber-400">
              <span className="w-4 h-4 rounded-full border-2 border-amber-400 border-t-transparent animate-spin" />
              <span className="text-sm">Calling backend…</span>
            </div>
          )}

          {healthStatus === 'UP' && (
            <div className="flex items-center gap-3">
              <span className="text-2xl text-emerald-400">✓</span>
              <div>
                <p className="text-sm font-semibold text-emerald-400">Status: UP</p>
                <p className="text-xs text-slate-500 mt-0.5">
                  Response received from backend — CORS configured correctly.
                </p>
              </div>
            </div>
          )}

          {healthStatus === 'DOWN' && (
            <div className="flex items-start gap-3">
              <span className="text-2xl text-red-400 mt-0.5">✕</span>
              <div className="space-y-1">
                <p className="text-sm font-semibold text-red-400">Backend unreachable</p>
                {errorMessage && (
                  <p className="text-xs text-slate-400 break-words">
                    {errorMessage}
                  </p>
                )}
                <p className="text-xs text-slate-500">
                  Ensure the Spring Boot backend is running on{' '}
                  <code className="text-slate-300">
                    {import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'}
                  </code>
                </p>
              </div>
            </div>
          )}

          {lastChecked && (
            <p className="text-xs text-slate-600 pt-1">
              Last checked: {lastChecked.toLocaleTimeString()}
            </p>
          )}
        </div>

        {/* Tech stack pills */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-left">
          {[
            { label: 'Framework', value: 'React 19 + Vite 8', color: 'text-slate-200' },
            { label: 'Styling', value: 'Tailwind CSS v4', color: 'text-emerald-400' },
            { label: 'Routing', value: 'React Router v7', color: 'text-indigo-400' },
            { label: 'Backend', value: 'Spring Boot 4.1', color: 'text-sky-400' },
          ].map(({ label, value, color }) => (
            <div key={label} className="p-3 rounded-xl bg-slate-800/60 border border-slate-700/50">
              <p className="text-xs text-slate-400 font-medium">{label}</p>
              <p className={`text-sm font-semibold mt-1 ${color}`}>{value}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
