export function Logo({ className }: { className?: string }) {
  return (
    <svg
      className={className}
      viewBox="0 0 40 40"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      aria-hidden="true"
    >
      <circle cx="20" cy="20" r="20" className="fill-emerald-700 dark:fill-emerald-600" />
      <path
        d="M20 28c0-6 4.5-10.5 10-12-1.5 7-5.5 11-10 12Z"
        className="fill-emerald-100 dark:fill-emerald-200"
      />
      <path
        d="M20 28c0-6-4.5-10.5-10-12 1.5 7 5.5 11 10 12Z"
        className="fill-emerald-200 dark:fill-emerald-300"
      />
      <path
        d="M20 28V14"
        className="stroke-emerald-50 dark:stroke-emerald-950"
        strokeWidth="2"
        strokeLinecap="round"
      />
    </svg>
  );
}
