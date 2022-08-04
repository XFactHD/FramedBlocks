package xfacthd.framedblocks.api.util.client;

@Deprecated
public enum TriangleDirection
{
    RIGHT,
    LEFT,
    UP,
    DOWN;

    public boolean isVertical() { return this == UP || this == DOWN; }
}