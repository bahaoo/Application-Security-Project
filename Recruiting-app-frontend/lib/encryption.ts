// Client-side AES-256 encryption using Web Crypto API
export async function encryptResume(file: File): Promise<string> {
  return new Promise(async (resolve, reject) => {
    try {
      const buffer = await file.arrayBuffer()
      const key = await crypto.subtle.generateKey({ name: "AES-GCM", length: 256 }, true, ["encrypt", "decrypt"])
      const iv = crypto.getRandomValues(new Uint8Array(12))
      const encryptedData = await crypto.subtle.encrypt({ name: "AES-GCM", iv }, key, buffer)
      const exportedKey = await crypto.subtle.exportKey("jwk", key)
      const combined = new Uint8Array(iv.length + encryptedData.byteLength)
      combined.set(new Uint8Array(iv), 0)
      combined.set(new Uint8Array(encryptedData), iv.length)
      const base64Data = btoa(String.fromCharCode(...combined))
      const encryptedPayload = JSON.stringify({
        encrypted_data: base64Data,
        key: exportedKey,
        algorithm: "AES-256-GCM",
      })
      resolve(encryptedPayload)
    } catch (error) {
      reject(error)
    }
  })
}

export async function decryptResume(encryptedPayload: string): Promise<ArrayBuffer> {
  try {
    const { encrypted_data, key: keyData } = JSON.parse(encryptedPayload)
    const binaryString = atob(encrypted_data)
    const bytes = new Uint8Array(binaryString.length)
    for (let i = 0; i < binaryString.length; i++) {
      bytes[i] = binaryString.charCodeAt(i)
    }
    const iv = bytes.slice(0, 12)
    const encryptedData = bytes.slice(12)
    const key = await crypto.subtle.importKey("jwk", keyData, { name: "AES-GCM" }, false, ["decrypt"])
    const decrypted = await crypto.subtle.decrypt({ name: "AES-GCM", iv }, key, encryptedData)
    return decrypted
  } catch (error) {
    throw new Error("Failed to decrypt resume: " + (error as Error).message)
  }
}
