import { readdirSync, readFileSync, writeFileSync } from "node:fs";
import { fileURLToPath } from "node:url";
import path from "node:path";
import { load } from "js-yaml";

const localesDir = fileURLToPath(new URL("../src/lib/i18n/locales", import.meta.url));

function assertOnlyStrings(node, keyPath) {
  for (const [key, value] of Object.entries(node)) {
    const currentPath = keyPath ? `${keyPath}.${key}` : key;
    if (value !== null && typeof value === "object") {
      assertOnlyStrings(value, currentPath);
    } else if (typeof value !== "string") {
      throw new Error(`Translation "${currentPath}" must be a string, got ${typeof value}`);
    }
  }
}

const yamlFiles = readdirSync(localesDir).filter((file) => file.endsWith(".yaml"));

for (const file of yamlFiles) {
  const locale = path.basename(file, ".yaml");
  const source = readFileSync(path.join(localesDir, file), "utf8");
  const translations = load(source);
  assertOnlyStrings(translations, "");

  const outputPath = path.join(localesDir, `${locale}.generated.ts`);
  const contents = `// AUTO-GENERATED from ${file} by scripts/generate-i18n.mjs. Do not edit directly.

export const ${locale} = ${JSON.stringify(translations, null, 2)} as const;
`;
  writeFileSync(outputPath, contents);
  console.log(`Generated ${path.relative(process.cwd(), outputPath)}`);
}
