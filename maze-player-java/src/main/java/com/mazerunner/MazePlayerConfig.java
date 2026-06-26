package com.mazerunner;

import com.mazerunner.client.MazeApiClient;
import com.mazerunner.game.GameEngine;
import com.mazerunner.map.MapMemory;
import com.mazerunner.map.Position;
import com.mazerunner.navigation.NavigationStrategy;
import com.mazerunner.pathfinding.PathFinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MazePlayerConfig {

    @Value("${maze.move-delay-ms:100}")
    private long moveDelayMs;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public MapMemory mapMemory() {
        return new MapMemory(new Position(0, 0));
    }

    @Bean
    public PathFinder pathFinder(MapMemory mapMemory) {
        return new PathFinder(mapMemory);
    }

    @Bean
    public NavigationStrategy navigationStrategy(MapMemory mapMemory, PathFinder pathFinder) {
        return new NavigationStrategy(mapMemory, pathFinder);
    }

    @Bean
    public GameEngine gameEngine(MazeApiClient apiClient,
                                 MapMemory mapMemory,
                                 NavigationStrategy navigationStrategy,
                                 PathFinder pathFinder) {
        return new GameEngine(apiClient, mapMemory, navigationStrategy, pathFinder, moveDelayMs);
    }
}
