"use client"

import { useState } from "react"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Input } from "@/components/ui/input"
import { Search, Shield, User, FileText } from "lucide-react"

interface AuditLog {
  id: string
  timestamp: string
  actor: string
  action: string
  resource: string
  status: "success" | "denied" | "error"
  ipAddress: string
  details: string
}

export default function AuditPage() {
  const [logs, setLogs] = useState<AuditLog[]>([
    {
      id: "1",
      timestamp: new Date(Date.now() - 3600000).toISOString(),
      actor: "recruiter@company.com",
      action: "view_resume",
      resource: "candidate:sarah-001",
      status: "success",
      ipAddress: "192.168.1.1",
      details: "Accessed candidate resume for position eng-2024",
    },
    {
      id: "2",
      timestamp: new Date(Date.now() - 7200000).toISOString(),
      actor: "candidate@company.com",
      action: "view_candidate",
      resource: "candidate:unknown",
      status: "denied",
      ipAddress: "203.0.113.45",
      details: "Insufficient permissions for resource",
    },
    {
      id: "3",
      timestamp: new Date(Date.now() - 10800000).toISOString(),
      actor: "admin@company.com",
      action: "modify_policy",
      resource: "policy:abac-001",
      status: "success",
      ipAddress: "192.168.1.10",
      details: "Updated ABAC rule: recruiters_view_candidates",
    },
  ])
  const [searchTerm, setSearchTerm] = useState("")

  // CHANGE: Immutable audit log viewer with timeline and filtering
  const filteredLogs = logs.filter(
    (log) =>
      log.actor.includes(searchTerm) ||
      log.action.includes(searchTerm) ||
      log.resource.includes(searchTerm) ||
      log.ipAddress.includes(searchTerm),
  )

  const getStatusColor = (status: AuditLog["status"]) => {
    switch (status) {
      case "success":
        return "default"
      case "denied":
        return "destructive"
      case "error":
        return "secondary"
    }
  }

  const getActionIcon = (action: string) => {
    if (action.includes("view")) return <FileText className="w-4 h-4" />
    if (action.includes("modify")) return <Shield className="w-4 h-4" />
    return <User className="w-4 h-4" />
  }

  return (
    <div className="space-y-6 p-6">
      <div>
        <h1 className="text-3xl font-bold">Audit Log</h1>
        <p className="text-muted-foreground mt-1">Immutable action history with zero-trust validation</p>
      </div>

      {/* Search */}
      <div className="relative">
        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-muted-foreground" />
        <Input
          placeholder="Search actor, action, resource, or IP..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="pl-10"
        />
      </div>

      {/* Timeline */}
      <div className="space-y-3">
        {filteredLogs.map((log, idx) => (
          <Card key={log.id} className="hover:shadow-md transition">
            <CardContent className="pt-6">
              <div className="flex gap-4">
                <div className="flex flex-col items-center">
                  <div className="w-8 h-8 rounded-full bg-muted flex items-center justify-center">
                    {getActionIcon(log.action)}
                  </div>
                  {idx < filteredLogs.length - 1 && <div className="w-0.5 h-16 bg-border mt-2" />}
                </div>

                <div className="flex-1 pb-2">
                  <div className="flex items-center justify-between mb-2">
                    <div>
                      <p className="font-medium">{log.action}</p>
                      <p className="text-sm text-muted-foreground">{log.actor}</p>
                    </div>
                    <Badge variant={getStatusColor(log.status)}>{log.status.toUpperCase()}</Badge>
                  </div>

                  <p className="text-sm text-muted-foreground mb-2">{log.details}</p>

                  <div className="flex flex-wrap gap-3 text-xs text-muted-foreground">
                    <span>Resource: {log.resource}</span>
                    <span>IP: {log.ipAddress}</span>
                    <span>{new Date(log.timestamp).toLocaleString()}</span>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  )
}
