interface SyncAction {
  id: string
  type: "update_candidate" | "create_job" | "submit_application"
  data: any
  timestamp: number
  retries: number
}

const STORAGE_KEY = "secure-recruit-sync-queue"

export async function addToSyncQueue(action: Omit<SyncAction, "id" | "timestamp" | "retries">): Promise<void> {
  const queue = getSyncQueue()
  const newAction: SyncAction = {
    ...action,
    id: `sync-${Date.now()}`,
    timestamp: Date.now(),
    retries: 0,
  }
  queue.push(newAction)
  if (typeof window !== "undefined" && "indexedDB" in window) {
    const db = await openSyncDatabase()
    const tx = db.transaction("sync_queue", "readwrite")
    await tx.objectStore("sync_queue").put({ queue })
  }
}

export function getSyncQueue(): SyncAction[] {
  if (typeof window === "undefined") return []
  const stored = localStorage.getItem(STORAGE_KEY)
  return stored ? JSON.parse(stored) : []
}

export async function processSyncQueue(): Promise<void> {
  const queue = getSyncQueue()
  for (const action of queue) {
    try {
      removeSyncAction(action.id)
    } catch (error) {
      action.retries++
      if (action.retries > 3) {
        removeSyncAction(action.id)
      }
    }
  }
}

function removeSyncAction(id: string): void {
  const queue = getSyncQueue().filter((a) => a.id !== id)
  if (typeof window !== "undefined") {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(queue))
  }
}

async function openSyncDatabase(): Promise<IDBDatabase> {
  return new Promise((resolve, reject) => {
    const request = indexedDB.open("SecureRecruitDB", 1)
    request.onerror = () => reject(request.error)
    request.onsuccess = () => resolve(request.result)
    request.onupgradeneeded = (e) => {
      const db = (e.target as IDBOpenDBRequest).result
      if (!db.objectStoreNames.contains("sync_queue")) {
        db.createObjectStore("sync_queue", { keyPath: "id" })
      }
    }
  })
}
