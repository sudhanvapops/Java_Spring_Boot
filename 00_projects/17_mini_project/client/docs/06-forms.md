# 06 — Forms & Validation

Every form in this app uses the same three pieces: **react-hook-form** for
state, **Zod** for rules, **`zodResolver`** to connect them.

## The canonical form

[`app/(app)/books/new/page.tsx`](../app/%28app%29/books/new/page.tsx) is the
reference implementation. Copy this shape.

```tsx
"use client";

const {
  register,
  handleSubmit,
  formState: { errors, isSubmitting },
} = useForm<BookFormValues>({
  resolver: zodResolver(bookFormSchema),
  defaultValues: { name: "", author: "", totalCopies: 1 },
});

const onSubmit = handleSubmit(async (values) => {
  // values is fully typed and already validated
  try {
    const book = await createBook.mutateAsync(values);
    toast.success(`"${book.title}" added.`);
    router.push(`/books/${book.id}`);
  } catch (err) {
    if (isNormalizedApiError(err) && err.errorCode === "BOOK_ALREADY_EXISTS") return; // shown inline
    toast.errorFrom(err);
  }
});

return (
  <form onSubmit={onSubmit}>
    <Label htmlFor="title" required>Title</Label>
    <Input
      id="title"
      invalid={!!errors.name}
      error={errors.name?.message}
      helper={errors.name ? null : "As printed on the spine."}
      autoFocus
      {...register("name")}
    />
    <Button type="submit" loading={isSubmitting || createBook.isPending} loadingLabel="Saving…">
      Add book
    </Button>
  </form>
);
```

---

## Why react-hook-form

It keeps inputs **uncontrolled**. Typing doesn't re-render the page — the DOM
node holds the value and RHF reads it on submit. A controlled `useState`
per field re-renders the whole form on every keystroke.

`register("name")` returns `{ name, onChange, onBlur, ref }`, which is why it's
spread onto the input:

```tsx
<Input {...register("name")} />
```

⚠️ **Spread `register()` last**, after your own props. It sets `name`/`ref`, and
an earlier spread can be silently overwritten.

---

## Zod schemas

Schemas live in [`lib/schemas/`](../lib/schemas/), one file per family. Each
exports the schema **and** its inferred type:

```ts
// lib/schemas/book.ts
export const bookFormSchema = z.object({
  name: z.string().trim().min(1, "Enter the book's title.").max(255, "Titles can't be longer than 255 characters."),
  author: z.string().trim().min(1, "Enter the author's name.").max(255, …),
  totalCopies: z.number({ error: "Enter how many copies the library owns." })
    .int("Enter a whole number.")
    .min(1, "The library needs at least one copy.")
    .max(10000, "Enter a smaller number of copies."),
});

export type BookFormValues = z.infer<typeof bookFormSchema>;
```

`z.infer` means the TypeScript type is **derived from** the runtime rules — they
can never drift apart.

### Error messages are UI copy

Every message is a complete sentence aimed at the person, not the developer:

| Don't | Do |
|---|---|
| "Required" | "Enter the book's title." |
| "Invalid email" | "That doesn't look like an email address." |
| "Min 8" | "Use at least 8 characters for your password." |
| "Must be > 0" | "Enter an age between 1 and 120." |

---

## Numbers: the `valueAsNumber` rule

HTML inputs always produce **strings**. A `z.number()` schema will reject
`"5"`. Two ways out; this codebase picks one deliberately:

```tsx
<Input type="number" {...register("totalCopies", { valueAsNumber: true })} />
```

```ts
totalCopies: z.number({ error: "Enter how many copies the library owns." }).int().min(1)
```

`valueAsNumber: true` makes **react-hook-form** do the conversion, so the schema
receives a real number.

> ▸ **Why not `z.coerce.number()`?** From the comment in
> [`lib/schemas/book.ts`](../lib/schemas/book.ts): `z.coerce`'s *input* type is
> `unknown`, which makes the form's input and output types differ and fights
> `useForm`'s generics. Keeping conversion in RHF keeps one clean type.

`z.coerce` **is** used in [`lib/schemas/settings.ts`](../lib/schemas/settings.ts)
— but those schemas validate a raw string outside `useForm`
(`rule.schema.safeParse(value)`), where the generics problem doesn't apply.

---

## Cross-field validation with `superRefine`

For rules spanning multiple fields — "passwords must match":

```ts
// lib/schemas/auth.ts
export const staffRegisterSchema = z
  .object({ ...accountFields, role: z.enum(["ADMIN", "LIBRARIAN"], { message: "Choose a role." }) })
  .superRefine(checkPasswordsMatch);

function checkPasswordsMatch(values, ctx) {
  if (values.password !== values.confirmPassword) {
    ctx.addIssue({ code: "custom", path: ["confirmPassword"], message: "Those passwords don't match." });
  }
}
```

`path: ["confirmPassword"]` attaches the error to that field, so it renders
under the right input rather than as a form-level message.

