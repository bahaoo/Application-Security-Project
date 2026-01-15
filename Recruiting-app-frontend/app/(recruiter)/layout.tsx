"use client"

import type React from "react"
import { Calendar } from "lucide-react"
import {
  SidebarProvider,
  Sidebar,
  SidebarContent,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuItem,
  SidebarMenuButton,
} from "@/components/ui/sidebar"
import { LayoutDashboard, Users, FileText, Settings, LogOut } from "lucide-react"
import Link from "next/link"
import { Button } from "@/components/ui/button"

const recruitmentMenuItems = [
  { label: "Dashboard", href: "/recruiter", icon: LayoutDashboard },
  { label: "Candidates", href: "/recruiter/candidates", icon: Users },
  { label: "Pipeline", href: "/recruiter/pipeline", icon: FileText },
  { label: "Calendar", href: "/recruiter/calendar", icon: Calendar },
  { label: "Settings", href: "/recruiter/settings", icon: Settings },
]

export default function RecruiterLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <SidebarProvider>
      <Sidebar>
        <SidebarHeader className="border-b p-4">
          <Link href="/recruiter" className="flex items-center gap-2">
            <div className="w-10 h-10 rounded-lg bg-primary flex items-center justify-center">
              <span className="text-lg font-bold text-primary-foreground">SR</span>
            </div>
            <div className="hidden sm:flex flex-col">
              <span className="font-semibold text-foreground">SecureRecruit</span>
              <span className="text-xs text-muted-foreground">Recruiter</span>
            </div>
          </Link>
        </SidebarHeader>
        <SidebarContent>
          <SidebarMenu>
            {recruitmentMenuItems.map((item) => (
              <SidebarMenuItem key={item.href}>
                <SidebarMenuButton asChild>
                  <Link href={item.href} className="flex items-center gap-3">
                    <item.icon className="w-5 h-5" />
                    <span>{item.label}</span>
                  </Link>
                </SidebarMenuButton>
              </SidebarMenuItem>
            ))}
          </SidebarMenu>
        </SidebarContent>
        <div className="border-t p-4">
          <Button variant="ghost" className="w-full justify-start" size="sm">
            <LogOut className="w-4 h-4 mr-2" />
            Sign Out
          </Button>
        </div>
      </Sidebar>
      <main className="flex-1 overflow-auto">{children}</main>
    </SidebarProvider>
  )
}
