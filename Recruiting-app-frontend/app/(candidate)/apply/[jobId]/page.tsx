"use client"

import type React from "react"
import { useState } from "react"
import { useRouter, useParams } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Upload, Lock, CheckCircle } from "lucide-react"
import { encryptResume } from "@/lib/encryption"

export default function ApplyPage() {
  const router = useRouter()
  const params = useParams()
  const jobId = params.jobId as string

  const [fullName, setFullName] = useState("")
  const [email, setEmail] = useState("")
  const [resumeFile, setResumeFile] = useState<File | null>(null)
  const [uploadProgress, setUploadProgress] = useState(0)
  const [isEncrypting, setIsEncrypting] = useState(false)
  const [isSubmitted, setIsSubmitted] = useState(false)
  const [encryptionStatus, setEncryptionStatus] = useState("")

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      setResumeFile(e.target.files[0])
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!resumeFile) {
      alert("Please select a resume file")
      return
    }

    setIsEncrypting(true)
    setEncryptionStatus("Encrypting your resume with AES-256...")

    try {
      // Encrypt the resume
      const encryptedData = await encryptResume(resumeFile)
      setEncryptionStatus("Encryption complete. Submitting application...")

      // Simulate upload
      for (let i = 0; i <= 100; i += 10) {
        setUploadProgress(i)
        await new Promise((resolve) => setTimeout(resolve, 200))
      }

      setIsSubmitted(true)
      setTimeout(() => {
        router.push("/")
      }, 2000)
    } catch (error) {
      console.error("Encryption error:", error)
      setEncryptionStatus("Error encrypting resume")
    } finally {
      setIsEncrypting(false)
    }
  }

  if (isSubmitted) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background p-4">
        <Card className="max-w-md w-full p-8 text-center">
          <CheckCircle className="w-16 h-16 mx-auto text-green-500 mb-4" />
          <h2 className="text-2xl font-bold text-foreground mb-2">Application Submitted!</h2>
          <p className="text-muted-foreground mb-4">
            Your resume has been encrypted and submitted successfully. We'll be in touch soon!
          </p>
          <p className="text-sm text-muted-foreground">Redirecting...</p>
        </Card>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-background p-4 md:p-6">
      <div className="max-w-2xl mx-auto">
        <Card className="border-border">
          <div className="p-6 md:p-8">
            <h1 className="text-3xl font-bold text-foreground mb-2">Apply Now</h1>
            <p className="text-muted-foreground mb-8">Your resume will be encrypted end-to-end for maximum security</p>

            <form onSubmit={handleSubmit} className="space-y-6">
              <div>
                <label className="block text-sm font-medium text-foreground mb-2">Full Name</label>
                <Input
                  type="text"
                  placeholder="John Doe"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-foreground mb-2">Email</label>
                <Input
                  type="email"
                  placeholder="john@example.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-foreground mb-2">Upload Resume</label>
                <div className="relative border-2 border-dashed border-border rounded-lg p-8 text-center hover:border-primary/50 transition-colors cursor-pointer">
                  <input
                    type="file"
                    accept=".pdf,.doc,.docx"
                    onChange={handleFileChange}
                    disabled={isEncrypting}
                    className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
                  />
                  <Upload className="w-8 h-8 mx-auto text-muted-foreground mb-2" />
                  <p className="text-foreground font-medium">
                    {resumeFile ? resumeFile.name : "Click to upload or drag and drop"}
                  </p>
                  <p className="text-sm text-muted-foreground mt-1">PDF, DOC, or DOCX (Max 10MB)</p>
                </div>
              </div>

              {isEncrypting && (
                <div className="bg-card border border-border rounded-lg p-4">
                  <div className="flex items-center gap-2 mb-3">
                    <Lock className="w-4 h-4 text-primary" />
                    <span className="text-sm font-medium text-foreground">{encryptionStatus}</span>
                  </div>
                  <div className="w-full bg-muted rounded-full h-2">
                    <div
                      className="bg-primary h-2 rounded-full transition-all duration-300"
                      style={{ width: `${uploadProgress}%` }}
                    />
                  </div>
                </div>
              )}

              <Button type="submit" disabled={isEncrypting || !fullName || !email || !resumeFile} className="w-full">
                {isEncrypting ? "Submitting..." : "Submit Application"}
              </Button>
            </form>
          </div>
        </Card>
      </div>
    </div>
  )
}
