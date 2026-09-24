import React from 'react'

export default function HomePage() {
  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col items-center justify-center p-6">
      <div className="max-w-2xl w-full bg-slate-900/80 border border-slate-800 rounded-2xl p-8 shadow-2xl backdrop-blur-sm text-center">
        <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-xs font-semibold uppercase tracking-wider mb-6">
          <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
          Frontend Scaffolded (Prompt 002)
        </div>
        
        <h1 className="text-4xl md:text-5xl font-extrabold tracking-tight bg-gradient-to-r from-white via-slate-200 to-indigo-300 bg-clip-text text-transparent mb-4">
          Project Intelligence Platform
        </h1>

        <p className="text-slate-400 text-base md:text-lg mb-8 leading-relaxed">
          AI-augmented project management platform with deterministic core intelligence and reviewable AI pipelines.
        </p>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 text-left">
          <div className="p-4 rounded-xl bg-slate-800/60 border border-slate-700/50">
            <p className="text-xs text-slate-400 font-medium">Framework</p>
            <p className="text-sm font-semibold text-slate-200 mt-1">React 19 + Vite</p>
          </div>
          <div className="p-4 rounded-xl bg-slate-800/60 border border-slate-700/50">
            <p className="text-xs text-slate-400 font-medium">Styling</p>
            <p className="text-sm font-semibold text-emerald-400 mt-1">Tailwind CSS v4</p>
          </div>
          <div className="p-4 rounded-xl bg-slate-800/60 border border-slate-700/50">
            <p className="text-xs text-slate-400 font-medium">Routing</p>
            <p className="text-sm font-semibold text-indigo-400 mt-1">React Router v7</p>
          </div>
        </div>
      </div>
    </div>
  )
}
