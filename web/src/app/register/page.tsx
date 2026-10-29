import { AuthForm } from "@/components/auth-form";
import { redirectIfSignedIn } from "@/lib/auth-page";
import { getTranslations } from "@/lib/i18n/server";
import { registerAction } from "./actions";

export default async function RegisterPage() {
  await redirectIfSignedIn();
  const t = await getTranslations();

  return (
    <div className="mx-auto flex w-full max-w-[370px] flex-col gap-content">
      <h1 className="type-headline text-accent">{t("auth.register.title")}</h1>
      <p className="type-body text-primary">{t("auth.register.intro")}</p>
      <AuthForm mode="register" action={registerAction} />
    </div>
  );
}
