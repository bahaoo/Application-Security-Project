"use client"

import { useState } from "react"
import { DndContext, closestCorners, KeyboardSensor, PointerSensor, useSensor, useSensors } from "@dnd-kit/core"
import { sortableKeyboardCoordinates } from "@dnd-kit/sortable"
import { PipelineColumn } from "@/components/pipeline/pipeline-column"
import { Button } from "@/components/ui/button"
import { Plus } from "lucide-react"
import Link from "next/link"

export interface Candidate {
  id: string
  name: string
  email: string
  jobId: string
  stage: "applied" | "screening" | "interview" | "offer" | "hired"
  appliedDate: Date
  encryptedResume?: string
}

const STAGES = [
  { id: "applied", label: "Applied", color: "bg-blue-500" },
  { id: "screening", label: "Screening", color: "bg-cyan-500" },
  { id: "interview", label: "Interview", color: "bg-purple-500" },
  { id: "offer", label: "Offer", color: "bg-amber-500" },
  { id: "hired", label: "Hired", color: "bg-green-500" },
]

export default function PipelinePage() {
  const [candidates, setCandidates] = useState<Candidate[]>([
    {
      id: "1",
      name: "Sarah Johnson",
      email: "sarah@example.com",
      jobId: "job-1",
      stage: "screening",
      appliedDate: new Date("2024-01-15"),
    },
    {
      id: "2",
      name: "Mike Chen",
      email: "mike@example.com",
      jobId: "job-1",
      stage: "interview",
      appliedDate: new Date("2024-01-10"),
    },
    {
      id: "3",
      name: "Emma Davis",
      email: "emma@example.com",
      jobId: "job-2",
      stage: "applied",
      appliedDate: new Date("2024-01-20"),
    },
  ])

  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    }),
  )

  const handleDragEnd = (event: any) => {
    const { active, over } = event

    if (!over) return

    const activeId = active.id
    const overId = over.id

    // Extract stage from column ID
    const stageMatch = overId.match(/^stage-(.+)/)
    if (!stageMatch) return

    const newStage = stageMatch[1] as Candidate["stage"]

    setCandidates((prev) =>
      prev.map((candidate) => (candidate.id === activeId ? { ...candidate, stage: newStage } : candidate)),
    )
  }

  return (
    <div className="h-screen flex flex-col bg-background">
      <div className="flex items-center justify-between p-6 border-b border-border">
        <div>
          <h1 className="text-3xl font-bold text-foreground">Recruitment Pipeline</h1>
          <p className="text-muted-foreground mt-1">Manage your candidate workflow</p>
        </div>
        <Link href="/recruiter/jobs/new">
          <Button className="gap-2">
            <Plus className="w-4 h-4" />
            New Job
          </Button>
        </Link>
      </div>

      <div className="flex-1 overflow-x-auto p-6">
        <DndContext sensors={sensors} collisionDetection={closestCorners} onDragEnd={handleDragEnd}>
          <div className="flex gap-6">
            {STAGES.map((stage) => (
              <PipelineColumn
                key={stage.id}
                stage={stage}
                candidates={candidates.filter((c) => c.stage === stage.id)}
              />
            ))}
          </div>
        </DndContext>
      </div>
    </div>
  )
}
