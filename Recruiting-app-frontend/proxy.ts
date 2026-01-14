import { type NextRequest, NextResponse } from "next/server"

export function proxy(request: NextRequest) {
  const { pathname } = request.nextUrl

  // Extract role from auth token/session (simplified example)
  const userRole = request.cookies.get("userRole")?.value || "candidate"

  // Recruiter-only routes
  if (pathname.startsWith("/recruiter")) {
    if (userRole !== "recruiter") {
      return NextResponse.redirect(new URL("/auth/login", request.url))
    }
  }

  // Candidate-only routes
  if (pathname.startsWith("/candidate")) {
    if (userRole !== "candidate") {
      return NextResponse.redirect(new URL("/auth/login", request.url))
    }
  }

  // Interviewer-specific routes
  if (pathname.startsWith("/interviewer")) {
    if (userRole !== "interviewer" && userRole !== "recruiter") {
      return NextResponse.redirect(new URL("/auth/login", request.url))
    }
  }

  return NextResponse.next()
}

export const config = {
  matcher: ["/(recruiter|candidate|interviewer)/:path*"],
}
