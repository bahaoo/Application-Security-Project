"use server"

import type { Candidate } from "@/app/(recruiter)/pipeline/page"
import { validateABACPolicy, type ABACContext, type ABACResource } from "@/lib/abac"
import { getRequestContext, auditAction, requirePermission } from "@/lib/server-abac"

export async function updateCandidateStage(candidateId: string, newStage: Candidate["stage"]): Promise<void> {
  // CHANGE: Add ABAC validation before updating
  const permission = await requirePermission("recruiter", `candidate:${candidateId}`)
  if (!permission.allowed) {
    throw new Error(permission.error)
  }

  const context = await getRequestContext()

  // Simulate resource fetch
  const resource: ABACResource = {
    id: candidateId,
    owner: "system",
    department: context.department,
    sensitivity: 2,
    type: "candidate",
  }

  if (!validateABACPolicy(context as ABACContext, resource, "write")) {
    await auditAction("update_candidate_stage", `candidate:${candidateId}`, "denied", "ABAC policy violation")
    throw new Error("ABAC policy violation: cannot update candidate")
  }

  await auditAction("update_candidate_stage", `candidate:${candidateId}`, "success", `Moved to ${newStage}`)
  console.log(`[Server Action] Moving candidate ${candidateId} to ${newStage}`)
  await new Promise((resolve) => setTimeout(resolve, 500))
}

export async function createJobPosting(jobData: {
  title: string
  department: string
  level: string
  description: string
  requirements: string
  salary_min: number
  salary_max: number
  location: string
}): Promise<{ id: string }> {
  // CHANGE: Require recruiter role for job creation
  const permission = await requirePermission("recruiter", "job_posting")
  if (!permission.allowed) {
    throw new Error(permission.error)
  }

  const context = await getRequestContext()
  const jobId = `job-${Date.now()}`

  await auditAction("create_job_posting", `job:${jobId}`, "success", `Created job: ${jobData.title}`)
  console.log("[Server Action] Creating job posting:", jobData)
  await new Promise((resolve) => setTimeout(resolve, 500))
  return { id: jobId }
}

export async function submitApplication(
  candidateName: string,
  candidateEmail: string,
  jobId: string,
  encryptedResume: string,
): Promise<void> {
  // CHANGE: Log application submission to audit trail
  const permission = await requirePermission("candidate", `job:${jobId}`)
  if (!permission.allowed) {
    throw new Error(permission.error)
  }

  await auditAction("submit_application", `job:${jobId}`, "success", `Application from ${candidateEmail}`)
  console.log("[Server Action] Application submitted for:", candidateName)
  await new Promise((resolve) => setTimeout(resolve, 500))
}
