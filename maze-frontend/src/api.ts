import type { DiscoveredMap, GameState, Maze } from "./types";

const API_BASE = import.meta.env.VITE_API_BASE ?? "";

function toUrl(path: string): string {
  return `${API_BASE}${path}`;
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(toUrl(path), {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...(init?.headers ?? {})
    }
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || `Request failed with status ${response.status}`);
  }

  return response.json() as Promise<T>;
}

async function getMaze(): Promise<Maze> {
  return request<Maze>("/gamecontrol/maze");
}

async function getPlayers(): Promise<GameState["players"]> {
  return request<GameState["players"]>("/gamecontrol/players");
}

export async function getGameState(): Promise<GameState> {
  const [maze, players] = await Promise.all([getMaze(), getPlayers()]);
  return { players, maze, activePlayerId: null };
}

export async function getDiscoveredMap(playerId: number): Promise<DiscoveredMap> {
  return request<DiscoveredMap>(`/gamecontrol/${playerId}/discovered-map`);
}

export const apiConfig = {
  baseUrl: API_BASE,
  toUrl
};
