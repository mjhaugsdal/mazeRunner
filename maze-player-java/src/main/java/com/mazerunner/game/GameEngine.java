package com.mazerunner.game;

import com.mazerunner.client.MazeApiClient;
import com.mazerunner.map.Direction;
import com.mazerunner.map.MapMemory;
import com.mazerunner.map.Position;
import com.mazerunner.map.Tile;
import com.mazerunner.navigation.NavigationStrategy;
import com.mazerunner.pathfinding.PathFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GameEngine {
    private static final Logger logger = LoggerFactory.getLogger(GameEngine.class);
    private static final int MAX_MOVES = 10000;

    private final MazeApiClient apiClient;
    private final MapMemory mapMemory;
    private final NavigationStrategy navigationStrategy;
    private final PathFinder pathFinder;
    private final long moveDelayMs;

    private int moveCount = 0;
    private boolean goalReached = false;

    public GameEngine(MazeApiClient apiClient, MapMemory mapMemory,
                     NavigationStrategy navigationStrategy, PathFinder pathFinder,
                     long moveDelayMs) {
        this.apiClient = apiClient;
        this.mapMemory = mapMemory;
        this.navigationStrategy = navigationStrategy;
        this.pathFinder = pathFinder;
        this.moveDelayMs = moveDelayMs;
    }

    public void play() {
        logger.info("Starting maze game");

        while (!goalReached && moveCount < MAX_MOVES) {
            try {
                // 1. Get current view
                // 2. Update map
                updateMapFromCurrentPosition();

                // 3. Check if goal is reached
                if (mapMemory.getTile(mapMemory.getCurrentPosition()) == Tile.GOAL) {
                    goalReached = true;
                    logger.info("Goal reached! Move count: {}", moveCount);
                    returnToStart();
                    break;
                }

                // 4. Calculate next move
                Optional<Direction> nextMove = navigationStrategy.getNextMove();
                if (nextMove.isEmpty()) {
                    logger.warn("No valid moves available");
                    break;
                }

                // 5. Execute move
                executeMove(nextMove.get());

                // 6. Report discovered map periodically
                if (moveCount % 10 == 0) {
                    reportDiscoveredMap();
                }

                // 7. Wait
                Thread.sleep(moveDelayMs);

            } catch (InterruptedException e) {
                logger.error("Game interrupted", e);
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.error("Game error", e);
                break;
            }
        }

        logger.info("Game ended. Total moves: {}", moveCount);
    }

    private void updateMapFromCurrentPosition() {
        Position current = mapMemory.getCurrentPosition();
        // Mark current position as floor if not already known
        if (mapMemory.getTile(current) == Tile.UNKNOWN) {
            mapMemory.updateTile(current, Tile.FLOOR);
        }
    }

    private void executeMove(Direction direction) {
        logger.debug("Executing move: {}", direction);
        MazeApiClient.MoveResult result = apiClient.move(direction);

        if (result.success) {
            // Move was successful
            Position newPos = mapMemory.getCurrentPosition().move(direction);
            mapMemory.updateCurrentPosition(newPos);
            moveCount++;

            // Update tiles from view
            updateTilesFromView(direction, result.views);

            if (result.escaped) {
                goalReached = true;
            }
        } else {
            // Move was blocked - treat as wall
            Position blockedPos = mapMemory.getCurrentPosition().move(direction);
            mapMemory.updateTile(blockedPos, Tile.WALL);
            logger.debug("Move blocked, {} is a wall", blockedPos);

            // Still update view from remaining directions
            updateTilesFromView(direction, result.views);
        }
    }

    private void updateTilesFromView(Direction direction, Map<Direction, List<Tile>> views) {
        Position current = mapMemory.getCurrentPosition();

        for (Map.Entry<Direction, List<Tile>> entry : views.entrySet()) {
            Direction viewDir = entry.getKey();
            List<Tile> tiles = entry.getValue();

            Position pos = current;
            for (int i = 0; i < tiles.size(); i++) {
                pos = pos.move(viewDir);
                Tile tile = tiles.get(i);
                if (tile != Tile.UNKNOWN) {
                    mapMemory.updateTile(pos, tile);
                }
            }
        }
    }

    private void returnToStart() {
        logger.info("Returning to start position");
        Position start = mapMemory.getStartPosition();
        Position current = mapMemory.getCurrentPosition();

        List<Direction> path = pathFinder.findShortestPath(current, start);
        logger.info("Return path length: {}", path.size());

        for (Direction direction : path) {
            try {
                MazeApiClient.MoveResult result = apiClient.move(direction);
                if (result.success) {
                    mapMemory.updateCurrentPosition(current.move(direction));
                    moveCount++;
                    logger.debug("Return move executed: {}", direction);
                } else {
                    logger.warn("Return move blocked: {}", direction);
                    break;
                }
                Thread.sleep(moveDelayMs);
            } catch (InterruptedException e) {
                logger.error("Return interrupted", e);
                Thread.currentThread().interrupt();
                break;
            }
        }

        logger.info("Return complete. Total return moves: {}", path.size());
    }

    private void reportDiscoveredMap() {
        Map<Position, Tile> tiles = mapMemory.getAllTiles();
        List<MazeApiClient.DiscoveredTile> discovered = new ArrayList<>();

        for (Map.Entry<Position, Tile> entry : tiles.entrySet()) {
            Position pos = entry.getKey();
            Tile tile = entry.getValue();
            String tileStr = switch (tile) {
                case WALL -> "WALL";
                case FLOOR -> "FLOOR";
                case START -> "START";
                case GOAL -> "GOAL";
                default -> "UNKNOWN";
            };
            discovered.add(new MazeApiClient.DiscoveredTile(pos.getX(), pos.getY(), tileStr));
        }

        apiClient.updateDiscoveredMap(discovered);
        logger.debug("Reported {} discovered tiles", discovered.size());
    }

    public int getMoveCount() {
        return moveCount;
    }

    public boolean isGoalReached() {
        return goalReached;
    }
}
