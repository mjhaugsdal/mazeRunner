# Teknologiske føringer
<Teknologiske valg bestemmes av den som løser oppgaven>

Dersom Kotlin JVM 21 og Gradle Kotlin DSL er tilgjengelig, skal disse benyttes. 
For kommunikasjon med maze-serveren skal `java.net.http.HttpClient` brukes, og for JSON-håndtering skal Jackson Kotlin Module benyttes. 
Enhetstester skal skrives med JUnit 5.

MÅ bruke:

* Kotlin JVM 21
* Gradle Kotlin DSL
* `java.net.http.HttpClient`
* Jackson Kotlin Module
* JUnit 5

SKAL IKKE bruke:

* Spring Boot
* Database
* WebSocket
* Eksterne biblioteker for pathfinding
