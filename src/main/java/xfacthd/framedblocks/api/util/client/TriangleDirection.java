package xfacthd.framedblocks.api.util.client;

@Deprecated(forRemoval = true, since = "1.19")
public enum TriangleDirection
{
    RIGHT,
    LEFT,
    UP,
    DOWN;

    public boolean isVertical() { return this == UP || this == DOWN; }
}