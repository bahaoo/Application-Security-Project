"use client"

import { useState } from "react"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Star, Save } from "lucide-react"

interface ScorecardItem {
  id: string
  category: string
  score: number
  feedback: string
}

export default function ScorecardPage({ params }: { params: { id: string } }) {
  const [scorecard, setScorecard] = useState<ScorecardItem[]>([
    { id: "1", category: "Technical Skills", score: 0, feedback: "" },
    { id: "2", category: "Communication", score: 0, feedback: "" },
    { id: "3", category: "Problem Solving", score: 0, feedback: "" },
    { id: "4", category: "Cultural Fit", score: 0, feedback: "" },
  ])

  const updateScore = (id: string, score: number) => {
    setScorecard((prev) => prev.map((item) => (item.id === id ? { ...item, score } : item)))
  }

  const updateFeedback = (id: string, feedback: string) => {
    setScorecard((prev) => prev.map((item) => (item.id === id ? { ...item, feedback } : item)))
  }

  const avgScore = scorecard.reduce((sum, item) => sum + item.score, 0) / scorecard.length

  return (
    <div className="max-w-2xl mx-auto p-6 space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold text-foreground">Interview Scorecard</h1>
        <Button className="gap-2">
          <Save className="w-4 h-4" />
          Save Scorecard
        </Button>
      </div>

      {/* Average Score */}
      <Card className="p-6 bg-gradient-to-br from-primary/10 to-primary/5 border-primary/20">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-muted-foreground">Overall Score</p>
            <p className="text-4xl font-bold text-primary">{avgScore.toFixed(1)}/5</p>
          </div>
          <div className="flex gap-1">
            {[...Array(5)].map((_, i) => (
              <Star
                key={i}
                className={`w-8 h-8 ${i < Math.round(avgScore) ? "fill-primary text-primary" : "text-muted-foreground"}`}
              />
            ))}
          </div>
        </div>
      </Card>

      {/* Scorecard Items */}
      <div className="space-y-4">
        {scorecard.map((item) => (
          <Card key={item.id} className="p-6 space-y-4">
            <h3 className="font-semibold text-foreground">{item.category}</h3>

            <div className="flex gap-2">
              {[1, 2, 3, 4, 5].map((score) => (
                <button
                  key={score}
                  onClick={() => updateScore(item.id, score)}
                  className="p-1 hover:scale-110 transition-transform"
                >
                  <Star
                    className={`w-6 h-6 ${score <= item.score ? "fill-primary text-primary" : "text-muted-foreground"}`}
                  />
                </button>
              ))}
            </div>

            {/* Feedback */}
            <Input
              placeholder="Add feedback..."
              value={item.feedback}
              onChange={(e) => updateFeedback(item.id, e.target.value)}
              className="bg-input text-foreground border-border"
            />
          </Card>
        ))}
      </div>
    </div>
  )
}
