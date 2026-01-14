// PSNR validation wrapper for Web Worker communication

export interface PSNRResult {
  psnr: number
  isValid: boolean
  mse?: number
  error?: string
}

let worker: Worker | null = null

function initWorker(): Worker {
  if (!worker && typeof window !== "undefined") {
    worker = new Worker(new URL("../public/workers/psnr-worker.ts", import.meta.url))
  }
  return worker!
}

export function calculatePSNR(originalData: ArrayBuffer, stegoData: ArrayBuffer): Promise<PSNRResult> {
  return new Promise((resolve, reject) => {
    try {
      const w = initWorker()

      const timeout = setTimeout(() => {
        reject(new Error("PSNR calculation timeout"))
      }, 5000)

      w.onmessage = (event: MessageEvent<PSNRResult>) => {
        clearTimeout(timeout)
        resolve(event.data)
      }

      w.onerror = (error) => {
        clearTimeout(timeout)
        reject(error)
      }

      // CHANGE: Send image data to worker for off-main-thread calculation
      w.postMessage(
        { originalData, stegoData },
        [originalData, stegoData], // Transfer ownership for better performance
      )
    } catch (error) {
      reject(error)
    }
  })
}

export function validateStegoImage(psnrValue: number): { valid: boolean; message: string } {
  if (psnrValue > 50) {
    return { valid: true, message: "Excellent quality - imperceptible differences" }
  }
  if (psnrValue > 45) {
    return { valid: true, message: "Good quality - minimal visual artifacts" }
  }
  if (psnrValue > 40) {
    return { valid: true, message: "Acceptable quality - minor artifacts visible" }
  }
  return { valid: false, message: `Quality too low: ${psnrValue.toFixed(1)}dB (minimum 45dB required)` }
}
