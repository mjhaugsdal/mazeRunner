package com.mazerunner.navigation;

import com.mazerunner.map.Direction;
import com.mazerunner.map.MapMemory;
import com.mazerunner.map.Position;
import com.mazerunner.pathfinding.PathFinder;

import java.util.*;

public class NavigationStrategy {
    private final MapMemory mapMemory;
    private final PathFinder pathFinder;

    public NavigationStrategy(MapMemory mapMemory, PathFinder pathFinder) {
        this.mapMemory = mapMemory;
        this.pathFinder = pathFinder;
    }

    public Optional<Direction> getNextMove() {
        Position current = mapMemory.getCurrentPosition();

        // Priority 1: If goal is discovered, navigate to goal
        if (mapMemory.isGoalDiscovered()) {
            Position goal = mapMemory.getGoalPosition();
            List<Direction> path = pathFinder.findShortestPath(current, goal);
            if (!path.isEmpty()) {
                return Optional.of(path.get(0));
            }
        }

        // Priority 2: Explore unknown areas - prefer direction with unknown neighbors
        for (Direction dir : Direction.values()) {
            Position next = current.move(dir);
            if (mapMemory.getTile(next).isWalkable() && !mapMemory.getUnknownNeighbors(next).isEmpty()) {
                return Optional.of(dir);
            }
        }

        // Priority 3: Navigate to nearest frontier
        Optional<Direction> frontierDirection = navigateToNearestFrontier(current);
        if (frontierDirection.isPresent()) {
            return frontierDirection;
        }

        // Priority 4: Try any walkable direction
        for (Direction dir : Direction.values()) {
            Position next = current.move(dir);
            if (mapMemory.isWalkable(next)) {
                return Optional.of(dir);
            }
        }

        return Optional.empty();
    }

    private Optional<Direction> navigateToNearestFrontier(Position current) {
        Set<Position> frontiers = mapMemory.getFrontiers();

        if (frontiers.isEmpty()) {
            return Optional.empty();
        }

        Position nearest = frontiers.stream()
                .min(Comparator.comparingDouble(f -> distance(current, f)))
                .orElse(null);

        if (nearest == null) {
            return Optional.empty();
        }

        List<Direction> path = pathFinder.findShortestPath(current, nearest);
        if (!path.isEmpty()) {
            return Optional.of(path.get(0));
        }

        return Optional.empty();
    }

    private double distance(Position a, Position b) {
        int dx = a.getX() - b.getX();
        int dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
