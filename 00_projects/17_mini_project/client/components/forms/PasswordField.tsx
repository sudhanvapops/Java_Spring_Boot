"use client";

import { useState, type InputHTMLAttributes, type ReactNode } from "react";
import { Input } from "./Input";
import { IconButton } from "./IconButton";

export interface PasswordFieldProps extends Omit<InputHTMLAttributes<HTMLInputElement>, "size"> {
  invalid?: boolean;
  error?: ReactNode;
  helper?: ReactNode;
  /** "current-password" on login, "new-password" on signup and reset. */
  autoComplete?: string;
}

export function PasswordField({
  id,
  value,
  onChange,
  invalid,
  error,
  helper,
  autoComplete = "current-password",
  ...rest
}: PasswordFieldProps) {
  const [shown, setShown] = useState(false);
  return (
    <div style={{ position: "relative" }}>
      <Input
        id={id}
        type={shown ? "text" : "password"}
        value={value}
        onChange={onChange}
        invalid={invalid}
        error={error}
        helper={helper}
        autoComplete={autoComplete}
        style={{ paddingRight: "var(--space-xxl)" }}
        {...rest}
      />
      <span style={{ position: "absolute", top: 3, right: 4 }}>
        <IconButton
          icon={shown ? "eye-off" : "eye"}
          label={shown ? "Hide password" : "Show password"}
          onClick={() => setShown((s) => !s)}
        />
      </span>
    </div>
  );
}
