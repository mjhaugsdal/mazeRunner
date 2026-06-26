# Maze Player Java

Autonom bot som navigerer gjennom en labyrint ved å kommunisere med maze-server via REST API.

## Teknologi Stack

- **Java 11+**
- **Spring Boot 3.2**
- **Maven**
- **OpenAPI Generator** (generer API-klient fra openapi-spec.yaml)
- **JUnit 5** + **Mockito** (testing)

## Prosjektstruktur

```
maze-player-java/
├── pom.xml                          # Maven-config med OpenAPI-plugin
├── openapi-spec.yaml                # API-spesifikasjon (input til codegen)
├── src/
│   ├── main/
│   │   ├── java/com/mazerunner/
│   │   │   ├── client/              # Generert API-klient (og wrapper)
│   │   │   ├── game/                # GameEngine - koordinerer spillflyten
│   │   │   ├── map/                 # MapMemory, Position, Tile, Direction
│   │   │   ├── navigation/          # NavigationStrategy - bestemmer trekk
│   │   │   ├── pathfinding/         # PathFinder - BFS-algoritme
│   │   │   ├── MazePlayerApplication.java  # Main entry point
│   │   │   └── MazePlayerConfig.java       # Spring Bean-konfigurering
│   │   └── resources/
│   │       └── application.yaml     # Spring Boot config
│   └── test/
│       ├── java/com/mazerunner/...  # Unit tests
│       └── resources/
```

## Oppgaver

Implementeringen deles i 6 oppgaver som følger oppgave-listen i README.md:

1. **Oppgave 1:** Registrer spiller i backend (POST /game)
2. **Oppgave 2:** Bevegelse & synsfelt (POST /game/{playerId}/move/{direction})
3. **Oppgave 3:** Kartlegging & automatisk utforsking (MapMemory)
4. **Oppgave 4:** Rapporter oppdaget kart til backend
5. **Oppgave 5:** Navigasjonsstrategi & mål-søk
6. **Oppgave 6:** Retur til startposisjon via korteste vei

Alle 6 oppgavene er implementert i denne versjonen.

## Bygging

Forutsetter Java 11+ og Maven installert.

### Generer API-klient
```bash
mvn generate-sources
```

### Bygg prosjektet
```bash
mvn clean package
```

### Kjør unit tests
```bash
mvn test
```

## Kjøring

### Forutsetter
- maze-server kjører på `http://localhost:8080`

### Start maze-player
```bash
mvn spring-boot:run
```

Eller etter bygging:
```bash
java -jar target/maze-player-java-1.0.0.jar
```

## Konfigurering

Redigér `src/main/resources/application.yaml`:

```yaml
spring:
  application:
    name: maze-player

maze:
  server:
    url: http://localhost:8080     # API-server-adresse
  player:
    name: JavaBot                   # Spillernavn (maks 10 tegn)
  move-delay-ms: 100               # Venting mellom trekk (ms)
```

## Hovedkomponenter

### MapMemory
Lagrer kartdata med posisjoner og felttyper. Holder rede på:
- Startposisjon
- Nåværende posisjon
- Oppdagede felt
- Mål-posisjon (hvis oppdaget)

### PathFinder
Implementerer **BFS-algoritme** for korteste vei mellom to kjente posisjoner.

### NavigationStrategy
Bestemmer neste trekk basert på prioritet:
1. Utforske ukjente områder
2. Navigere til kjent mål
3. Navigere til nærmeste frontier
4. Fallback til tilgjengelig retning

### GameEngine
Koordinerer hele spillflyten:
1. Hent synsfelt
2. Oppdater kart
3. Beregn neste trekk
4. Utfør trekk
5. Rapporter oppdagelser
6. Vent
7. Gjenta til mål nådd
8. Returner til start

### MazeApiClient
Wrapper rundt REST API-kommunikasjon med:
- Retry-logikk (backoff)
- Error-handling
- View-parsing

## Testing

### Unit Tests
```bash
mvn test
```

Tests dekker:
- Kartoppdateringer (MapMemoryTest)
- Vegg-registrering
- Korteste vei-beregning (PathFinderTest)
- Navigasjonsstrategi & prioritering (NavigationStrategyTest)

Tests er uavhengige av kjørende backend.

## API-integrasjon

OpenAPI-klienten genereres automatisk fra `openapi-spec.yaml` når du kjører:
```bash
mvn generate-sources
```

Den genererte koden plasseres i `target/generated-sources/`.

Konfigurering av OpenAPI-generering er i `pom.xml`:
- Generator: `spring`
- Library: `spring-boot`
- Output: `target/generated-sources`

## Feilhåndtering

- **Nettverksfeil:** Automatisk retry med eksponentielt backoff (maks 3 forsøk)
- **Blokkert trekk:** Behandles som vegg, ny rute beregnes
- **Utilgjengelig mål:** Bruker frontier-strategien

## Logging

Standard Spring Boot-logging via SLF4J. Redigér `application.yaml` for å endre log-nivå:

```yaml
logging:
  level:
    com.mazerunner: DEBUG
```

## Videre utvikling

- Implementer caching av BFS-resultater
- Legge til strategier for optimal utforsking (f.eks. Right-hand rule)
- Statistikk og performance-analyse
- Visualisering av kartutvikling
