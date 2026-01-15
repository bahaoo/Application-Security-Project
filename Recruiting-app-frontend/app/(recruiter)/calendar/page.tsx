"use client"

import { useState } from "react"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { ChevronLeft, ChevronRight, Calendar, Plus } from "lucide-react"

interface Interview {
  id: string
  candidateName: string
  scheduledTime: string
  duration: number
  videoLink: string
  status: "scheduled" | "completed" | "cancelled"
}

export default function CalendarPage() {
  const [currentDate, setCurrentDate] = useState(new Date(2024, 0, 1))
  const [interviews, setInterviews] = useState<Interview[]>([
    {
      id: "1",
      candidateName: "Sarah Johnson",
      scheduledTime: "2024-01-15T10:00:00",
      duration: 60,
      videoLink: "https://secure-recruit.app/video/abc123",
      status: "scheduled",
    },
    {
      id: "2",
      candidateName: "Mike Chen",
      scheduledTime: "2024-01-18T14:00:00",
      duration: 45,
      videoLink: "https://secure-recruit.app/video/def456",
      status: "scheduled",
    },
  ])

  const getInterviewsForDate = (date: Date) => {
    return interviews.filter((interview) => {
      const interviewDate = new Date(interview.scheduledTime)
      return interviewDate.getMonth() === date.getMonth() && interviewDate.getFullYear() === date.getFullYear()
    })
  }

  const monthInterviews = getInterviewsForDate(currentDate)
  const daysInMonth = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 0).getDate()
  const firstDayOfMonth = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1).getDay()

  const handlePrevMonth = () => {
    setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() - 1))
  }

  const handleNextMonth = () => {
    setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() + 1))
  }

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold text-foreground flex items-center gap-2">
          <Calendar className="w-8 h-8 text-primary" />
          Interview Schedule
        </h1>
        <Button className="gap-2">
          <Plus className="w-4 h-4" />
          Schedule Interview
        </Button>
      </div>

      <div className="grid grid-cols-3 gap-6">
        {/* Calendar */}
        <Card className="col-span-2 p-6 space-y-6">
          {/* Calendar Header */}
          <div className="flex items-center justify-between">
            <h2 className="font-semibold text-foreground">
              {currentDate.toLocaleString("default", { month: "long", year: "numeric" })}
            </h2>
            <div className="flex gap-2">
              <Button size="sm" variant="outline" onClick={handlePrevMonth}>
                <ChevronLeft className="w-4 h-4" />
              </Button>
              <Button size="sm" variant="outline" onClick={handleNextMonth}>
                <ChevronRight className="w-4 h-4" />
              </Button>
            </div>
          </div>

          <div className="space-y-2">
            {/* Day headers */}
            <div className="grid grid-cols-7 gap-2 mb-2">
              {["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"].map((day) => (
                <div key={day} className="text-center text-sm font-semibold text-muted-foreground">
                  {day}
                </div>
              ))}
            </div>

            {/* Calendar days */}
            <div className="grid grid-cols-7 gap-2">
              {[...Array(firstDayOfMonth)].map((_, i) => (
                <div key={`empty-${i}`} className="aspect-square" />
              ))}
              {[...Array(daysInMonth)].map((_, i) => {
                const day = i + 1
                const dateStr = `${currentDate.getFullYear()}-${String(currentDate.getMonth() + 1).padStart(2, "0")}-${String(day).padStart(2, "0")}`
                const dayInterviews = monthInterviews.filter((interview) => interview.scheduledTime.startsWith(dateStr))

                return (
                  <div
                    key={day}
                    className={`aspect-square p-2 border rounded-lg text-sm font-semibold text-center flex items-center justify-center ${
                      dayInterviews.length > 0
                        ? "bg-primary/10 border-primary text-primary"
                        : "border-border text-foreground"
                    }`}
                  >
                    <div>
                      <div>{day}</div>
                      {dayInterviews.length > 0 && (
                        <div className="text-xs text-primary">{dayInterviews.length} interview</div>
                      )}
                    </div>
                  </div>
                )
              })}
            </div>
          </div>
        </Card>

        {/* Upcoming Interviews */}
        <div className="space-y-4">
          <h3 className="font-semibold text-foreground">Upcoming Interviews</h3>
          {monthInterviews.length === 0 ? (
            <p className="text-muted-foreground text-sm">No interviews scheduled</p>
          ) : (
            monthInterviews.map((interview) => (
              <Card
                key={interview.id}
                className="p-4 hover:bg-card-hover transition-colors cursor-pointer border-l-4 border-primary"
              >
                <p className="font-semibold text-foreground text-sm">{interview.candidateName}</p>
                <p className="text-xs text-muted-foreground">{new Date(interview.scheduledTime).toLocaleString()}</p>
                <p className="text-xs text-muted-foreground">{interview.duration} mins</p>
                <Button size="sm" variant="outline" className="mt-2 w-full text-xs bg-transparent">
                  Join Video
                </Button>
              </Card>
            ))
          )}
        </div>
      </div>
    </div>
  )
}
