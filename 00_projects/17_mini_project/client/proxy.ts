import { NextResponse, type NextRequest } from "next/server";

/**
 * Layer 1 of the 3-layer route protection (uploads/05-auth-spec.md "Route
 * protection"): a fast, edge-side check of refresh-cookie *presence* only —
 * not validity, that's layer 2/3. Its only job is to bounce an obviously
 * signed-out visitor before the app bundle loads. Named for the httpOnly
 * cookie the PROPOSED /api/auth/login sets; it won't exist until the
 * backend implements that endpoint, so every protected route redirects to
 * /login until then — which is the correct, honest behaviour for a
 * not-yet-built backend, not a bug.
 *
 * Next.js 16 renamed this convention from middleware.ts/`middleware()` to
 * proxy.ts/`proxy()` — same edge runtime, same request/response shape.
 */
const SESSION_COOKIE = "stacks_refresh";

const PUBLIC_PATHS = ["/login", "/signup", "/forgot-password", "/reset-password", "/verify-email"];

function isPublicPath(pathname: string): boolean {
  return PUBLIC_PATHS.some((p) => pathname === p || pathname.startsWith(`${p}/`));
}

/** Set NEXT_PUBLIC_DISABLE_AUTH=true in .env.local to make every route
 * public — see the comment there for why/how to turn it back off. */
const AUTH_DISABLED = process.env.NEXT_PUBLIC_DISABLE_AUTH === "true";

export function proxy(request: NextRequest) {
  if (AUTH_DISABLED) return NextResponse.next();

  const { pathname } = request.nextUrl;
  const hasSession = request.cookies.has(SESSION_COOKIE);
  const isPublic = isPublicPath(pathname);

  if (!isPublic && !hasSession) {
    const url = request.nextUrl.clone();
    url.pathname = "/login";
    url.search = "";
    url.searchParams.set("next", pathname);
    return NextResponse.redirect(url);
  }

  if (isPublic && hasSession) {
    const url = request.nextUrl.clone();
    url.pathname = "/dashboard";
    url.search = "";
    return NextResponse.redirect(url);
  }

  return NextResponse.next();
}

export const config = {
  matcher: ["/((?!_next/static|_next/image|favicon.ico).*)"],
};
