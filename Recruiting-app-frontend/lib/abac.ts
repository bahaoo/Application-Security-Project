// Server-side ABAC validation
export interface ABACContext {
  userId: string
  role: string
  department: string
  clearanceLevel: number
}

export interface ABACResource {
  id: string
  owner: string
  department: string
  sensitivity: number
  type: string
}

export function validateABACPolicy(context: ABACContext, resource: ABACResource, action: string): boolean {
  // CHANGE: ABAC enforcement logic with attribute checks
  switch (action) {
    case "read":
      // Recruiters can read candidates in their department
      if (context.role === "recruiter") {
        return context.department === resource.department && context.clearanceLevel >= resource.sensitivity
      }
      // Candidates can only read their own data
      if (context.role === "candidate") {
        return context.userId === resource.owner
      }
      return false

    case "write":
      // Only recruiters with sufficient clearance
      return context.role === "recruiter" && context.clearanceLevel > resource.sensitivity

    case "delete":
      // Only admins with max clearance
      return context.role === "admin" && context.clearanceLevel >= 5

    default:
      return false
  }
}
