"use client"

import { useDroppable } from "@dnd-kit/core"
import { SortableContext, verticalListSortingStrategy } from "@dnd-kit/sortable"
import { CandidateCard } from "./candidate-card"
import type { Candidate } from "@/app/(recruiter)/pipeline/page"

interface PipelineColumnProps {
  stage: { id: string; label: string; color: string }
  candidates: Candidate[]
}

export function PipelineColumn({ stage, candidates }: PipelineColumnProps) {
  const { setNodeRef } = useDroppable({
    id: `stage-${stage.id}`,
  })

  return (
    <div
      ref={setNodeRef}
      className="flex flex-col w-80 flex-shrink-0 bg-card rounded-lg border border-border p-4 min-h-96"
    >
      <div className="flex items-center gap-2 mb-4">
        <div className={`w-3 h-3 rounded-full ${stage.color}`} />
        <h2 className="font-semibold text-foreground">{stage.label}</h2>
        <span className="ml-auto text-xs bg-muted text-muted-foreground px-2 py-1 rounded">{candidates.length}</span>
      </div>

      <SortableContext items={candidates.map((c) => c.id)} strategy={verticalListSortingStrategy}>
        <div className="flex flex-col gap-3">
          {candidates.map((candidate) => (
            <CandidateCard key={candidate.id} candidate={candidate} />
          ))}
        </div>
      </SortableContext>
    </div>
  )
}
