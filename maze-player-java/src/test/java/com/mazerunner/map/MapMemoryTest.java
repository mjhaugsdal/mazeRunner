package com.mazerunner.map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MapMemoryTest {

    private MapMemory mapMemory;

    @BeforeEach
    void setUp() {
        mapMemory = new MapMemory(new Position(0, 0));
    }

    @Test
    void testStartPositionInitialization() {
        assertEquals(new Position(0, 0), mapMemory.getStartPosition());
        assertEquals(new Position(0, 0), mapMemory.getCurrentPosition());
        assertEquals(Tile.START, mapMemory.getTile(new Position(0, 0)));
    }

    @Test
    void testUpdateTile() {
        Position pos = new Position(1, 1);
        mapMemory.updateTile(pos, Tile.FLOOR);
        assertEquals(Tile.FLOOR, mapMemory.getTile(pos));
    }

    @Test
    void testGoalDiscovery() {
        assertFalse(mapMemory.isGoalDiscovered());
        Position goalPos = new Position(5, 5);
        mapMemory.updateTile(goalPos, Tile.GOAL);
        assertTrue(mapMemory.isGoalDiscovered());
        assertEquals(goalPos, mapMemory.getGoalPosition());
    }

    @Test
    void testWallDetection() {
        Position wall = new Position(1, 0);
        mapMemory.updateTile(wall, Tile.WALL);
        assertTrue(mapMemory.isWall(wall));
        assertFalse(mapMemory.isWalkable(wall));
    }

    @Test
    void testWalkableTiles() {
        mapMemory.updateTile(new Position(1, 0), Tile.FLOOR);
        mapMemory.updateTile(new Position(0, 1), Tile.START);
        mapMemory.updateTile(new Position(-1, 0), Tile.GOAL);
        mapMemory.updateTile(new Position(0, -1), Tile.WALL);

        assertTrue(mapMemory.isWalkable(new Position(1, 0)));
        assertTrue(mapMemory.isWalkable(new Position(0, 1)));
        assertTrue(mapMemory.isWalkable(new Position(-1, 0)));
        assertFalse(mapMemory.isWalkable(new Position(0, -1)));
    }

    @Test
    void testGetUnknownNeighbors() {
        mapMemory.updateTile(new Position(1, 0), Tile.FLOOR);
        mapMemory.updateTile(new Position(0, 1), Tile.FLOOR);
        mapMemory.updateTile(new Position(-1, 0), Tile.FLOOR);

        Set<Position> unknownNeighbors = mapMemory.getUnknownNeighbors(new Position(0, 0));
        assertEquals(1, unknownNeighbors.size());
        assertTrue(unknownNeighbors.contains(new Position(0, -1)));
    }

    @Test
    void testGetWalkableNeighbors() {
        mapMemory.updateTile(new Position(1, 0), Tile.FLOOR);
        mapMemory.updateTile(new Position(0, 1), Tile.WALL);
        mapMemory.updateTile(new Position(-1, 0), Tile.FLOOR);

        Set<Position> walkable = mapMemory.getWalkableNeighbors(new Position(0, 0));
        assertEquals(2, walkable.size());
        assertTrue(walkable.contains(new Position(1, 0)));
        assertTrue(walkable.contains(new Position(-1, 0)));
        assertFalse(walkable.contains(new Position(0, 1)));
    }

    @Test
    void testFrontier() {
        // Create a small explored area
        mapMemory.updateTile(new Position(1, 0), Tile.FLOOR);
        mapMemory.updateTile(new Position(0, 1), Tile.FLOOR);
        mapMemory.updateTile(new Position(2, 0), Tile.WALL);

        Set<Position> frontiers = mapMemory.getFrontiers();
        assertFalse(frontiers.isEmpty());
        // Frontier should be a walkable position next to unknown
        assertTrue(frontiers.contains(new Position(1, 0)) || frontiers.contains(new Position(0, 1)));
    }

    @Test
    void testIsVisited() {
        assertFalse(mapMemory.isVisited(new Position(1, 1)));
        mapMemory.updateTile(new Position(1, 1), Tile.FLOOR);
        assertTrue(mapMemory.isVisited(new Position(1, 1)));
    }

    @Test
    void testTileFromChar() {
        assertEquals(Tile.WALL, Tile.fromChar('#'));
        assertEquals(Tile.FLOOR, Tile.fromChar(' '));
        assertEquals(Tile.FLOOR, Tile.fromChar('F'));
        assertEquals(Tile.START, Tile.fromChar('S'));
        assertEquals(Tile.GOAL, Tile.fromChar('E'));
    }
}
