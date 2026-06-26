package com.mazerunner.map;

import java.util.*;

public class MapMemory {
    private final Map<Position, Tile> tiles = new HashMap<>();
    private final Position startPosition;
    private Position goalPosition;
    private Position currentPosition;

    public MapMemory(Position startPosition) {
        this.startPosition = startPosition;
        this.currentPosition = startPosition;
        // Start position er kjent fra starten
        this.tiles.put(startPosition, Tile.START);
    }

    public void updateTile(Position position, Tile tile) {
        this.tiles.put(position, tile);
        if (tile == Tile.GOAL) {
            this.goalPosition = position;
        }
    }

    public void updateCurrentPosition(Position position) {
        this.currentPosition = position;
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public Position getGoalPosition() {
        return goalPosition;
    }

    public Tile getTile(Position position) {
        return tiles.getOrDefault(position, Tile.UNKNOWN);
    }

    public boolean isVisited(Position position) {
        return tiles.containsKey(position);
    }

    public boolean isWall(Position position) {
        return getTile(position) == Tile.WALL;
    }

    public boolean isWalkable(Position position) {
        Tile tile = getTile(position);
        return tile.isWalkable();
    }

    public Set<Position> getUnknownNeighbors(Position position) {
        Set<Position> unknownNeighbors = new HashSet<>();
        for (Direction dir : Direction.values()) {
            Position neighbor = position.move(dir);
            if (getTile(neighbor) == Tile.UNKNOWN) {
                unknownNeighbors.add(neighbor);
            }
        }
        return unknownNeighbors;
    }

    public Set<Position> getWalkableNeighbors(Position position) {
        Set<Position> walkableNeighbors = new HashSet<>();
        for (Direction dir : Direction.values()) {
            Position neighbor = position.move(dir);
            if (isWalkable(neighbor)) {
                walkableNeighbors.add(neighbor);
            }
        }
        return walkableNeighbors;
    }

    public Set<Position> getFrontiers() {
        Set<Position> frontiers = new HashSet<>();
        for (Position pos : tiles.keySet()) {
            if (isWalkable(pos) && !getUnknownNeighbors(pos).isEmpty()) {
                frontiers.add(pos);
            }
        }
        return frontiers;
    }

    public Map<Position, Tile> getAllTiles() {
        return new HashMap<>(tiles);
    }

    public boolean isGoalDiscovered() {
        return goalPosition != null;
    }

    public boolean hasVisitedAny() {
        return tiles.size() > 1 || !tiles.get(startPosition).equals(Tile.START);
    }
}
