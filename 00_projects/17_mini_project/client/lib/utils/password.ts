/**
 * Advisory strength signal only — the meter never gates submit. The actual
 * validation in lib/schemas/auth.ts only checks the backend's real 8-char
 * minimum plus a blocklist (BACKEND_HANDOFF.md §3.8: no composition rules
 * are enforced server-side, so these extra checks are UI-only encouragement).
 */
export interface PasswordCheck {
  label: string;
  met: boolean;
}

export function passwordChecks(password: string): PasswordCheck[] {
  return [
    { label: "At least 8 characters", met: password.length >= 8 },
    { label: "One number", met: /\d/.test(password) },
    { label: "One symbol", met: /[^A-Za-z0-9]/.test(password) },
  ];
}

export function passwordScore(password: string): 0 | 1 | 2 | 3 {
  const met = passwordChecks(password).filter((c) => c.met).length;
  return Math.min(3, met) as 0 | 1 | 2 | 3;
}
