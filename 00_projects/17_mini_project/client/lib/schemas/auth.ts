import { z } from "zod";

/**
 * Matches the real backend contract — BACKEND_HANDOFF.md §3.4/§3.8.
 * `password`: @NotBlank, @Size(min = 8) server-side, nothing else enforced.
 * No stricter client-only minimum — there's no backend rationale for it on
 * a practice project, and a mismatch just adds friction (see §3.8).
 */
const COMMON_PASSWORDS = new Set([
  "password",
  "password1",
  "12345678",
  "123456789",
  "qwertyuiop",
  "letmein123",
  "iloveyou1",
  "welcome123",
  "changeme1",
  "admin1234",
]);

function passwordField(label = "password") {
  return z
    .string()
    .min(8, `Use at least 8 characters for your ${label}.`)
    .max(128, `Your ${label} can be at most 128 characters.`)
    .refine((v) => !COMMON_PASSWORDS.has(v.toLowerCase()), {
      message: "Choose a less common password.",
    });
}

export const loginSchema = z.object({
  email: z
    .string()
    .trim()
    .toLowerCase()
    .min(1, "Enter an email address.")
    .email("That doesn't look like an email address."),
  // Never apply signup strength rules to login — locks out users if the
  // rules changed since their account was created.
  password: z.string().min(1, "Enter your password."),
  rememberMe: z.boolean(),
});
export type LoginFormValues = z.infer<typeof loginSchema>;

/** User accounts (which can sign in) are staff-only, created exclusively via
 * /register-staff by an existing admin — see lib/schemas/member.ts's
 * memberFormSchema for the public /signup form, which registers a Member
 * (no password) instead. */
const accountFields = {
  username: z
    .string()
    .trim()
    .min(2, "Enter a username.")
    .max(50, "Usernames can't be longer than 50 characters."),
  email: z
    .string()
    .trim()
    .toLowerCase()
    .min(1, "Enter an email address.")
    .email("That doesn't look like an email address."),
  password: passwordField(),
  confirmPassword: z.string().min(1, "Confirm your password."),
};

function checkPasswordsMatch(values: { password: string; confirmPassword: string }, ctx: z.RefinementCtx) {
  if (values.password !== values.confirmPassword) {
    ctx.addIssue({ code: "custom", path: ["confirmPassword"], message: "Those passwords don't match." });
  }
}

/** A required role on top of the shared account fields — @AdminOnly, see
 * BACKEND_HANDOFF.md §3.3. */
export const staffRegisterSchema = z
  .object({ ...accountFields, role: z.enum(["ADMIN", "LIBRARIAN"], { message: "Choose a role." }) })
  .superRefine(checkPasswordsMatch);
export type StaffRegisterFormValues = z.infer<typeof staffRegisterSchema>;
