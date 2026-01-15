"use client"

import { Card } from "@/components/ui/card"
import { Users, FileText, CheckCircle, TrendingUp } from "lucide-react"

const stats = [
  {
    label: "Total Candidates",
    value: "48",
    icon: Users,
    change: "+12%",
    color: "text-blue-500",
  },
  {
    label: "Open Positions",
    value: "8",
    icon: FileText,
    change: "+2",
    color: "text-cyan-500",
  },
  {
    label: "Hired This Month",
    value: "5",
    icon: CheckCircle,
    change: "+1",
    color: "text-green-500",
  },
  {
    label: "Pipeline Progress",
    value: "68%",
    icon: TrendingUp,
    change: "+5%",
    color: "text-amber-500",
  },
]

export default function DashboardPage() {
  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-foreground">Dashboard</h1>
        <p className="text-muted-foreground mt-1">Welcome back! Here's your recruitment overview.</p>
      </div>

      <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-4">
        {stats.map((stat) => (
          <Card key={stat.label} className="border-border p-6">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-sm text-muted-foreground">{stat.label}</p>
                <p className="text-2xl font-bold text-foreground mt-2">{stat.value}</p>
                <p className={`text-xs mt-2 ${stat.color}`}>{stat.change}</p>
              </div>
              <stat.icon className={`w-8 h-8 ${stat.color} opacity-80`} />
            </div>
          </Card>
        ))}
      </div>

      <Card className="border-border p-6">
        <h2 className="text-xl font-semibold text-foreground mb-4">Recent Activity</h2>
        <div className="space-y-4">
          {[
            { candidate: "Sarah Johnson", action: "moved to Interview stage", time: "2 hours ago" },
            { candidate: "Mike Chen", action: "submitted application", time: "4 hours ago" },
            { candidate: "Emma Davis", action: "received offer", time: "1 day ago" },
          ].map((activity, i) => (
            <div
              key={i}
              className="flex items-center justify-between pb-4 border-b border-border last:pb-0 last:border-0"
            >
              <div>
                <p className="font-medium text-foreground">{activity.candidate}</p>
                <p className="text-sm text-muted-foreground">{activity.action}</p>
              </div>
              <span className="text-xs text-muted-foreground">{activity.time}</span>
            </div>
          ))}
        </div>
      </Card>
    </div>
  )
}
