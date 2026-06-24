import { describe, expect, it } from "vitest";
import { apiConfig } from "./api";

describe("apiConfig", () => {
  it("builds absolute urls for api paths", () => {
    expect(apiConfig.toUrl("/game/1/view")).toContain("/game/1/view");
    expect(apiConfig.baseUrl.length).toBeGreaterThan(0);
  });
});

