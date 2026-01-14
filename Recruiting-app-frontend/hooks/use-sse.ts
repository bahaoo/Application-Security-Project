"use client"

import { useEffect, useRef, useCallback } from "react"

interface SSEEvent {
  type: string
  data: any
  timestamp: string
}

export function useSSE(onMessage: (event: SSEEvent) => void, url = "/api/pipeline/sse") {
  const eventSourceRef = useRef<EventSource | null>(null)
  const reconnectAttempts = useRef(0)
  const maxReconnectAttempts = 5

  const connect = useCallback(() => {
    if (eventSourceRef.current) return

    try {
      eventSourceRef.current = new EventSource(url)

      eventSourceRef.current.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data)
          onMessage(data)
          reconnectAttempts.current = 0
        } catch (error) {
          console.error("Failed to parse SSE message:", error)
        }
      }

      eventSourceRef.current.onerror = () => {
        eventSourceRef.current?.close()
        eventSourceRef.current = null

        // Exponential backoff retry
        if (reconnectAttempts.current < maxReconnectAttempts) {
          const delay = Math.pow(2, reconnectAttempts.current) * 1000
          reconnectAttempts.current++
          setTimeout(connect, delay)
        }
      }
    } catch (error) {
      console.error("Failed to create EventSource:", error)
    }
  }, [url, onMessage])

  useEffect(() => {
    connect()

    return () => {
      eventSourceRef.current?.close()
      eventSourceRef.current = null
    }
  }, [connect])
}
