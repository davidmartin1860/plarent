import { t } from "@/lib/i18n";

// Placeholder until the signed-in user's name is available.
const USER_NAME = "Plarent user";

export default function HomePage() {
  const weekday = new Intl.DateTimeFormat("en-US", { weekday: "long" }).format(new Date());

  return (
    <div className="flex w-full flex-col gap-content">
      <h1 className="type-headline text-accent">{t("home.greeting", { name: USER_NAME })}</h1>
      <section className="mx-auto flex w-full max-w-[370px] items-center justify-between rounded-[20px] bg-boxes-primary px-4 py-[14px] shadow-[0_5px_16px_rgba(53,66,56,0.07)]">
        <div className="flex items-center gap-[10px]">
          <div className="flex size-[34px] shrink-0 items-center justify-center rounded-full bg-beige">
            <img src="/home/day-plant.svg" alt="" width={18} height={26} />
          </div>
          <div className="flex flex-col gap-[2px] text-secondary">
            <p className="type-body">{t("home.dayTitle", { weekday })}</p>
            {/* The completed and total task counts should be shown here once tasks exist. */}
            <p className="type-small">{t("home.allDone")}</p>
          </div>
        </div>
        <img src="/home/all-done.svg" alt="" width={28.9453} height={28.9453} className="shrink-0" />
      </section>
    </div>
  );
}
