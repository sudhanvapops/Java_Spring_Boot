import { NextResponse } from "next/server";

/**
 * There used to be a layer-1 cookie-presence check here (bounce an obviously
 * signed-out visitor before the app bundle loads). It's gone because it
 * cannot work against the real backend: `refreshToken` is set by the Spring
 * server at `localhost:8080` with `Path=/api/auth` (BACKEND_HANDOFF.md
 * §3.5). That path only exists on the *backend* — every request this Next.js
 * proxy ever sees (`/dashboard`, `/books`, ...) has a different path, so the
 * browser never attaches the cookie to them. `request.cookies.has(...)`
 * would be permanently false for every visitor, signed in or not, and every
 * protected route would redirect to /login in a loop.
 *
 * Route protection is two-layer now: app/(app)/layout.tsx calls
 * /api/auth/refresh on mount (that request *does* hit /api/auth, so the
 * cookie works there) and redirects client-side on failure; the server's
 * @PreAuthorize checks are the only real security either way — see
 * BACKEND_HANDOFF.md §3.6's three-layer caveat.
 *
 * Next.js 16 renamed this convention from middleware.ts/`middleware()` to
 * proxy.ts/`proxy()` — same edge runtime, same request/response shape.
 */
export function proxy() {
  return NextResponse.next();
}

export const config = {
  matcher: ["/((?!_next/static|_next/image|favicon.ico).*)"],
};
