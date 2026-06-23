# maze-player

Dere skal Vibe kode en maze-player.
Oppgaven er å kommunisere med maze-server og navigere i labyrinten.
Kan Vibe kodes i hvilket som helst språk.
Det kan være en fordel å spesifisere på forhånd hvilket språk, rammeverk og foretrukket byggeverktøy 

## Kotlin:
[maze-player-kotlin-tools](maze-player-kotlin-tools.md)

## Python
[maze-player-python-tools](maze-player-python-tools.md)

Løs en og en oppgave.

# Oppgaver
## Oppgave 1
Bruk API´et i `[openapi-spec.yaml](openapi-spec.yaml)` til å opprette en spiller i backend.
Når en spiller er registrert vises navnet i maze-frontend

## Oppgave 2
Bruk API´et til å flytte spilleren rundt i labyrinten.
Hvert trekk gir tilbake informasjon om hvilke retninger som er åpne

## Oppgave 3
Start automatisk utforsking av labyrinten.
Bygg opp et internt kart over oppdagede områder i labyrinten.

## Oppgave 4
Send via API´et for hvert trekk, informasjon om alle områder som fortløppende er avdekket i labyrinten.

## Oppgave 5
Finn labyrintens MÅL.

## Oppgave 6
Når MÅL er funnet, gå raskeste vei tilbake til START poisjonen
Den som har funnet kortest vei mellom START og MÅL har vunnet spillet

Din oppgave er å implementere en autonom bot som navigerer gjennom en labyrint ved å kommunisere med en kjørende
`maze-server`.

## Kom i gang

## Hva du skal implementere

Se `maze-player-spec.md` for fullstendig beskrivelse. 
