import { useEffect, useMemo, useState } from "react";
import { apiConfig, getDiscoveredMap, getGameState } from "./api";
import type { DiscoveredMap, GameState } from "./types";
import { createGameSocket, type WsStatus } from "./websocket";

function playFanfare(): void {
  try {
    const ctx = new AudioContext();
    // Kort "Da-da"
    const notes = [587.33, 587.33];
    notes.forEach((freq, i) => {
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.frequency.value = freq;
      osc.type = "square";
      const start = ctx.currentTime + i * 0.12;
      gain.gain.setValueAtTime(0.22, start);
      gain.gain.exponentialRampToValueAtTime(0.001, start + 0.16);
      osc.start(start);
      osc.stop(start + 0.18);
    });
  } catch {
    // Web Audio ikke tilgjengelig
  }
}

function App() {
  const backendBase = apiConfig.baseUrl || window.location.origin;
  const swaggerUrl = `${backendBase}/api/swagger-ui.html`;
  const [gameState, setGameState] = useState<GameState | null>(null);
  const [discoveredMaps, setDiscoveredMaps] = useState<DiscoveredMap[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [wsStatus, setWsStatus] = useState<WsStatus>("disconnected");

  async function refreshState(silent = false): Promise<void> {
    if (!silent) setIsLoading(true);
    try {
      const state = await getGameState();
      setGameState(state);
      setError(null);
      // Hent oppdaget kart for alle spillere
      const maps = await Promise.all(
        state.players.map((p) => getDiscoveredMap(p.id).catch(() => ({ playerId: p.id, tiles: [] })))
      );
      setDiscoveredMaps(maps);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Ukjent feil");
    } finally {
      if (!silent) setIsLoading(false);
    }
  }

  // Last inn labyrint ved oppstart
  useEffect(() => {
    void refreshState();
  }, []);

  // Polling hvert 500ms
  useEffect(() => {
    const timer = setInterval(() => {
      void refreshState(true);
    }, 500);
    return () => clearInterval(timer);
  }, []);

  // WebSocket for sanntidsoppdateringer
  useEffect(() => {
    const socket = createGameSocket(apiConfig.baseUrl, {
      onStatusChange: (status) => setWsStatus(status),
      onGameEvent: (message) => {
        if (message.event === "PLAYER_EXITED") playFanfare();
        void refreshState(true);
      },
      onError: (message) => setError(message)
    });
    socket.connect();
    return () => socket.disconnect();
  }, []);

  const renderedDiscoveredMaps = useMemo(() => {
    if (!gameState?.maze) return null;
    const { width, height } = gameState.maze;

    return discoveredMaps.map((dm) => {
      const player = gameState.players.find((p) => p.id === dm.playerId);
      if (!player) return null;

      const tileMap = new Map(dm.tiles.map((t) => [`${t.x},${t.y}`, t.tile]));

      const rows = Array.from({ length: height }, (_, y) =>
        Array.from({ length: width }, (_, x) => {
          const key = `${x},${y}`;
          const tile = tileMap.get(key);
          let className = "disc-cell unknown";
          if (tile === "WALL") className = "disc-cell wall";
          else if (tile === "FLOOR") className = "disc-cell floor";
          else if (tile === "START") className = "disc-cell start";
          else if (tile === "GOAL") className = "disc-cell goal";
          return <div className={className} key={key} />;
        })
      );

      return (
        <div key={dm.playerId} className="discovered-map">
          <h3 style={{ color: player.color }}>🐭 {player.name}</h3>
          <div
            className="disc-grid"
            style={{ gridTemplateColumns: `repeat(${width}, 4px)` }}
          >
            {rows}
          </div>
        </div>
      );
    });
  }, [discoveredMaps, gameState]);

  const renderedMaze = useMemo(() => {
    if (!gameState?.maze) return [] as JSX.Element[];

    return gameState.maze.layout.map((row, y) => {
      const cells = row.split("").map((ch, x) => {
        const playersHere = gameState.players.filter(
          (p) => p.position.first === x && p.position.second === y
        );
        const player = playersHere[0];

        let className = "maze-cell floor";
        let content = "";

        if (ch === "#") {
          className = "maze-cell wall";
        } else if (ch === "S") {
          className = "maze-cell start";
          content = "START";
        } else if (ch === "E") {
          className = "maze-cell goal";
          content = "MÅL";
        }

        if (player) {
          className = `maze-cell player player-${player.color}`;
          content = "🐭";
        }

        return (
          <div className={className} key={`${x}-${y}`} title={`${x},${y}`}>
            {content}
          </div>
        );
      });

      return (
        <div className="maze-row" key={`row-${y}`}>
          {cells}
        </div>
      );
    });
  }, [gameState]);

  return (
    <main className="app">
      <header className="card header-bar">
        <h1>Maze Challenge</h1>
        <div className="header-actions">
          <a className="swagger-link" href={swaggerUrl} rel="noreferrer" target="_blank">
            {swaggerUrl}
          </a>
          <button disabled={isLoading} onClick={() => void refreshState()} type="button">
            Oppdater
          </button>
        </div>
      </header>

      <div className="game-layout">
        <section className="card maze-card">
          {isLoading && <p>Laster...</p>}
          {error && <p className="error">Feil: {error}</p>}
          {!gameState?.maze && !isLoading && <p>Laster labyrint...</p>}
          {gameState?.maze && (
            <div
              className="maze-grid"
              style={{ gridTemplateColumns: `repeat(${gameState.maze.width}, 24px)` }}
            >
              {renderedMaze}
            </div>
          )}
        </section>

        <section className="card players-panel">
          <h2>Spillere ({gameState?.players.length ?? 0})</h2>
          {!gameState?.players.length && <p>Venter på spillere...</p>}
          <ul>
            {gameState?.players.map((player, index) => (
              <li key={player.id} style={{ color: player.color }}>
                #{index + 1} {player.name} @ {player.position.first},{player.position.second} - trekk: {player.moveCount}
                {player.returnMoveCount != null && ` (retur: ${player.returnMoveCount})`}
              </li>
            ))}
          </ul>
          <hr />
          <p className="connection-info">Backend: {apiConfig.baseUrl}</p>
          <p className="connection-info">WebSocket: {wsStatus}</p>
        </section>
      </div>

      {renderedDiscoveredMaps && renderedDiscoveredMaps.length > 0 && (
        <section className="card discovered-maps-section">
          <h2 style={{ margin: "0 0 0.4rem", fontSize: "0.85rem" }}>Oppdaget kart</h2>
          <div className="discovered-maps-grid">
            {renderedDiscoveredMaps}
          </div>
        </section>
      )}
    </main>
  );
}

export default App;
