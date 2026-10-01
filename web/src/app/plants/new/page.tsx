import { createPlantAction } from "./actions";

export default function NewPlantPage() {
  return (
    <div className="rounded-lg border border-stone-200 bg-white p-6 shadow-sm">
      <h1 className="text-2xl font-semibold text-stone-900">Add a plant</h1>

      <form action={createPlantAction} className="mt-6 space-y-4">
        <Field label="Name" name="name" required />
        <Field label="Species" name="species" placeholder="Monstera deliciosa" />
        <Field label="Location" name="location" placeholder="Living room window" />
        <Field label="Acquired date" name="acquiredDate" type="date" />
        <div>
          <label htmlFor="notes" className="block text-sm font-medium text-stone-700">
            Notes
          </label>
          <textarea
            id="notes"
            name="notes"
            rows={3}
            className="mt-1 block w-full rounded-md border border-stone-300 px-3 py-2 shadow-sm focus:border-emerald-500 focus:outline-none focus:ring-1 focus:ring-emerald-500"
          />
        </div>

        <button
          type="submit"
          className="rounded-md bg-emerald-700 px-4 py-2 font-medium text-white hover:bg-emerald-800"
        >
          Save plant
        </button>
      </form>
    </div>
  );
}

function Field({
  label,
  name,
  type = "text",
  required = false,
  placeholder,
}: {
  label: string;
  name: string;
  type?: string;
  required?: boolean;
  placeholder?: string;
}) {
  return (
    <div>
      <label htmlFor={name} className="block text-sm font-medium text-stone-700">
        {label}
        {required && <span className="text-emerald-700"> *</span>}
      </label>
      <input
        id={name}
        name={name}
        type={type}
        required={required}
        placeholder={placeholder}
        className="mt-1 block w-full rounded-md border border-stone-300 px-3 py-2 shadow-sm focus:border-emerald-500 focus:outline-none focus:ring-1 focus:ring-emerald-500"
      />
    </div>
  );
}
