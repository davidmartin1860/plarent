import Link from "next/link";
import { getMe } from "@/lib/api";
import { getTranslations } from "@/lib/i18n/server";
import { MOCK_PROFILE } from "@/lib/mock-profile";
import { logoutAction } from "./actions";

export default async function ProfilePage() {
  const t = await getTranslations();
  const user = await getMe();
  // Only the name comes from the account so far; the stats are still placeholders.
  const profile = { ...MOCK_PROFILE, name: user.username };

  const stats = [
    { value: profile.plantCount, label: t("profile.stats.plants") },
    { value: profile.points, label: t("profile.stats.points") },
    { value: profile.formerPlantCount, label: t("profile.stats.formerPlants") },
  ];

  return (
    <div className="flex w-full flex-col gap-6">
      <h1 className="type-headline text-accent">{t("profile.title")}</h1>

      <section className="grid grid-cols-2 items-center gap-4 rounded-[20px] bg-boxes-secondary px-6 py-6 shadow-[0_5px_16px_rgba(53,66,56,0.07)]">
        <div className="flex flex-col items-center gap-4">
          {profile.avatarUrl ? (
            <img
              src={profile.avatarUrl}
              alt=""
              className="size-28 rounded-full object-cover"
            />
          ) : (
            <div className="flex size-28 items-center justify-center rounded-full bg-boxes-primary">
              <img src="/home/day-plant.svg" alt="" width={40} height={58} />
            </div>
          )}
          <p className="font-display text-2xl font-semibold text-primary">{profile.name}</p>
        </div>

        <dl className="flex flex-col gap-4">
          {stats.map((stat) => (
            <div key={stat.label} className="flex max-w-32 flex-col-reverse border-b border-primary pb-2">
              <dt className="type-small text-primary">{stat.label}</dt>
              <dd className="type-body text-primary">{stat.value}</dd>
            </div>
          ))}
        </dl>
      </section>

      <nav className="flex flex-col">
        <SettingsRow href="/profile/language" label={t("profile.language.label")} />
        <SettingsRow href="/profile/appearance" label={t("profile.appearance.label")} />
      </nav>

      <form action={logoutAction} className="flex justify-center">
        <button type="submit" className="type-body text-accent underline underline-offset-2 hover:opacity-80">
          {t("auth.logout")}
        </button>
      </form>
    </div>
  );
}

function SettingsRow({ href, label }: { href: string; label: string }) {
  return (
    <Link href={href} className="flex items-center justify-between px-4 py-4 text-primary">
      <span className="type-body text-lg">{label}</span>
      <svg
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
        className="size-8"
        aria-hidden
      >
        <path d="M9 6l6 6-6 6" />
      </svg>
    </Link>
  );
}
