import { AuthForm } from "@/components/auth-form";
import { redirectIfSignedIn } from "@/lib/auth-page";
import { getTranslations } from "@/lib/i18n/server";
import { loginAction } from "./actions";

export default async function LoginPage() {
  await redirectIfSignedIn();
  const t = await getTranslations();

  return (
    <div className="mx-auto flex w-full max-w-[370px] flex-col gap-content">
      <h1 className="type-headline text-accent">{t("auth.login.title")}</h1>
      <p className="type-body text-primary">{t("auth.login.intro")}</p>
      <AuthForm mode="login" action={loginAction} />
    </div>
  );
}
