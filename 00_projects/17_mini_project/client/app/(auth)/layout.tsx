import type { ReactNode } from "react";
import { BackgroundBeams } from "@/components/panels/AuthCard";

/** Shared shell for all five auth routes: canvas background plus the one
 * ambient effect the spec allows on auth screens (readme.md "Backgrounds"). */
export default function AuthLayout({ children }: { children: ReactNode }) {
  return (
    <div style={{ position: "relative", minHeight: "100vh", display: "grid", placeItems: "center", background: "var(--canvas)", overflow: "hidden", padding: "var(--space-lg)" }}>
      <BackgroundBeams />
      <div style={{ position: "relative" }}>{children}</div>
    </div>
  );
}
