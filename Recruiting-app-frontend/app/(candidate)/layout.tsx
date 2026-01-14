"use client"

import type React from "react"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { LogOut, Home } from "lucide-react"

export default function CandidateLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <div className="min-h-screen bg-background flex flex-col">
      <header className="border-b border-border bg-card">
        <div className="flex items-center justify-between p-4 md:p-6 max-w-7xl mx-auto w-full">
          <Link href="/" className="flex items-center gap-2">
            <div className="w-10 h-10 rounded-lg bg-primary flex items-center justify-center">
              <span className="text-lg font-bold text-primary-foreground">SR</span>
            </div>
            <div className="flex flex-col">
              <span className="font-semibold text-foreground">SecureRecruit</span>
              <span className="text-xs text-muted-foreground">Candidate Portal</span>
            </div>
          </Link>
          <div className="flex items-center gap-3">
            <Link href="/">
              <Button variant="ghost" size="sm" className="gap-2">
                <Home className="w-4 h-4" />
                Back Home
              </Button>
            </Link>
            <Button variant="ghost" size="sm" className="gap-2">
              <LogOut className="w-4 h-4" />
              Sign Out
            </Button>
          </div>
        </div>
      </header>
      <main className="flex-1">{children}</main>
    </div>
  )
}
