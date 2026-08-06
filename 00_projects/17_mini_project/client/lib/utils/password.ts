/**
 * Advisory strength signal only — the meter never gates submit
 * (uploads/05-auth-spec.md "Password rules": length beats composition, so
 * the actual validation in lib/schemas/auth.ts only checks length + a
 * blocklist). These three checks mirror the reference StrengthMeter usage.
 */
export interface PasswordCheck {
  label: string;
  met: boolean;
}

export function passwordChecks(password: string): PasswordCheck[] {
  return [
    { label: "At least 12 characters", met: password.length >= 12 },
    { label: "One number", met: /\d/.test(password) },
    { label: "One symbol", met: /[^A-Za-z0-9]/.test(password) },
  ];
}

export function passwordScore(password: string): 0 | 1 | 2 | 3 {
  const met = passwordChecks(password).filter((c) => c.met).length;
  return Math.min(3, met) as 0 | 1 | 2 | 3;
}
