export type Position = {
  first: number;  // x-koordinat (0 = venstre kant)
  second: number; // y-koordinat (0 = øverste kant)
};

export type Player = {
  id: number;
  name: string;
  color: string; // "red" | "blue" | "green" | "yellow" | "purple" | "orange"
  position: Position;
  moveCount: number;
  returnMoveCount?: number | null;
};

export type Maze = {
  width: number;
  height: number;
  layout: string[];        // Én streng per rad: '#', ' ', 'S', 'E'
  startPosition: Position;
  exitPosition: Position;
};

export type WsEventType = "PLAYER_CREATED" | "PLAYER_MOVED" | "PLAYER_EXITED";

export type DiscoveredTile = { x: number; y: number; tile: "WALL" | "FLOOR" | "START" | "GOAL" | "UNKNOWN" };

export type DiscoveredMap = {
  playerId: number;
  tiles: DiscoveredTile[];
};

export type GameState = {
  players: Player[];
  maze: Maze | null;
  activePlayerId: number | null;
};
