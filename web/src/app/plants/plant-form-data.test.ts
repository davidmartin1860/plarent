import { describe, expect, it } from "vitest";
import { imageFromForm, plantImageUrl } from "./plant-form-data";

describe("plant form images", () => {
  it("ignores an empty file input", () => {
    const formData = new FormData();
    formData.set("image", new File([], "empty.jpg", { type: "image/jpeg" }));

    expect(imageFromForm(formData)).toBeNull();
  });

  it("keeps a selected image", () => {
    const formData = new FormData();
    const file = new File([new Uint8Array([1, 2, 3])], "plant.jpg", { type: "image/jpeg" });
    formData.set("image", file);

    expect(imageFromForm(formData)).toBe(file);
  });

  it("builds a cache-busted image url only when a photo is stored", () => {
    expect(plantImageUrl({ id: "abc", updatedAt: "2026-10-29T18:00:00Z", hasImage: false })).toBeNull();
    expect(plantImageUrl({ id: "abc", updatedAt: "2026-10-29T18:00:00Z", hasImage: true })).toBe(
      "/plants/abc/image?v=2026-10-29T18%3A00%3A00Z",
    );
  });
});
