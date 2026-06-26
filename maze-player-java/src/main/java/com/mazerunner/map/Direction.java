package com.mazerunner.map;

public enum Direction {
    NORTH, SOUTH, EAST, WEST;

    public Direction opposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
        };
    }

    public static Direction fromString(String value) {
        return Direction.valueOf(value.toUpperCase());
    }
}
