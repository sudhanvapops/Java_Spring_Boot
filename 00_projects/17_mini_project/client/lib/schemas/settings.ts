import { z } from "zod";

/** uploads/06-validation-rules.md "Settings forms". Each of the 3 keys saves
 * independently; the value must parse cleanly under its valueType or the
 * backend 400s with INVALID_SETTING_VALUE. */
export const maxBooksSchema = z.coerce
  .number({ error: "Enter a whole number." })
  .int("Enter a whole number.")
  .min(1, "Enter a number between 1 and 50.")
  .max(50, "Enter a number between 1 and 50.");

export const maxBorrowDaysSchema = z.coerce
  .number({ error: "Enter a whole number." })
  .int("Enter a whole number.")
  .min(1, "Enter a number between 1 and 365.")
  .max(365, "Enter a number between 1 and 365.");

export const finePerDaySchema = z.coerce
  .number({ error: "Enter an amount." })
  .min(0, "Enter an amount of 0 or more.")
  .max(10000, "Enter a smaller amount.")
  .refine((v) => Math.round(v * 100) === v * 100, {
    message: "Enter an amount with at most two decimal places.",
  });
