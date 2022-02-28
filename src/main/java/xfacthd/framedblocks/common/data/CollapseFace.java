package xfacthd.framedblocks.common.data;

import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum CollapseFace implements IStringSerializable
{
    NONE(null),
    DOWN(Direction.DOWN),
    UP(Direction.UP),
    NORTH(Direction.NORTH),
    SOUTH(Direction.SOUTH),
    WEST(Direction.WEST),
    EAST(Direction.EAST);

    private final Direction dir;

    CollapseFace(Direction dir) { this.dir = dir; }

    public Direction toDirection() { return dir; }

    @Override
    public String getSerializedName() { return toString().toLowerCase(Locale.ROOT); }



    public static CollapseFace fromDirection(Direction dir)
    {
        if (dir == null) { return NONE; }

        switch (dir)
        {
            case DOWN: return DOWN;
            case UP: return UP;
            case NORTH: return NORTH;
            case SOUTH: return SOUTH;
            case WEST: return WEST;
            case EAST: return EAST;
        };
        throw new IllegalArgumentException("Invalid direction");
    }
}