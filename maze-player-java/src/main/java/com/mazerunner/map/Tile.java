package com.mazerunner.map;

public enum Tile {
    UNKNOWN,  // Ikke oppdaget
    WALL,     // Vegg (#)
    FLOOR,    // Gangbart felt (' ' eller 'F')
    START,    // Start (S)
    GOAL;     // Utgang (E)

    public static Tile fromChar(char c) {
        return switch (c) {
            case '#' -> WALL;
            case ' ', 'F' -> FLOOR;
            case 'S' -> START;
            case 'E' -> GOAL;
            default -> UNKNOWN;
        };
    }

    public static Tile fromApiString(String s) {
        return switch (s.toUpperCase()) {
            case "WALL" -> WALL;
            case "FLOOR" -> FLOOR;
            case "START" -> START;
            case "GOAL" -> GOAL;
            default -> UNKNOWN;
        };
    }

    public boolean isWalkable() {
        return this == FLOOR || this == START || this == GOAL;
    }
}
