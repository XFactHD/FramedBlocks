package io.github.xfacthd.framedblocks.api.util;

public record Rect(int x0, int y0, int width, int height) {
    public static final Rect EMPTY = new Rect(0, 0, 0, 0);

    public int x1() {
        return x0 + width;
    }

    public int y1() {
        return y0 + height;
    }

    public boolean contains(int x, int y) {
        return x >= x0 && x <= x1() && y >= y0 && y <= y1();
    }
}
