# Maze Challenge Backend

This project is a backend server for a Maze Challenge built using Kotlin and Spring Boot. The server allows multiple players to connect and navigate through a maze in real-time.

## Project Structure

The project is organized as follows:

```
maze-server
├── src
│   ├── main
│   │   ├── kotlin
│   │   │   └── com
│   │   │       └── mazechallenge
│   │   │           ├── MazeServerApplication.kt
│   │   │           ├── config
│   │   │           │   ├── WebSocketConfig.kt
│   │   │           │   └── OpenApiConfig.kt
│   │   │           ├── controller
│   │   │           │   ├── GameController.kt
│   │   │           │   └── PlayerController.kt
│   │   │           ├── service
│   │   │           │   ├── GameService.kt
│   │   │           │   ├── PlayerService.kt
│   │   │           │   └── MazeService.kt
│   │   │           ├── domain
│   │   │           │   ├── Player.kt
│   │   │           │   ├── Maze.kt
│   │   │           │   └── GameState.kt
│   │   │           ├── mapper
│   │   │           │   ├── PlayerMapper.kt
│   │   │           │   └── MazeMapper.kt
│   │   │           └── websocket
│   │   │               ├── GameWebSocketHandler.kt
│   │   │               └── GameStateMessage.kt
│   │   └── resources
│   │       ├── application.properties
│   │       ├── Maze1.txt
│   │       ├── Maze2.txt
│   │       └── templates
│   │           └── index.html
│   └── test
│       └── kotlin
│           └── com
│               └── mazechallenge
│                   ├── service
│                   │   ├── GameServiceTest.kt
│                   │   ├── PlayerServiceTest.kt
│                   │   └── MazeServiceTest.kt
│                   ├── controller
│                   │   └── GameControllerTest.kt
│                   └── integration
│                       └── GameIntegrationTest.kt
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Getting Started

### Prerequisites

- Kotlin 1.6 or higher
- Java 21
- Gradle 7.0 or higher

### Installation

1. Clone the repository:
   ```
   git clone <repository-url>
   ```

2. Navigate to the project directory:
   ```
   cd maze-server
   ```

3. Build the project using Gradle:
   ```
   ./gradlew build
   ```

4. Run the application:
   ```
   ./gradlew bootRun
   ```

### Usage

- Connect to the WebSocket endpoint to join the game.
- Players can register with a name and will be assigned a unique identifier.
- Players can navigate through the maze using the provided controls in the frontend.

### API Documentation

API specifications are automatically generated using OpenAPI. You can access the documentation at `/v3/api-docs`.

### Testing

Unit and integration tests are included in the project. To run the tests, use the following command:
```
./gradlew test
```

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any enhancements or bug fixes.

## License

This project is licensed under the MIT License. See the LICENSE file for details.