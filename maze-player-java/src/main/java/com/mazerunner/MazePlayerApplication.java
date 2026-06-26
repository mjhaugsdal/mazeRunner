package com.mazerunner;

import com.mazerunner.client.MazeApiClient;
import com.mazerunner.game.GameEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class MazePlayerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MazePlayerApplication.class, args);
    }
}

@Component
class MazePlayerRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(MazePlayerRunner.class);

    @Value("${maze.player.name:JavaBot}")
    private String playerName;

    private final MazeApiClient apiClient;
    private final GameEngine gameEngine;

    public MazePlayerRunner(MazeApiClient apiClient, GameEngine gameEngine) {
        this.apiClient = apiClient;
        this.gameEngine = gameEngine;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            logger.info("Maze Player starting");
            logger.info("Player name: {}", playerName);

            // 1. Create player
            String playerId = apiClient.createPlayer(playerName);
            logger.info("Player registered: {}", playerId);

            // 2. Start game
            gameEngine.play();

            logger.info("Game finished");
            logger.info("Total moves: {}", gameEngine.getMoveCount());
            logger.info("Goal reached: {}", gameEngine.isGoalReached());

        } catch (Exception e) {
            logger.error("Fatal error in maze player", e);
            System.exit(1);
        }
    }
}
