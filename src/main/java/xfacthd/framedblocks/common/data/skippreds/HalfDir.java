package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.Direction;

/**
 * Indicates the normal and outer edge of a half-block sized face
 */
public enum HalfDir
{
    NULL(null, null),

    UP_NORTH(Direction.UP, Direction.NORTH),
    UP_EAST (Direction.UP, Direction.EAST),
    UP_SOUTH(Direction.UP, Direction.SOUTH),
    UP_WEST (Direction.UP, Direction.WEST),

    DOWN_NORTH(Direction.DOWN, Direction.NORTH),
    DOWN_EAST (Direction.DOWN, Direction.EAST),
    DOWN_SOUTH(Direction.DOWN, Direction.SOUTH),
    DOWN_WEST (Direction.DOWN, Direction.WEST),

    NORTH_UP  (Direction.NORTH, Direction.UP),
    NORTH_EAST(Direction.NORTH, Direction.EAST),
    NORTH_DOWN(Direction.NORTH, Direction.DOWN),
    NORTH_WEST(Direction.NORTH, Direction.WEST),

    SOUTH_UP  (Direction.SOUTH, Direction.UP),
    SOUTH_EAST(Direction.SOUTH, Direction.EAST),
    SOUTH_DOWN(Direction.SOUTH, Direction.DOWN),
    SOUTH_WEST(Direction.SOUTH, Direction.WEST),

    EAST_UP   (Direction.EAST, Direction.UP),
    EAST_NORTH(Direction.EAST, Direction.NORTH),
    EAST_DOWN (Direction.EAST, Direction.DOWN),
    EAST_SOUTH(Direction.EAST, Direction.SOUTH),

    WEST_UP   (Direction.WEST, Direction.UP),
    WEST_NORTH(Direction.WEST, Direction.NORTH),
    WEST_DOWN (Direction.WEST, Direction.DOWN),
    WEST_SOUTH(Direction.WEST, Direction.SOUTH);

    private static final HalfDir[][] FROM_DIRS = makeDirTable();

    private final Direction normal;
    private final Direction edge;

    HalfDir(Direction normal, Direction edge)
    {
        this.normal = normal;
        this.edge = edge;
    }

    public HalfDir getOppositeEdge()
    {
        if (this == NULL)
        {
            return this;
        }
        return fromDirections(normal, edge.getOpposite());
    }

    public HalfDir getOppositeNormal()
    {
        if (this == NULL)
        {
            return this;
        }
        return fromDirections(normal.getOpposite(), edge);
    }

    /**
     * {@return true if both {@code HalfDir}s are not {@link HalfDir#NULL} and the other {@code HalfDir}
     * has the same edge direction and the opposite normal direction}
     */
    public boolean isEqualTo(HalfDir other)
    {
        return this != NULL && getOppositeNormal() == other;
    }

    /**
     * {@return true if both {@code HalfDir}s are not {@link HalfDir#NULL} and the other {@code HalfDir}
     * has the same normal direction and the opposite edge direction}
     */
    public boolean isEqualToOppositeEdge(HalfDir other)
    {
        return this != NULL && getOppositeEdge() == other;
    }

    public static HalfDir fromDirections(Direction normal, Direction edge)
    {
        HalfDir dir = FROM_DIRS[normal.ordinal()][edge.ordinal()];
        if (dir == null)
        {
            throw new IllegalArgumentException("Invalid direction pair: normal:" + normal + ", edge: " + edge);
        }
        return dir;
    }



    private static HalfDir[][] makeDirTable()
    {
        HalfDir[][] table = new HalfDir[6][6];
        for (HalfDir dir : values())
        {
            if (dir == NULL) { continue; }

            table[dir.normal.ordinal()][dir.edge.ordinal()] = dir;
        }
        return table;
    }
}
