import Image from "next/image";

export function Logo({ className }: { className?: string }) {
  return (
    <Image
      src="/logo.png"
      alt=""
      aria-hidden="true"
      width={142}
      height={130}
      className={["object-contain", className].filter(Boolean).join(" ")}
      priority
    />
  );
}
