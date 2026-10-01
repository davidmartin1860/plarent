import Link from "next/link";

export default function NotFound() {
  return (
    <div className="rounded-lg border border-dashed border-stone-300 p-8 text-center">
      <p className="text-stone-600">We couldn&apos;t find that plant.</p>
      <Link href="/" className="mt-3 inline-block text-emerald-700 underline underline-offset-2">
        Back to plants
      </Link>
    </div>
  );
}
