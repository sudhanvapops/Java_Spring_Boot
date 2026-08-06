"use client";

import { forwardRef, type HTMLAttributes, type ReactNode } from "react";
import {
  AnimatePresence,
  motion,
  type HTMLMotionProps,
  type TargetAndTransition,
  type Transition,
  type Variants,
} from "framer-motion";
import { useMediaQuery } from "@/lib/hooks/useMediaQuery";

/**
 * Ported from stacks-design-system/project/components/core/Motion.jsx. The
 * original bridged an optional UMD framer-motion global so the static kit
 * degraded gracefully if the script tag was missing; here framer-motion is a
 * real npm dependency, so the only thing MotionDiv/Presence still gate on is
 * prefers-reduced-motion, per the system's rule that it turns effects fully
 * OFF, not down (readme.md "Motion"). Consuming components (Dialog, Toast,
 * CommandPalette, EmptyState, AuthCard, StatCard...) use MotionDiv/Presence
 * unchanged from the reference JSX.
 */
export function usePrefersReducedMotion(): boolean {
  return useMediaQuery("(prefers-reduced-motion: reduce)");
}

/** Framer's AnimatePresence, or a passthrough when reduced motion is on. */
export function Presence({ children }: { children?: ReactNode }) {
  const reduced = usePrefersReducedMotion();
  if (reduced) return <>{children}</>;
  return <AnimatePresence>{children}</AnimatePresence>;
}

// framer-motion's HTMLMotionProps types these handlers with its own event
// shapes, which conflict with React's native DOM event types of the same
// name — omit them since none of our usages pass drag/animation handlers.
type ConflictingHandlers =
  | "onAnimationStart"
  | "onAnimationEnd"
  | "onDrag"
  | "onDragStart"
  | "onDragEnd"
  | "onDragEnter"
  | "onDragExit"
  | "onDragLeave"
  | "onDragOver"
  | "onDrop";

export interface MotionDivProps
  extends Omit<HTMLAttributes<HTMLDivElement>, ConflictingHandlers>,
    Pick<HTMLMotionProps<"div">, "initial" | "animate" | "exit" | "transition" | "variants" | "custom" | "layout"> {}

/** A div that animates unless the user prefers reduced motion, in which case
 * it's a plain div with none of the animation props applied. Forwards its
 * ref to the underlying DOM node (framer-motion's motion.div does this
 * natively; the plain-div fallback needs it spelled out) so consumers like
 * Dialog can focus-trap and measure it. */
export const MotionDiv = forwardRef<HTMLDivElement, MotionDivProps>(function MotionDiv(
  { initial, animate, exit, transition, variants, custom, layout, children, ...rest },
  ref,
) {
  const reduced = usePrefersReducedMotion();
  if (reduced) {
    return (
      <div ref={ref} {...rest}>
        {children}
      </div>
    );
  }
  return (
    <motion.div
      ref={ref}
      initial={initial}
      animate={animate}
      exit={exit}
      transition={transition}
      variants={variants}
      custom={custom}
      layout={layout}
      {...rest}
    >
      {children}
    </motion.div>
  );
});

/** The system's easing curves, as Framer transition objects. */
export const EASE = {
  /** State changes — 150ms ease-out. */
  fast: { duration: 0.15, ease: [0, 0, 0.2, 1] } satisfies Transition,
  /** Things that travel — 220ms. */
  medium: { duration: 0.22, ease: [0.16, 1, 0.3, 1] } satisfies Transition,
  /** Arrivals — 400ms. */
  entrance: { duration: 0.4, ease: [0.16, 1, 0.3, 1] } satisfies Transition,
};

/** Rise-and-fade, the system's one entrance. Nothing scales, nothing bounces.
 * Typed as plain TargetAndTransition objects (not the wider Variants map)
 * because consumers pass RISE.hidden/RISE.visible directly as initial/animate
 * rather than referencing variant label strings. */
export const RISE: Record<"hidden" | "visible", TargetAndTransition> = {
  hidden: { opacity: 0, y: 8 },
  visible: { opacity: 1, y: 0 },
};

/** Stagger container for a row of arriving cards. Dashboard only. */
export const STAGGER: Variants = { visible: { transition: { staggerChildren: 0.06 } } };

export function stagger(step = 0.05): Variants {
  return { visible: { transition: { staggerChildren: step } } };
}
