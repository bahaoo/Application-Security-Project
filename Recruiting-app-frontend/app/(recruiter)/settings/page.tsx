"use client"

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Label } from "@/components/ui/label"
import { Switch } from "@/components/ui/switch"
import { Separator } from "@/components/ui/separator"
import { Bell, Lock, Users, Palette } from "lucide-react"

const settingsSections = [
  {
    title: "Account",
    icon: Lock,
    settings: [
      { label: "Email", value: "recruiter@company.com", type: "input" },
      { label: "Password", value: "••••••••", type: "password" },
    ],
  },
  {
    title: "Notifications",
    icon: Bell,
    settings: [
      { label: "Email notifications", value: true, type: "toggle" },
      { label: "Application received alerts", value: true, type: "toggle" },
      { label: "Interview reminders", value: false, type: "toggle" },
    ],
  },
  {
    title: "Team",
    icon: Users,
    settings: [
      { label: "Team members", value: "5", type: "text" },
      { label: "Admin access", value: true, type: "toggle" },
    ],
  },
  {
    title: "Appearance",
    icon: Palette,
    settings: [
      { label: "Dark mode", value: true, type: "toggle" },
      { label: "Compact layout", value: false, type: "toggle" },
    ],
  },
]

export default function SettingsPage() {
  return (
    <div className="p-6 space-y-6 max-w-4xl">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Settings</h1>
        <p className="text-muted-foreground mt-1">Manage your account and preferences</p>
      </div>

      <div className="space-y-6">
        {settingsSections.map((section) => (
          <Card key={section.title} className="border-border">
            <CardHeader>
              <div className="flex items-center gap-2">
                <section.icon className="w-5 h-5 text-primary" />
                <CardTitle>{section.title}</CardTitle>
              </div>
            </CardHeader>
            <CardContent className="space-y-6">
              {section.settings.map((setting, idx) => (
                <div key={idx}>
                  <div className="flex items-center justify-between">
                    <Label className="text-base">{setting.label}</Label>
                    {setting.type === "toggle" ? (
                      <Switch checked={setting.value as boolean} />
                    ) : setting.type === "password" ? (
                      <Button variant="outline" size="sm">
                        Change Password
                      </Button>
                    ) : (
                      <span className="text-foreground">{setting.value}</span>
                    )}
                  </div>
                  {idx < section.settings.length - 1 && <Separator className="mt-6" />}
                </div>
              ))}
            </CardContent>
          </Card>
        ))}
      </div>

      <Card className="border-destructive bg-destructive/5">
        <CardHeader>
          <CardTitle className="text-destructive">Danger Zone</CardTitle>
          <CardDescription>Irreversible actions</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <Button variant="destructive" className="w-full">
            Delete Account
          </Button>
        </CardContent>
      </Card>
    </div>
  )
}
