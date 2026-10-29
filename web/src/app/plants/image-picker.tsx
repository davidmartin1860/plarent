"use client";

import { useEffect, useState } from "react";
import { useTranslations } from "@/lib/i18n";

export function ImagePicker({ savedImageUrl }: { savedImageUrl?: string | null }) {
  const t = useTranslations();
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);

  useEffect(() => {
    return () => {
      if (previewUrl) {
        URL.revokeObjectURL(previewUrl);
      }
    };
  }, [previewUrl]);

  const shown = previewUrl ?? savedImageUrl;

  return (
    <label className="flex size-[95px] cursor-pointer items-center justify-center overflow-hidden rounded-full border border-secondary focus-within:ring-2 focus-within:ring-secondary-title">
      {shown ? (
        <img src={shown} alt="" className="size-full object-cover" />
      ) : (
        <span className="type-small text-center text-secondary">{t("plants.new.addImage")}</span>
      )}
      <input
        name="image"
        type="file"
        accept="image/jpeg,image/png,image/webp,image/gif"
        aria-label={t("plants.new.addImage")}
        className="sr-only"
        onChange={(event) => {
          const file = event.target.files?.[0];
          setPreviewUrl(file ? URL.createObjectURL(file) : null);
        }}
      />
    </label>
  );
}
