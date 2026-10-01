import { en } from "./locales/en.generated";

type Translations = typeof en;

type DotPaths<T> = T extends string
  ? never
  : {
      [K in keyof T & string]: T[K] extends string ? K : `${K}.${DotPaths<T[K]>}`;
    }[keyof T & string];

export type TranslationKey = DotPaths<Translations>;

const dictionary: Translations = en;

function resolve(key: TranslationKey): string {
  const value = key.split(".").reduce<unknown>((node, segment) => {
    if (node && typeof node === "object" && segment in node) {
      return (node as Record<string, unknown>)[segment];
    }
    return undefined;
  }, dictionary);

  if (typeof value !== "string") {
    throw new Error(`Missing translation for key "${key}"`);
  }

  return value;
}

export function t(key: TranslationKey, params?: Record<string, string | number>): string {
  const template = resolve(key);
  if (!params) {
    return template;
  }

  return template.replace(/{{(\w+)}}/g, (match, token: string) =>
    token in params ? String(params[token]) : match,
  );
}
