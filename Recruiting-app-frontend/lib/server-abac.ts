// Server-side ABAC validation with request context

import { headers, cookies } from "next/headers"
import type { ABACContext } from "./abac"

export async function getRequestContext(): Promise<ABACContext> {
  // CHANGE: Extract user context from cookies and headers
  const cookieStore = await cookies()
  const userId = cookieStore.get("userId")?.value || "unknown"
  const role = cookieStore.get("userRole")?.value || "candidate"
  const department = cookieStore.get("department")?.value || "general"
  const clearanceLevel = Number.parseInt(cookieStore.get("clearanceLevel")?.value || "1")

  return {
    userId,
    role,
    department,
    clearanceLevel,
  }
}

export async function auditAction(
  action: string,
  resource: string,
  status: "success" | "denied" | "error",
  details?: string,
) {
  // CHANGE: Log all actions to immutable audit trail
  const context = await getRequestContext()
  const headersList = await headers()
  const ipAddress = headersList.get("x-forwarded-for") || headersList.get("x-real-ip") || "0.0.0.0"

  const auditLog = {
    timestamp: new Date().toISOString(),
    actor: context.userId,
    action,
    resource,
    status,
    ipAddress,
    details,
    context,
  }

  // Store in database or append-only log
  console.log("[AUDIT]", JSON.stringify(auditLog))

  return auditLog
}

export async function requirePermission(
  requiredRole: string | string[],
  resource?: string,
): Promise<{ allowed: boolean; error?: string }> {
  // CHANGE: Enforce role-based access with audit logging
  const context = await getRequestContext()
  const roles = Array.isArray(requiredRole) ? requiredRole : [requiredRole]

  if (!roles.includes(context.role)) {
    await auditAction(
      "access_denied",
      resource || "unknown",
      "denied",
      `Role ${context.role} not in ${roles.join(",")}`,
    )
    return { allowed: false, error: "Insufficient permissions" }
  }

  return { allowed: true }
}
