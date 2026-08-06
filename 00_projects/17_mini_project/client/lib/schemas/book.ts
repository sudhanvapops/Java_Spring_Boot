import { z } from "zod";

/** uploads/06-validation-rules.md "Book form". availableCopies is read-only
 * on the edit form (server-derived), so it's never part of the submit schema. */
export const bookFormSchema = z.object({
  name: z
    .string()
    .trim()
    .min(1, "Enter the book's title.")
    .max(255, "Titles can't be longer than 255 characters."),
  author: z
    .string()
    .trim()
    .min(1, "Enter the author's name.")
    .max(255, "Author names can't be longer than 255 characters."),
  // Plain z.number(), not z.coerce — the input binds with
  // { valueAsNumber: true } so react-hook-form does the string->number
  // conversion itself, keeping the form's input and output types identical
  // (z.coerce's input type is `unknown`, which fights useForm's generics).
  totalCopies: z
    .number({ error: "Enter how many copies the library owns." })
    .int("Enter a whole number.")
    .min(1, "The library needs at least one copy.")
    .max(10000, "Enter a smaller number of copies."),
});

export type BookFormValues = z.infer<typeof bookFormSchema>;
