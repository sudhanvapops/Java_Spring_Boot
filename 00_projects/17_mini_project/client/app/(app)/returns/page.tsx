import { Suspense } from "react";
import { ReturnsWorkspace } from "./ReturnsWorkspace";

export default function ReturnsPage() {
  return (
    <Suspense>
      <ReturnsWorkspace />
    </Suspense>
  );
}
