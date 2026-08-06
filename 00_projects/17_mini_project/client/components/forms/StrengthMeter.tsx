import { Icon } from "@/components/core/Icon";

export interface StrengthRequirement {
  label: string;
  met: boolean;
}

export interface StrengthMeterProps {
  /** 0 = too short, 1 = Weak, 2 = Fair, 3 = Strong. */
  score?: 0 | 1 | 2 | 3;
  /** Rules that tick off live as the user types. */
  requirements?: StrengthRequirement[];
}

const LEVELS = [
  { label: "Too short", color: "var(--hairline)" },
  { label: "Weak", color: "var(--danger)" },
  { label: "Fair", color: "var(--warning)" },
  { label: "Strong", color: "var(--success)" },
];

export function StrengthMeter({ score = 0, requirements = [] }: StrengthMeterProps) {
  const level = LEVELS[Math.min(score, 3)];
  return (
    <div style={{ marginTop: "var(--space-xs)" }}>
      <div style={{ display: "flex", alignItems: "center", gap: "var(--space-sm)" }}>
        <div style={{ display: "flex", gap: "var(--space-xxs)", flex: 1 }}>
          {[0, 1, 2, 3].map((i) => (
            <span
              key={i}
              style={{
                flex: 1,
                height: 3,
                borderRadius: "var(--radius-pill)",
                background: i < score ? level.color : "var(--hairline)",
              }}
            />
          ))}
        </div>
        <span
          style={{
            font: "var(--type-caption)",
            color: score >= 3 ? "var(--success)" : "var(--ink-subtle)",
            minWidth: 52,
            textAlign: "right",
          }}
        >
          {level.label}
        </span>
      </div>
      {requirements.length ? (
        <ul style={{ listStyle: "none", margin: "var(--space-xs) 0 0", padding: 0, display: "grid", gap: "var(--space-xxs)" }}>
          {requirements.map((r) => (
            <li
              key={r.label}
              style={{
                display: "flex",
                alignItems: "center",
                gap: "var(--space-xxs)",
                font: "var(--type-caption)",
                color: r.met ? "var(--ink-muted)" : "var(--ink-subtle)",
              }}
            >
              <Icon name={r.met ? "circle-check" : "circle"} size={12} color={r.met ? "var(--success)" : "var(--ink-tertiary)"} />
              {r.label}
            </li>
          ))}
        </ul>
      ) : null}
    </div>
  );
}
