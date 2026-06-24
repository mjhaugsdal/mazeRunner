import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import type { WsEventType } from "./types";

export type WsStatus = "connecting" | "connected" | "disconnected" | "error";

export type GameStateMessage = {
  event: WsEventType;
  playerId: number;
  playerName: string;
  position: {
    first: number;
    second: number;
  };
};

type Handlers = {
  onStatusChange: (status: WsStatus) => void;
  onGameEvent: (message: GameStateMessage) => void;
  onError: (error: string) => void;
};

function trimTrailingSlash(url: string): string {
  return url.endsWith("/") ? url.slice(0, -1) : url;
}

export function createGameSocket(baseHttpUrl: string, handlers: Handlers) {
  const endpoint = `${trimTrailingSlash(baseHttpUrl)}/ws`;
  const SockJsCtor = ((SockJS as unknown as { default?: typeof SockJS }).default ?? SockJS) as unknown as new (url: string) => WebSocket;

  const client = new Client({
    reconnectDelay: 3000,
    webSocketFactory: () => new SockJsCtor(endpoint),
    onConnect: () => {
      handlers.onStatusChange("connected");
      client.subscribe("/topic/game-state", (frame) => {
        try {
          const parsed = JSON.parse(frame.body) as GameStateMessage;
          handlers.onGameEvent(parsed);
        } catch {
          handlers.onError("Klarte ikke lese WebSocket-melding");
        }
      });
    },
    onStompError: (frame) => {
      handlers.onStatusChange("error");
      handlers.onError(frame.headers.message ?? "STOMP-feil");
    },
    onWebSocketClose: () => {
      handlers.onStatusChange("disconnected");
    },
    onWebSocketError: () => {
      handlers.onStatusChange("error");
      handlers.onError("WebSocket-feil");
    }
  });

  return {
    connect: () => {
      handlers.onStatusChange("connecting");
      client.activate();
    },
    disconnect: () => {
      client.deactivate();
      handlers.onStatusChange("disconnected");
    }
  };
}

