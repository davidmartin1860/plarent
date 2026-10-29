"use client";

import Link from "next/link";
import { useActionState } from "react";
import { type TranslationKey, useTranslations } from "@/lib/i18n";

export type AuthFormState = { error: TranslationKey; username: string } | undefined;

type Mode = "login" | "register";

const inputClassName =
  "h-10 w-full rounded-[20px] border border-secondary bg-transparent px-[14px] font-sans text-sm font-semibold text-primary outline-none placeholder:text-secondary focus:border-secondary-title focus:ring-1 focus:ring-secondary-title";

export function AuthForm({
  mode,
  action,
}: {
  mode: Mode;
  action: (state: AuthFormState, formData: FormData) => Promise<AuthFormState>;
}) {
  const t = useTranslations();
  const [state, formAction, isPending] = useActionState(action, undefined);
  const isRegister = mode === "register";

  return (
    <form action={formAction} className="flex flex-col gap-[14px]">
      <input
        name="username"
        type="text"
        required
        autoComplete="username"
        autoCapitalize="none"
        spellCheck={false}
        minLength={isRegister ? 3 : undefined}
        maxLength={32}
        pattern={isRegister ? "[a-zA-Z0-9_.\\-]+" : undefined}
        title={isRegister ? t("auth.usernameHint") : undefined}
        placeholder={t("auth.username")}
        aria-label={t("auth.username")}
        defaultValue={state?.username}
        className={inputClassName}
      />
      <input
        name="password"
        type="password"
        required
        autoComplete={isRegister ? "new-password" : "current-password"}
        minLength={isRegister ? 8 : undefined}
        placeholder={t("auth.password")}
        aria-label={t("auth.password")}
        className={inputClassName}
      />
      {isRegister && <p className="type-small text-secondary">{t("auth.passwordHint")}</p>}

      {state?.error && (
        <p role="alert" className="type-small text-accent">
          {t(state.error)}
        </p>
      )}

      <button
        type="submit"
        disabled={isPending}
        className="h-[43px] self-end rounded-[10px] bg-boxes-primary px-[13px] font-sans text-sm font-semibold text-secondary hover:opacity-90 disabled:opacity-60"
      >
        {t(isRegister ? "auth.register.submit" : "auth.login.submit")}
      </button>

      <p className="type-body text-center text-primary">
        {t(isRegister ? "auth.register.haveAccount" : "auth.login.noAccount")}{" "}
        <Link href={isRegister ? "/login" : "/register"} className="text-accent underline underline-offset-2">
          {t(isRegister ? "auth.register.toLogin" : "auth.login.toRegister")}
        </Link>
      </p>
    </form>
  );
}
