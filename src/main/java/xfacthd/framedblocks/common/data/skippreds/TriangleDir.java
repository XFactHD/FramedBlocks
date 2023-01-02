package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.Direction;

import java.util.Objects;

/**
 * Indicates in which direction the corner above the baseline points
 */
public enum TriangleDir
{
    NULL(null, null),

    NORTH_EAST(Direction.NORTH, Direction.EAST),
    NORTH_WEST(Direction.NORTH, Direction.WEST),
    SOUTH_EAST(Direction.SOUTH, Direction.EAST),
    SOUTH_WEST(Direction.SOUTH, Direction.WEST),

    UP_NORTH  (Direction.UP, Direction.NORTH),
    UP_SOUTH  (Direction.UP, Direction.SOUTH),
    DOWN_NORTH(Direction.DOWN, Direction.NORTH),
    DOWN_SOUTH(Direction.DOWN, Direction.SOUTH),

    UP_EAST  (Direction.UP, Direction.EAST),
    UP_WEST  (Direction.UP, Direction.WEST),
    DOWN_EAST(Direction.DOWN, Direction.EAST),
    DOWN_WEST(Direction.DOWN, Direction.WEST);

    private static final TriangleDir[][] FROM_DIRS = makeDirTable();

    private final Direction cardOne;
    private final Direction cardTwo;

    TriangleDir(Direction cardOne, Direction cardTwo)
    {
        this.cardOne = cardOne;
        this.cardTwo = cardTwo;
    }

    public TriangleDir getOpposite()
    {
        if (this == NULL)
        {
            return this;
        }
        return fromDirections(cardOne.getOpposite(), cardTwo.getOpposite());
    }

    /**
     * {@return true if both {@code TriangleDir}s are not {@link TriangleDir#NULL} and the other
     * {@code TriangleDir} has the same edge directions}
     */
    public boolean isEqualTo(TriangleDir other)
    {
        return this != NULL && this == other;
    }

    public static TriangleDir fromDirections(Direction dirOne, Direction dirTwo)
    {
        TriangleDir dir = FROM_DIRS[dirOne.ordinal()][dirTwo.ordinal()];
        return Objects.requireNonNull(dir, "Invalid direction pair!");
    }



    private static TriangleDir[][] makeDirTable()
    {
        TriangleDir[][] table = new TriangleDir[6][6];
        for (TriangleDir dir : values())
        {
            if (dir == NULL) { continue; }

            table[dir.cardOne.ordinal()][dir.cardTwo.ordinal()] = dir;
            table[dir.cardTwo.ordinal()][dir.cardOne.ordinal()] = dir;
        }
        return table;
    }
}
