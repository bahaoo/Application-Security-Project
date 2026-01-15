"use client"

import type React from "react"

import { useState, useRef, useCallback } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Progress } from "@/components/ui/progress"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Upload, Download, AlertCircle, CheckCircle2 } from "lucide-react"
import { calculatePSNR, validateStegoImage } from "@/lib/psnr-validator"

interface SecureSendDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  resumeFile?: File
}

export function SecureSendDialog({ open, onOpenChange, resumeFile }: SecureSendDialogProps) {
  const [coverImage, setCoverImage] = useState<File | null>(null)
  const [previewUrl, setPreviewUrl] = useState<string>("")
  const [embedding, setEmbedding] = useState(false)
  const [progress, setProgress] = useState(0)
  const [result, setResult] = useState<{ success: boolean; psnr?: number; message: string } | null>(null)
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const fileInputRef = useRef<HTMLInputElement>(null)

  const handleImageSelect = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (!file) return

    setCoverImage(file)
    const reader = new FileReader()
    reader.onload = (event) => {
      setPreviewUrl(event.target?.result as string)
    }
    reader.readAsDataURL(file)
  }, [])

  const embedResume = async () => {
    if (!coverImage || !resumeFile) return

    setEmbedding(true)
    setProgress(0)

    // CHANGE: Integrate Web Worker-based PSNR validation
    try {
      setProgress(20)
      const coverBuffer = await coverImage.arrayBuffer()
      const resumeBuffer = await resumeFile.arrayBuffer()

      setProgress(40)
      // Simulate LSB embedding
      const stegoBuffer = new ArrayBuffer(coverBuffer.byteLength)
      const coverView = new Uint8Array(coverBuffer)
      const stegoView = new Uint8Array(stegoBuffer)
      stegoView.set(coverView)

      // Embed resume data in LSBs
      const resumeView = new Uint8Array(resumeBuffer)
      for (let i = 0; i < resumeView.length && i < stegoView.length; i++) {
        stegoView[i] = (stegoView[i] & 0xf0) | (resumeView[i] & 0x0f)
      }

      setProgress(60)

      const psnrResult = await calculatePSNR(coverBuffer, stegoBuffer)

      setProgress(90)
      const validation = validateStegoImage(psnrResult.psnr)

      setProgress(100)

      setResult({
        success: psnrResult.isValid && validation.valid,
        psnr: psnrResult.psnr,
        message: validation.message,
      })
    } catch (error) {
      setResult({
        success: false,
        message: "Embedding failed: " + (error as Error).message,
      })
    } finally {
      setEmbedding(false)
    }
  }

  const downloadStegoImage = () => {
    if (!canvasRef.current) return
    const link = document.createElement("a")
    link.href = canvasRef.current.toDataURL("image/png")
    link.download = "resume-stego.png"
    link.click()
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle>Secure Resume Delivery</DialogTitle>
          <DialogDescription>Embed resume in image using steganography (LSB)</DialogDescription>
        </DialogHeader>

        <div className="space-y-4">
          {/* Image Upload Section */}
          <div className="space-y-2">
            <label className="text-sm font-medium">Cover Image</label>
            <div
              className="border-2 border-dashed border-border rounded-lg p-8 text-center cursor-pointer hover:bg-muted/50 transition"
              onClick={() => fileInputRef.current?.click()}
            >
              <input ref={fileInputRef} type="file" accept="image/*" onChange={handleImageSelect} className="hidden" />
              <Upload className="w-8 h-8 mx-auto mb-2 text-muted-foreground" />
              <p className="text-sm text-muted-foreground">{coverImage ? coverImage.name : "Click to select image"}</p>
            </div>
          </div>

          {/* Preview */}
          {previewUrl && (
            <div className="space-y-2">
              <label className="text-sm font-medium">Preview</label>
              <div className="relative w-full h-48 bg-muted rounded-lg overflow-hidden">
                <img src={previewUrl || "/placeholder.svg"} alt="Cover" className="w-full h-full object-cover" />
                <canvas ref={canvasRef} className="hidden" />
              </div>
            </div>
          )}

          {/* Progress */}
          {embedding && (
            <div className="space-y-2">
              <div className="flex justify-between text-sm">
                <span>Embedding resume...</span>
                <span>{progress}%</span>
              </div>
              <Progress value={progress} />
            </div>
          )}

          {/* Result */}
          {result && (
            <Alert variant={result.success ? "default" : "destructive"}>
              <div className="flex gap-2">
                {result.success ? <CheckCircle2 className="w-4 h-4" /> : <AlertCircle className="w-4 h-4" />}
                <AlertDescription>{result.message}</AlertDescription>
              </div>
            </Alert>
          )}

          {/* Actions */}
          <div className="flex gap-2 justify-end">
            <Button variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button onClick={embedResume} disabled={!coverImage || !resumeFile || embedding}>
              {embedding ? "Embedding..." : "Embed Resume"}
            </Button>
            {result?.success && (
              <Button onClick={downloadStegoImage} variant="secondary" gap-2>
                <Download className="w-4 h-4" />
                Download
              </Button>
            )}
          </div>
        </div>
      </DialogContent>
    </Dialog>
  )
}
