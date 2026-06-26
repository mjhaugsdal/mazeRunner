package com.mazerunner.pathfinding;

import com.mazerunner.map.Direction;
import com.mazerunner.map.MapMemory;
import com.mazerunner.map.Position;

import java.*;
import java.util.*;

public class PathFinder {
    private final MapMemory mapMemory;

    public PathFinder(MapMemory mapMemory) {
        this.mapMemory = mapMemory;
    }

    public List<Direction> findShortestPath(Position from, Position to) {
        if (from.equals(to)) {
            return Collections.emptyList();
        }

        Queue<Position> queue = new LinkedList<>();
        Map<Position, Position> parent = new HashMap<>();
        Set<Position> visited = new HashSet<>();

        queue.add(from);
        visited.add(from);

        while (!queue.isEmpty()) {
            Position current = queue.poll();

            if (current.equals(to)) {
                return reconstructPath(parent, from, to);
            }

            for (Direction direction : Direction.values()) {
                Position neighbor = current.move(direction);

                if (visited.contains(neighbor)) {
                    continue;
                }

                if (!mapMemory.isWalkable(neighbor)) {
                    continue;
                }

                visited.add(neighbor);
                parent.put(neighbor, current);
                queue.add(neighbor);
            }
        }

        // No path found
        return Collections.emptyList();
    }

    private List<Direction> reconstructPath(Map<Position, Position> parent, Position from, Position to) {
        List<Direction> path = new ArrayList<>();
        Position current = to;

        while (!current.equals(from)) {
            Position prev = parent.get(current);
            Direction direction = getDirection(prev, current);
            path.add(0, direction);
            current = prev;
        }

        return path;
    }

    private Direction getDirection(Position from, Position to) {
        if (to.getX() > from.getX()) {
            return Direction.EAST;
        } else if (to.getX() < from.getX()) {
            return Direction.WEST;
        } else if (to.getY() > from.getY()) {
            return Direction.SOUTH;
        } else if (to.getY() < from.getY()) {
            return Direction.NORTH;
        }
        throw new IllegalArgumentException("Positions are not adjacent");
    }
}
