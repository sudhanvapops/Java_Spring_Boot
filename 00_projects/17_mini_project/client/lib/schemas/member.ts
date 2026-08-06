import { z } from "zod";

/** uploads/06-validation-rules.md "Member form". The backend only checks
 * email is non-empty, so real format validation is a frontend-only guard. */
export const memberFormSchema = z.object({
  name: z
    .string()
    .trim()
    .min(1, "Enter the member's full name.")
    .max(120, "Names can't be longer than 120 characters."),
  email: z
    .string()
    .trim()
    .toLowerCase()
    .min(1, "Enter an email address.")
    .email("That doesn't look like an email address.")
    .max(254, "That email address is too long."),
  // Plain z.number() bound with { valueAsNumber: true } — see book.ts for why.
  age: z
    .number({ error: "Enter the member's age." })
    .int("Enter a whole number.")
    .min(1, "Enter an age between 1 and 120.")
    .max(120, "Enter an age between 1 and 120."),
});

export type MemberFormValues = z.infer<typeof memberFormSchema>;
