import { Suspense } from "react";
import { LendWorkspace } from "./LendWorkspace";

export default function LendPage() {
  return (
    <Suspense>
      <LendWorkspace />
    </Suspense>
  );
}
