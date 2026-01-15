import Link from "next/link"
import { Button } from "@/components/ui/button"

export default function Home() {
  return (
    <main className="min-h-screen w-full flex flex-col items-center justify-center bg-background p-4">
      <div className="w-full max-w-md space-y-8">
        <div className="space-y-3 text-center">
          <div className="flex items-center justify-center gap-2 mb-4">
            <div className="w-10 h-10 rounded-lg bg-primary flex items-center justify-center">
              <span className="text-primary-foreground font-bold text-lg">SR</span>
            </div>
          </div>
          <h1 className="text-4xl font-bold text-foreground">SecureRecruit</h1>
          <p className="text-base text-muted-foreground">Enterprise Secure Recruitment Platform</p>
        </div>

        <div className="space-y-3">
          <Link href="/auth/login" className="block">
            <Button size="lg" className="w-full bg-primary hover:bg-primary/90 text-primary-foreground">
              Sign In
            </Button>
          </Link>

          <Link href="/auth/signup" className="block">
            <Button
              size="lg"
              variant="outline"
              className="w-full border-border text-foreground hover:bg-card bg-transparent"
            >
              Create Account
            </Button>
          </Link>
        </div>

        <div className="pt-8 border-t border-border">
          <p className="text-center text-sm text-muted-foreground">
            Secured by OAuth 2.0 PKCE • End-to-End Encryption • PWA Ready
          </p>
        </div>
      </div>
    </main>
  )
}
