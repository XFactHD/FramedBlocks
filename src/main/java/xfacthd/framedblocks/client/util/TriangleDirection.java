package xfacthd.framedblocks.client.util;

public enum TriangleDirection
{
    RIGHT,
    LEFT,
    UP,
    DOWN;

    public boolean isVertical() { return this == UP || this == DOWN; }
}