"use client"

import { useSortable } from "@dnd-kit/sortable"
import { CSS } from "@dnd-kit/utilities"
import type { Candidate } from "@/app/(recruiter)/pipeline/page"
import { Mail, Calendar } from "lucide-react"

interface CandidateCardProps {
  candidate: Candidate
}

export function CandidateCard({ candidate }: CandidateCardProps) {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({
    id: candidate.id,
  })

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
  }

  return (
    <div
      ref={setNodeRef}
      style={style}
      {...attributes}
      {...listeners}
      className="bg-background border border-border rounded-lg p-3 cursor-grab active:cursor-grabbing hover:bg-muted/50 transition-colors"
    >
      <h3 className="font-medium text-foreground text-sm">{candidate.name}</h3>
      <div className="flex items-center gap-2 mt-2 text-xs text-muted-foreground">
        <Mail className="w-3 h-3" />
        <span className="truncate">{candidate.email}</span>
      </div>
      <div className="flex items-center gap-2 mt-2 text-xs text-muted-foreground">
        <Calendar className="w-3 h-3" />
        <span>{candidate.appliedDate.toLocaleDateString()}</span>
      </div>
    </div>
  )
}
