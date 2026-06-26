package com.mazerunner.client;

import com.mazerunner.map.Direction;
import com.mazerunner.map.Tile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.*;

@Service
public class MazeApiClient {
    private static final Logger logger = LoggerFactory.getLogger(MazeApiClient.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    @Value("${maze.server.url}")
    private String serverUrl;

    private final RestTemplate restTemplate;
    private String playerId;

    public MazeApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String createPlayer(String playerName) {
        try {
            String url = serverUrl + "/game?name=" + playerName;
            logger.info("Creating player: {}", playerName);

            Map<String, Object> response = restTemplate.postForObject(url, null, Map.class);
            if (response != null) {
                this.playerId = response.get("playerId").toString();
                logger.info("Player created with ID: {}", playerId);
                return playerId;
            }
        } catch (RestClientException e) {
            logger.error("Failed to create player", e);
            throw new RuntimeException("Failed to create player", e);
        }
        throw new RuntimeException("Failed to create player: invalid response");
    }

    public MoveResult move(Direction direction) {
        return retryOperation(() -> moveWithRetry(direction));
    }

    private MoveResult moveWithRetry(Direction direction) {
        try {
            String url = serverUrl + "/game/" + playerId + "/move/" + direction.name();
            logger.debug("Moving {} to {}", direction, url);

            Map<String, Object> response = restTemplate.postForObject(url, null, Map.class);
            if (response != null) {
                return parseMoveResponse(response);
            }
        } catch (RestClientException e) {
            logger.error("Failed to move in direction: {}", direction, e);
            throw e;
        }
        throw new RuntimeException("Failed to move: invalid response");
    }

    private MoveResult parseMoveResponse(Map<String, Object> response) {
        boolean success = (boolean) response.get("success");
        boolean escaped = (boolean) response.getOrDefault("escaped", false);

        Map<Direction, List<Tile>> views = new HashMap<>();
        views.put(Direction.NORTH, parseView((List<?>) response.get("north")));
        views.put(Direction.SOUTH, parseView((List<?>) response.get("south")));
        views.put(Direction.EAST, parseView((List<?>) response.get("east")));
        views.put(Direction.WEST, parseView((List<?>) response.get("west")));

        return new MoveResult(success, escaped, views);
    }

    private List<Tile> parseView(List<?> view) {
        List<Tile> tiles = new ArrayList<>();
        if (view != null) {
            for (Object item : view) {
                tiles.add(Tile.fromChar(((String) item).charAt(0)));
            }
        }
        return tiles;
    }

    public void updateDiscoveredMap(List<DiscoveredTile> tiles) {
        try {
            String url = serverUrl + "/game/" + playerId + "/discovered-map";
            Map<String, Object> request = Map.of("tiles", tiles);
            logger.debug("Updating discovered map for player {}", playerId);
            restTemplate.postForObject(url, request, Map.class);
        } catch (RestClientException e) {
            logger.error("Failed to update discovered map", e);
        }
    }

    private <T> T retryOperation(java.util.function.Supplier<T> operation) {
        RestClientException lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return operation.get();
            } catch (RestClientException e) {
                lastException = e;
                if (attempt < MAX_RETRIES) {
                    long delay = RETRY_DELAY_MS * attempt;
                    logger.warn("Attempt {} failed, retrying in {}ms", attempt, delay);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        throw lastException != null ? lastException : new RuntimeException("Operation failed");
    }

    public static class MoveResult {
        public final boolean success;
        public final boolean escaped;
        public final Map<Direction, List<Tile>> views;

        public MoveResult(boolean success, boolean escaped, Map<Direction, List<Tile>> views) {
            this.success = success;
            this.escaped = escaped;
            this.views = views;
        }
    }

    public static class DiscoveredTile {
        public int x;
        public int y;
        public String tile;

        public DiscoveredTile(int x, int y, String tile) {
            this.x = x;
            this.y = y;
            this.tile = tile;
        }
    }
}
