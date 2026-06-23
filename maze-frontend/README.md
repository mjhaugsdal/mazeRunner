# maze-frontend

Maze frontend kommuniserer med maze-server for å vise labyrinten og spillerne
Frontend for Maze-backend, utviklet med React + TypeScript + Vite.

## Funksjoner

- Opprett spiller/spill via `POST /game`
- Hent retningsvisning via `GET /game/{playerId}/view`
- Flytt spilleren via `POST /game/{playerId}/move` med `{ "direction": "NORTH|SOUTH|EAST|WEST" }`
- Hent spillerens rømningsstatus via `GET /game/{playerId}/status`
- Abonner på `/topic/game-state` via STOMP/SockJS (`/ws`) for sanntidsoppdateringer
- Enkelt brukergrensesnitt for spiller- og spillstatus

## Krav

- Node.js 20 eller nyere
- Backend kjører på `http://localhost:8080` (standard)

## Oppsett

1. Installer avhengigheter.
2. Valgfritt: Sett `VITE_API_BASE` i `.env.local`.
3. Start utviklingsserveren.

## Kommandoer

```bash
npm install
npm run dev
npm run test
npm run build
```

## Miljøvariabler

Kopier `.env.example` til `.env.local` dersom backend kjører på en annen vert:

```bash
cp .env.example .env.local
```

Rediger deretter `.env.local`:

```bash
VITE_API_BASE=http://localhost:8080
```

## Prosjektstruktur

- `src/App.tsx` – hovedgrensesnitt og spillinteraksjoner
- `src/api.ts` – typesikker API-klient
- `src/types.ts` – delte typer for frontend
- `src/api.test.ts` – enkel test for API-konfigurasjonen
