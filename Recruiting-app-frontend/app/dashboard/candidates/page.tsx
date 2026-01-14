"use client"

import { useState } from "react"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"

interface Candidate {
  id: string
  name: string
  email: string
  position: string
  stage: "screening" | "interview" | "offer" | "hired" | "rejected"
  appliedDate: string
}

const mockCandidates: Candidate[] = [
  {
    id: "1",
    name: "Sarah Chen",
    email: "sarah.chen@email.com",
    position: "Senior Software Engineer",
    stage: "interview",
    appliedDate: "2024-01-10",
  },
  {
    id: "2",
    name: "Michael Rodriguez",
    email: "m.rodriguez@email.com",
    position: "Product Manager",
    stage: "screening",
    appliedDate: "2024-01-12",
  },
  {
    id: "3",
    name: "Emily Watson",
    email: "emily.watson@email.com",
    position: "UX Designer",
    stage: "offer",
    appliedDate: "2024-01-08",
  },
  {
    id: "4",
    name: "David Kim",
    email: "david.kim@email.com",
    position: "Data Analyst",
    stage: "hired",
    appliedDate: "2024-01-02",
  },
]

const stageColors = {
  screening: "bg-blue-500/20 text-blue-400",
  interview: "bg-purple-500/20 text-purple-400",
  offer: "bg-amber-500/20 text-amber-400",
  hired: "bg-green-500/20 text-green-400",
  rejected: "bg-red-500/20 text-red-400",
}

export default function CandidatesPage() {
  const [searchQuery, setSearchQuery] = useState("")

  const filteredCandidates = mockCandidates.filter(
    (candidate) =>
      candidate.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      candidate.email.toLowerCase().includes(searchQuery.toLowerCase()) ||
      candidate.position.toLowerCase().includes(searchQuery.toLowerCase()),
  )

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-foreground">Candidates</h1>
          <p className="text-muted-foreground mt-1">Manage your recruitment pipeline securely</p>
        </div>
        <Button className="bg-primary hover:bg-primary/90 text-primary-foreground">+ Add Candidate</Button>
      </div>

      <Card className="bg-card border-border">
        <CardHeader className="pb-4">
          <CardTitle className="text-foreground">Search & Filter</CardTitle>
        </CardHeader>
        <CardContent>
          <Input
            placeholder="Search by name, email, or position..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="bg-input border-border text-foreground"
          />
        </CardContent>
      </Card>

      <Card className="bg-card border-border">
        <CardHeader>
          <CardTitle className="text-foreground">Candidates ({filteredCandidates.length})</CardTitle>
          <CardDescription className="text-muted-foreground">All candidates in your pipeline</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            {filteredCandidates.map((candidate) => (
              <div
                key={candidate.id}
                className="flex items-center justify-between p-4 bg-background rounded-lg border border-border hover:border-primary/50 transition-colors cursor-pointer"
              >
                <div className="flex-1">
                  <p className="font-medium text-foreground">{candidate.name}</p>
                  <p className="text-sm text-muted-foreground">{candidate.email}</p>
                  <p className="text-sm text-muted-foreground mt-1">{candidate.position}</p>
                </div>
                <div className="flex items-center gap-4">
                  <div className="text-right">
                    <p className="text-xs text-muted-foreground">Applied</p>
                    <p className="text-sm text-foreground">{candidate.appliedDate}</p>
                  </div>
                  <span className={`px-3 py-1 rounded-full text-xs font-medium ${stageColors[candidate.stage]}`}>
                    {candidate.stage.charAt(0).toUpperCase() + candidate.stage.slice(1)}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
