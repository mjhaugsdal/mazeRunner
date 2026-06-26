package com.mazerunner.pathfinding;

import com.mazerunner.map.Direction;
import com.mazerunner.map.MapMemory;
import com.mazerunner.map.Position;
import com.mazerunner.map.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PathFinderTest {

    private MapMemory mapMemory;
    private PathFinder pathFinder;

    @BeforeEach
    void setUp() {
        mapMemory = new MapMemory(new Position(0, 0));
        pathFinder = new PathFinder(mapMemory);
    }

    @Test
    void testSamePositionReturnsEmptyPath() {
        Position pos = new Position(0, 0);
        List<Direction> path = pathFinder.findShortestPath(pos, pos);
        assertTrue(path.isEmpty());
    }

    @Test
    void testSimpleOneStepPath() {
        mapMemory.updateTile(new Position(1, 0), Tile.FLOOR);

        List<Direction> path = pathFinder.findShortestPath(new Position(0, 0), new Position(1, 0));

        assertEquals(1, path.size());
        assertEquals(Direction.EAST, path.get(0));
    }

    @Test
    void testMultiStepPath() {
        // Create a path: start -> east -> south -> goal
        mapMemory.updateTile(new Position(1, 0), Tile.FLOOR);
        mapMemory.updateTile(new Position(1, 1), Tile.FLOOR);

        List<Direction> path = pathFinder.findShortestPath(new Position(0, 0), new Position(1, 1));

        assertEquals(2, path.size());
        assertTrue(
                (path.get(0) == Direction.EAST && path.get(1) == Direction.SOUTH) ||
                (path.get(0) == Direction.SOUTH && path.get(1) == Direction.EAST)
        );
    }

    @Test
    void testPathAroundWall() {
        // Create a map:
        // S # .
        // . # .
        // . . G
        mapMemory.updateTile(new Position(0, -1), Tile.FLOOR);
        mapMemory.updateTile(new Position(1, -1), Tile.WALL);
        mapMemory.updateTile(new Position(2, -1), Tile.FLOOR);

        mapMemory.updateTile(new Position(0, 0), Tile.FLOOR);
        mapMemory.updateTile(new Position(1, 0), Tile.WALL);
        mapMemory.updateTile(new Position(2, 0), Tile.FLOOR);

        mapMemory.updateTile(new Position(0, 1), Tile.FLOOR);
        mapMemory.updateTile(new Position(1, 1), Tile.FLOOR);
        mapMemory.updateTile(new Position(2, 1), Tile.FLOOR);

        List<Direction> path = pathFinder.findShortestPath(new Position(0, -1), new Position(2, 1));

        assertNotNull(path);
        assertFalse(path.isEmpty());
        assertTrue(path.size() <= 6); // Should be around 4-6 steps
    }

    @Test
    void testNoPathAvailable() {
        // Wall completely surrounds
        mapMemory.updateTile(new Position(1, 0), Tile.WALL);
        mapMemory.updateTile(new Position(-1, 0), Tile.WALL);
        mapMemory.updateTile(new Position(0, 1), Tile.WALL);
        mapMemory.updateTile(new Position(0, -1), Tile.WALL);

        List<Direction> path = pathFinder.findShortestPath(new Position(0, 0), new Position(10, 10));

        assertTrue(path.isEmpty());
    }

    @Test
    void testShortestPathWithMultipleOptions() {
        // Create two possible paths, verify shortest is chosen
        // Path 1: East -> East -> South
        // Path 2: South -> South -> East
        // Both are 3 steps, but we should get one of them

        for (int i = 1; i <= 2; i++) {
            mapMemory.updateTile(new Position(i, 0), Tile.FLOOR);
            mapMemory.updateTile(new Position(0, i), Tile.FLOOR);
            mapMemory.updateTile(new Position(i, 2), Tile.FLOOR);
            mapMemory.updateTile(new Position(2, i), Tile.FLOOR);
        }

        List<Direction> path = pathFinder.findShortestPath(new Position(0, 0), new Position(2, 2));

        assertEquals(4, path.size()); // 2 steps in one direction + 2 in another
    }
}