⚠️ `.superRefine()` returns a `ZodEffects`, not a `ZodObject` — you can't call
`.extend()` on it afterwards. That's why the shared fields live in a plain
`accountFields` object that gets spread into each schema.

---

## Displaying errors

Three levels, each with a different treatment:

### 1. Field errors — inline, under the input

```tsx
<Input
  invalid={!!errors.name}
  error={errors.name?.message}
  helper={errors.name ? null : "As printed on the spine."}
/>
```

The [`Input`](../components/forms/Input.tsx) component handles the rest: red
border, `aria-invalid`, warning icon, and swapping the helper text for the
error. The `helper={errors.name ? null : "…"}` idiom prevents both showing at
once.

### 2. Server field errors — same place, from the mutation

A duplicate title is only detectable server-side, but belongs on the field:

```tsx
const duplicateError =
  createBook.error && isNormalizedApiError(createBook.error) && createBook.error.errorCode === "BOOK_ALREADY_EXISTS"
    ? createBook.error.userMessage
    : null;

<Input invalid={!!errors.name || !!duplicateError} error={errors.name?.message || duplicateError} />
```

And in the catch, `return` early so it isn't *also* toasted:

```ts
catch (err) {
  if (isNormalizedApiError(err) && err.errorCode === "BOOK_ALREADY_EXISTS") return; // shown inline
  toast.errorFrom(err);
}
```

### 3. Form-level errors — above everything

Credential failures must **never** sit on a field — saying "wrong password"
confirms the email exists. From
[`app/(auth)/login/LoginForm.tsx`](../app/%28auth%29/login/LoginForm.tsx):

```tsx
const formError =
  login.error && isNormalizedApiError(login.error)
    ? login.error.errorCode === "UNAUTHORIZED"
      ? "That email and password don't match."
      : login.error.userMessage
    : null;

<AuthCard error={formError}>…</AuthCard>
```

---

## Loading state on submit

```tsx
<Button type="submit" loading={isSubmitting || createBook.isPending} loadingLabel="Saving…">
  Add book
</Button>
```

Both flags: `isSubmitting` covers validation and any local async work,
`isPending` covers the in-flight request. `Button` disables itself while
loading, which is the double-submit guard.

---

## Populating an edit form

Data arrives asynchronously, after the form has mounted with empty defaults.
Use `reset()`:

```tsx
const { data: member } = useMember(id);
const { reset, … } = useForm<MemberFormValues>({ … });

useEffect(() => {
  if (member) reset({ name: member.name, email: member.email, age: member.age });
}, [member, reset]);
```

⚠️ **This exact effect caused an infinite render loop in this codebase.** Not
because the effect is wrong — it's the standard pattern — but because `member`
was a *new object every render* from an unmemoised hook, so `[member, reset]`
never settled. The fix was `useMemo` in the hook. Full story in
[13 Gotchas](./13-gotchas.md). When you write this effect, make sure the value
it depends on is referentially stable.

---

## Reading a value while typing

`watch()` subscribes to a field and re-renders on change — use it only when the
UI genuinely must react live:

```tsx
const password = watch("password") || "";
<StrengthMeter score={passwordScore(password)} requirements={passwordChecks(password)} />
```

For a non-`<input>` control, pair `watch` with `setValue`:

```tsx
<Select
  value={watch("role")}
  onChange={(e) => setValue("role", e.target.value as StaffRegisterFormValues["role"])}
  options={[{ value: "LIBRARIAN", label: "Librarian" }, { value: "ADMIN", label: "Admin" }]}
/>
```

---

## Form field components

| Component | Use for |
|---|---|
| [`Input`](../components/forms/Input.tsx) | Text, email, number. Supports `iconLeft`, `prefix`, `unit`, `error`, `helper` |
| [`PasswordField`](../components/forms/PasswordField.tsx) | Passwords — adds a show/hide toggle |
| [`Select`](../components/forms/Select.tsx) | Dropdowns. Needs `value` + `onChange`, not `register` |
| [`Checkbox`](../components/forms/Checkbox.tsx) | Booleans. Supports `indeterminate` |
| [`Label`](../components/forms/Label.tsx) | Field labels. `required` adds the asterisk, `hint` adds right-aligned text |
| [`StrengthMeter`](../components/forms/StrengthMeter.tsx) | Advisory password strength — never gates submit |

Always pair `<Label htmlFor="x">` with `<Input id="x">` — that's what makes
clicking the label focus the field, and what screen readers announce.

---

## Checklist for a new form

1. Schema in `lib/schemas/`, exporting schema + `z.infer` type.
2. `useForm({ resolver: zodResolver(schema), defaultValues })` — always give
   defaults, or React warns about uncontrolled→controlled.
3. `{...register("field")}` last in the props spread.
4. `valueAsNumber: true` on numeric fields.
5. `errors.field?.message` into `error`, `!!errors.field` into `invalid`.
6. Submit via `mutateAsync` in try/catch.
7. Field-specific server errors inline; everything else `toast.errorFrom(err)`.
8. `loading={isSubmitting || mutation.isPending}` on the button.

**Next:** [07 — Types & derive](./07-types-and-derive.md)
