// Device fingerprinting for login alerts
export async function getDeviceFingerprint(): Promise<string> {
  // CHANGE: Simulated fingerprint using browser APIs
  const components = [
    navigator.userAgent,
    navigator.language,
    new Date().getTimezoneOffset(),
    navigator.hardwareConcurrency || "unknown",
    navigator.deviceMemory || "unknown",
    screen.width,
    screen.height,
    screen.colorDepth,
  ]

  const fingerprint = components.join("|")
  const encoder = new TextEncoder()
  const data = encoder.encode(fingerprint)
  const hashBuffer = await crypto.subtle.digest("SHA-256", data)
  const hashArray = Array.from(new Uint8Array(hashBuffer))
  const hashHex = hashArray.map((b) => b.toString(16).padStart(2, "0")).join("")

  return hashHex.slice(0, 16)
}

export async function isNewDevice(storedFingerprint?: string): Promise<boolean> {
  const currentFingerprint = await getDeviceFingerprint()
  return !storedFingerprint || storedFingerprint !== currentFingerprint
}
