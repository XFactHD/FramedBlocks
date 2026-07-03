package io.github.xfacthd.framedblocks.api.util;

/// Represents a rectangle at a given position with a given size.
///
/// @param x0     The min X coordinate of the rectangle
/// @param y0     The min Y coordinate of the rectangle
/// @param width  The width of the rectangle
/// @param height The height of the rectangle
public record Rect(int x0, int y0, int width, int height) {
    /// A rectangle with zero size.
    public static final Rect EMPTY = new Rect(0, 0, 0, 0);

    /// {@return the max X coordinate of this rectangle}
    public int x1() {
        return x0 + width;
    }

    /// {@return the max Y coordinate of this rectangle}
    public int y1() {
        return y0 + height;
    }

    /// {@return whether this rectangle contains the given point}
    ///
    /// @param x The X coordinate of the point
    /// @param y The Y coordinate of the point
    public boolean contains(int x, int y) {
        return x >= x0 && x <= x1() && y >= y0 && y <= y1();
    }
}
