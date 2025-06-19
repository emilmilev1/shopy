package org.example.model;

public record Point(int x, int y) {
    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
