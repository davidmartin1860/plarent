import { notFound } from "next/navigation";
import Link from "next/link";
import { getPlant } from "@/lib/api";

export default async function PlantDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const plant = await getPlant(id);

  if (!plant) {
    notFound();
  }

  return (
    <div className="rounded-lg border border-stone-200 bg-white p-6 shadow-sm">
      <Link href="/" className="text-sm text-emerald-700 underline underline-offset-2">
        ← Back to plants
      </Link>
      <h1 className="mt-3 text-2xl font-semibold text-stone-900">{plant.name}</h1>
      {plant.species && <p className="italic text-stone-500">{plant.species}</p>}

      <dl className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div>
          <dt className="text-xs uppercase tracking-wide text-stone-400">Location</dt>
          <dd className="text-stone-800">{plant.location ?? "—"}</dd>
        </div>
        <div>
          <dt className="text-xs uppercase tracking-wide text-stone-400">Acquired</dt>
          <dd className="text-stone-800">{plant.acquiredDate ?? "—"}</dd>
        </div>
      </dl>

      {plant.notes && (
        <div className="mt-6">
          <dt className="text-xs uppercase tracking-wide text-stone-400">Notes</dt>
          <dd className="whitespace-pre-wrap text-stone-800">{plant.notes}</dd>
        </div>
      )}
    </div>
  );
}
