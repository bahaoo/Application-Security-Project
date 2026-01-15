"use client"

import { useState, useEffect, useRef } from "react"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Textarea } from "@/components/ui/textarea"
import { MessageSquare, Send } from "lucide-react"

interface Comment {
  id: string
  userId: string
  userName: string
  content: string
  mentions: string[]
  createdAt: string
}

export default function CommentsPage({ params }: { params: { id: string } }) {
  const [comments, setComments] = useState<Comment[]>([])
  const [content, setContent] = useState("")
  const [mentions, setMentions] = useState<string[]>([])
  const [loading, setLoading] = useState(true)
  const textareaRef = useRef<HTMLTextAreaElement>(null)

  useEffect(() => {
    const fetchComments = async () => {
      try {
        const response = await fetch(`/api/candidates/${params.id}/comments`)
        const data = await response.json()
        setComments(data.comments)
      } catch (error) {
        console.error("Failed to fetch comments:", error)
      } finally {
        setLoading(false)
      }
    }

    fetchComments()
  }, [params.id])

  const handleContentChange = (text: string) => {
    setContent(text)

    // Parse mentions (@username)
    const mentionPattern = /@(\w+)/g
    const foundMentions = []
    let match

    while ((match = mentionPattern.exec(text)) !== null) {
      foundMentions.push(match[1])
    }

    setMentions(foundMentions)
  }

  const handleSubmit = async () => {
    if (!content.trim()) return

    try {
      const response = await fetch(`/api/candidates/${params.id}/comments`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          userId: "current-user-id",
          userName: "Current User",
          content,
          mentions,
        }),
      })

      const newComment = await response.json()
      setComments([...comments, newComment])
      setContent("")
      setMentions([])
    } catch (error) {
      console.error("Failed to post comment:", error)
    }
  }

  return (
    <div className="max-w-4xl mx-auto p-6 space-y-6">
      <div className="flex items-center gap-2">
        <MessageSquare className="w-6 h-6 text-primary" />
        <h1 className="text-3xl font-bold text-foreground">Candidate Comments</h1>
      </div>

      {/* Comments Thread */}
      <Card className="p-6 space-y-4">
        {loading ? (
          <p className="text-muted-foreground">Loading comments...</p>
        ) : comments.length === 0 ? (
          <p className="text-muted-foreground text-center py-8">No comments yet</p>
        ) : (
          comments.map((comment) => (
            <div key={comment.id} className="border-l-2 border-primary pl-4 py-2">
              <div className="flex items-center gap-2 mb-1">
                <span className="font-semibold text-foreground">{comment.userName}</span>
                <span className="text-xs text-muted-foreground">{new Date(comment.createdAt).toLocaleString()}</span>
              </div>
              <p className="text-foreground whitespace-pre-wrap break-words">{comment.content}</p>
              {comment.mentions.length > 0 && (
                <div className="mt-2 flex flex-wrap gap-2">
                  {comment.mentions.map((mention) => (
                    <span key={mention} className="inline-block bg-primary/10 text-primary text-xs px-2 py-1 rounded">
                      @{mention}
                    </span>
                  ))}
                </div>
              )}
            </div>
          ))
        )}
      </Card>

      {/* Comment Input */}
      <Card className="p-6 space-y-3">
        <h2 className="font-semibold text-foreground">Add Comment</h2>
        <Textarea
          ref={textareaRef}
          placeholder="Type your comment... (use @name to mention)"
          value={content}
          onChange={(e) => handleContentChange(e.target.value)}
          className="min-h-24 resize-none"
        />
        <div className="flex items-center justify-between">
          {mentions.length > 0 && (
            <div className="flex gap-1 flex-wrap">
              {mentions.map((mention) => (
                <span key={mention} className="bg-primary/10 text-primary text-xs px-2 py-1 rounded">
                  @{mention}
                </span>
              ))}
            </div>
          )}
          <Button onClick={handleSubmit} disabled={!content.trim()} className="gap-2">
            <Send className="w-4 h-4" />
            Post Comment
          </Button>
        </div>
      </Card>
    </div>
  )
}
