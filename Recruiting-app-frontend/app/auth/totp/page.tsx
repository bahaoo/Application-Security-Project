"use client"

import type React from "react"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"

export default function TOTPEnrollmentPage() {
  const [totpCode, setTotpCode] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [step, setStep] = useState<"setup" | "verify">("setup")

  const handleVerify = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    setTimeout(() => {
      setIsLoading(false)
      setStep("verify")
    }, 800)
  }

  const handleConfirm = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    setTimeout(() => setIsLoading(false), 1000)
  }

  return (
    <div className="min-h-screen w-full flex items-center justify-center bg-background p-4">
      <Card className="w-full max-w-md bg-card border-border">
        <CardHeader className="space-y-1">
          <div className="flex items-center justify-center mb-4">
            <div className="w-12 h-12 rounded-lg bg-primary flex items-center justify-center">
              <span className="text-xl font-bold text-primary-foreground">🔐</span>
            </div>
          </div>
          <CardTitle className="text-2xl text-center text-foreground">Set Up Two-Factor Auth</CardTitle>
          <CardDescription className="text-center text-muted-foreground">
            {step === "setup"
              ? "Secure your account with time-based authentication"
              : "Enter the code from your authenticator app"}
          </CardDescription>
        </CardHeader>
        <CardContent>
          {step === "setup" ? (
            <form onSubmit={handleVerify} className="space-y-4">
              <div className="bg-muted p-4 rounded-lg space-y-4">
                <p className="text-sm text-foreground">
                  Use an authenticator app like Google Authenticator, Authy, or Microsoft Authenticator
                </p>
                <div className="w-32 h-32 bg-background rounded-lg mx-auto flex items-center justify-center border border-border">
                  <span className="text-muted-foreground text-sm">QR Code</span>
                </div>
                <p className="text-xs text-muted-foreground text-center">
                  Can't scan? Enter backup key:{" "}
                  <code className="bg-background px-2 py-1 rounded font-mono">JBSWY3DPEBLW64TMMQ======</code>
                </p>
              </div>

              <Button type="submit" className="w-full" disabled={isLoading}>
                {isLoading ? "Generating..." : "Next"}
              </Button>
            </form>
          ) : (
            <form onSubmit={handleConfirm} className="space-y-4">
              <div className="space-y-2">
                <label className="text-sm font-medium text-foreground">Enter 6-digit code</label>
                <Input
                  type="text"
                  placeholder="000000"
                  value={totpCode}
                  onChange={(e) => setTotpCode(e.target.value.slice(0, 6))}
                  maxLength={6}
                  className="text-center text-lg tracking-widest bg-input border-border text-foreground"
                  disabled={isLoading}
                  required
                />
              </div>

              <Button type="submit" className="w-full" disabled={isLoading || totpCode.length !== 6}>
                {isLoading ? "Verifying..." : "Verify & Enable"}
              </Button>

              <Button
                type="button"
                variant="ghost"
                className="w-full"
                onClick={() => setStep("setup")}
                disabled={isLoading}
              >
                Back
              </Button>
            </form>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
