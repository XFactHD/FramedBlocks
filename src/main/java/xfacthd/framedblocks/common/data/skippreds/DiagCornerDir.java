package xfacthd.framedblocks.common.data.skippreds;

/**
 * Indicates the normal and two outer edges of each section of a face with two diagonally located quarter-block sized sections
 */
public enum DiagCornerDir
{
    NULL(null, null),

    UP_NE_SW(CornerDir.UP_NORTH_EAST, CornerDir.UP_SOUTH_WEST),
    UP_NW_SE(CornerDir.UP_NORTH_WEST, CornerDir.UP_SOUTH_EAST),

    DOWN_NE_SW(CornerDir.DOWN_NORTH_EAST, CornerDir.DOWN_SOUTH_WEST),
    DOWN_NW_SE(CornerDir.DOWN_NORTH_WEST, CornerDir.DOWN_SOUTH_EAST),

    NORTH_UE_DW(CornerDir.NORTH_UP_EAST, CornerDir.NORTH_DOWN_WEST),
    NORTH_UW_DE(CornerDir.NORTH_UP_WEST, CornerDir.NORTH_DOWN_EAST),

    SOUTH_UE_DW(CornerDir.SOUTH_UP_EAST, CornerDir.SOUTH_DOWN_WEST),
    SOUTH_UW_DE(CornerDir.SOUTH_UP_WEST, CornerDir.SOUTH_DOWN_EAST),

    EAST_UN_DS(CornerDir.EAST_UP_NORTH, CornerDir.EAST_DOWN_SOUTH),
    EAST_US_DN(CornerDir.EAST_UP_SOUTH, CornerDir.EAST_DOWN_NORTH),

    WEST_UN_DS(CornerDir.WEST_UP_NORTH, CornerDir.WEST_DOWN_SOUTH),
    WEST_US_DN(CornerDir.WEST_UP_SOUTH, CornerDir.WEST_DOWN_NORTH);

    private final CornerDir dirOne;
    private final CornerDir dirTwo;

    DiagCornerDir(CornerDir dirOne, CornerDir dirTwo)
    {
        this.dirOne = dirOne;
        this.dirTwo = dirTwo;
    }

    /**
     * {@return true if both {@code DiagCornerDir}s are not {@link DiagCornerDir#NULL} and the other {@code DiagCornerDir}
     * has the same edge directions and the opposite normal direction}
     */
    public boolean isEqualTo(DiagCornerDir other)
    {
        return this != NULL && dirOne.isEqualTo(other.dirOne) && dirTwo.isEqualTo(other.dirTwo);
    }
}
