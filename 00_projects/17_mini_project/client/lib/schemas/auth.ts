import { z } from "zod";

/**
 * PROPOSED — none of these endpoints exist in the backend yet.
 * uploads/05-auth-spec.md "Password rules": 12-128 chars, no composition
 * requirements (NIST-current, length beats complexity), blocklist common
 * passwords, never allow reuse of the account's own email/name.
 */
const COMMON_PASSWORDS = new Set([
  "password",
  "password123",
  "12345678",
  "123456789",
  "qwertyuiop",
  "letmein12345",
  "iloveyou1234",
  "welcome12345",
  "changeme1234",
  "admin1234567",
]);

function passwordField(label = "password") {
  return z
    .string()
    .min(12, `Use at least 12 characters for your ${label}.`)
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

export const signupSchema = z
  .object({
    name: z
      .string()
      .trim()
      .min(2, "Enter your full name.")
      .max(120, "Names can't be longer than 120 characters."),
    email: z
      .string()
      .trim()
      .toLowerCase()
      .min(1, "Enter an email address.")
      .email("That doesn't look like an email address."),
    password: passwordField(),
    confirmPassword: z.string().min(1, "Confirm your password."),
  })
  .superRefine((values, ctx) => {
    if (values.password !== values.confirmPassword) {
      ctx.addIssue({
        code: "custom",
        path: ["confirmPassword"],
        message: "Those passwords don't match.",
      });
    }
    const local = values.email.split("@")[0]?.toLowerCase();
    const pw = values.password.toLowerCase();
    if (
      (local && local.length > 2 && pw.includes(local)) ||
      (values.name.trim().length > 2 && pw.includes(values.name.trim().toLowerCase()))
    ) {
      ctx.addIssue({
        code: "custom",
        path: ["password"],
        message: "Don't use your name or email in your password.",
      });
    }
  });
export type SignupFormValues = z.infer<typeof signupSchema>;

export const forgotPasswordSchema = z.object({
  email: z
    .string()
    .trim()
    .toLowerCase()
    .min(1, "Enter an email address.")
    .email("That doesn't look like an email address."),
});
export type ForgotPasswordFormValues = z.infer<typeof forgotPasswordSchema>;

export const resetPasswordSchema = z
  .object({
    token: z.string().min(1),
    password: passwordField(),
    confirmPassword: z.string().min(1, "Confirm your password."),
  })
  .refine((values) => values.password === values.confirmPassword, {
    message: "Those passwords don't match.",
    path: ["confirmPassword"],
  });
export type ResetPasswordFormValues = z.infer<typeof resetPasswordSchema>;

export const changePasswordSchema = z
  .object({
    currentPassword: z.string().min(1, "Enter your current password."),
    newPassword: passwordField("new password"),
    confirmPassword: z.string().min(1, "Confirm your new password."),
  })
  .superRefine((values, ctx) => {
    if (values.newPassword !== values.confirmPassword) {
      ctx.addIssue({ code: "custom", path: ["confirmPassword"], message: "Those passwords don't match." });
    }
    if (values.newPassword === values.currentPassword) {
      ctx.addIssue({
        code: "custom",
        path: ["newPassword"],
        message: "Choose a password you haven't used here before.",
      });
    }
  });
export type ChangePasswordFormValues = z.infer<typeof changePasswordSchema>;
