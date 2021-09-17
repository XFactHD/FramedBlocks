package xfacthd.framedblocks.api.util.client;

public enum TriangleDirection
{
    RIGHT,
    LEFT,
    UP,
    DOWN;

    public boolean isVertical() { return this == UP || this == DOWN; }
}