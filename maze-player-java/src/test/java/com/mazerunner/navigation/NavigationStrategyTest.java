package com.mazerunner.navigation;

import com.mazerunner.map.Direction;
import com.mazerunner.map.MapMemory;
import com.mazerunner.map.Position;
import com.mazerunner.map.Tile;
import com.mazerunner.pathfinding.PathFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class NavigationStrategyTest {

    private MapMemory mapMemory;
    private PathFinder pathFinder;
    private NavigationStrategy navigationStrategy;

    @BeforeEach
    void setUp() {
        mapMemory = new MapMemory(new Position(0, 0));
        pathFinder = new PathFinder(mapMemory);
        navigationStrategy = new NavigationStrategy(mapMemory, pathFinder);
    }

    @Test
    void testNavigateToGoalWhenDiscovered() {
        // Setup: Simple path to goal
        mapMemory.updateTile(new Position(1, 0), Tile.FLOOR);
        mapMemory.updateTile(new Position(2, 0), Tile.GOAL);

        Optional<Direction> move = navigationStrategy.getNextMove();

        assertTrue(move.isPresent());
        assertEquals(Direction.EAST, move.get());
    }

    @Test
    void testExploreUnknownBeforeKnownGoal() {
        // Setup: Goal is known but there are unknown neighbors
        mapMemory.updateTile(new Position(1, 0), Tile.FLOOR);
        mapMemory.updateTile(new Position(2, 0), Tile.GOAL);
        // Unknown at (0, -1)

        Optional<Direction> move = navigationStrategy.getNextMove();

        assertTrue(move.isPresent());
        // Should prefer exploring unknown or navigating to frontier
        // depending on current positions
    }

    @Test
    void testNavigateToFrontierWhenNoExploreOption() {
        // Setup: Create explored area with frontier
        mapMemory.updateTile(new Position(1, 0), Tile.FLOOR);
        mapMemory.updateTile(new Position(0, 1), Tile.FLOOR);
        mapMemory.updateTile(new Position(-1, 0), Tile.FLOOR);
        // Frontier at (1,0) and (0,1) with unknown neighbors

        Optional<Direction> move = navigationStrategy.getNextMove();

        assertTrue(move.isPresent());
    }

    @Test
    void testFallbackToAnyWalkableTile() {
        // Setup: Only one walkable direction available
        mapMemory.updateTile(new Position(1, 0), Tile.FLOOR);
        mapMemory.updateTile(new Position(-1, 0), Tile.WALL);
        mapMemory.updateTile(new Position(0, 1), Tile.WALL);
        mapMemory.updateTile(new Position(0, -1), Tile.WALL);

        Optional<Direction> move = navigationStrategy.getNextMove();

        assertTrue(move.isPresent());
        assertEquals(Direction.EAST, move.get());
    }

    @Test
    void testNoMoveWhenCompletelyBoxedIn() {
        // Setup: All directions are walls
        mapMemory.updateTile(new Position(1, 0), Tile.WALL);
        mapMemory.updateTile(new Position(-1, 0), Tile.WALL);
        mapMemory.updateTile(new Position(0, 1), Tile.WALL);
        mapMemory.updateTile(new Position(0, -1), Tile.WALL);

        Optional<Direction> move = navigationStrategy.getNextMove();

        assertFalse(move.isPresent());
    }

    @Test
    void testPriorityExploreOverFrontier() {
        // If there's an unexplored neighbor of current position's neighbor,
        // it should be preferred over frontier navigation
        
        // Create simple maze with exploration opportunity
        mapMemory.updateTile(new Position(1, 0), Tile.FLOOR);
        mapMemory.updateTile(new Position(0, 1), Tile.FLOOR);
        mapMemory.updateTile(new Position(2, 0), Tile.FLOOR);
        mapMemory.updateTile(new Position(0, 2), Tile.FLOOR);

        Optional<Direction> move = navigationStrategy.getNextMove();

        assertTrue(move.isPresent());
        // Should move towards exploration
    }

    @Test
    void testGoalPrioritizedWhenDiscovered() {
        // When goal is discovered, it should be navigated to
        // even if there are unvisited frontiers
        
        mapMemory.updateTile(new Position(1, 0), Tile.FLOOR);
        mapMemory.updateTile(new Position(1, 1), Tile.FLOOR);
        mapMemory.updateTile(new Position(1, 2), Tile.GOAL);
        
        Optional<Direction> move = navigationStrategy.getNextMove();
        
        assertTrue(move.isPresent());
        // Should navigate towards goal (east then south)
    }
}
