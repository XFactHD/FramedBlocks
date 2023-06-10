package xfacthd.framedblocks.common.data.property;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import xfacthd.framedblocks.api.util.Utils;

import java.util.Locale;

public enum DirectionAxis implements StringRepresentable
{
    DOWN_X  (Direction.DOWN, Direction.Axis.X),
    DOWN_Z  (Direction.DOWN, Direction.Axis.Z),

    UP_X    (Direction.UP, Direction.Axis.X),
    UP_Z    (Direction.UP, Direction.Axis.Z),

    NORTH_X (Direction.NORTH, Direction.Axis.X),
    NORTH_Y (Direction.NORTH, Direction.Axis.Y),

    SOUTH_X (Direction.SOUTH, Direction.Axis.X),
    SOUTH_Y (Direction.SOUTH, Direction.Axis.Y),

    WEST_Y  (Direction.WEST, Direction.Axis.Y),
    WEST_Z  (Direction.WEST, Direction.Axis.Z),

    EAST_Y  (Direction.EAST, Direction.Axis.Y),
    EAST_Z  (Direction.EAST, Direction.Axis.Z);

    private static final DirectionAxis[][] FROM_DIR_AXIS = makeDirTable();

    private final String name = toString().toLowerCase(Locale.ROOT);
    private final Direction dir;
    private final Direction.Axis axis;

    DirectionAxis(Direction dir, Direction.Axis axis)
    {
        this.dir = dir;
        this.axis = axis;
    }

    public Direction direction()
    {
        return dir;
    }

    public Direction.Axis axis()
    {
        return axis;
    }

    public DirectionAxis rotate(Rotation rot)
    {
        if (rot == Rotation.NONE)
        {
            return this;
        }

        if (Utils.isY(dir))
        {
            if (rot == Rotation.CLOCKWISE_180)
            {
                return this;
            }

            return of(dir, Utils.nextAxisNotEqualTo(axis, dir.getAxis()));
        }
        else
        {
            Direction.Axis newAxis = axis;
            if (axis != Direction.Axis.Y)
            {
                newAxis = Utils.nextAxisNotEqualTo(axis, Direction.Axis.Y);
            }
            return of(rot.rotate(dir), newAxis);
        }
    }

    public DirectionAxis mirror(Mirror mirror)
    {
        return switch (mirror)
        {
            case NONE -> this;
            case FRONT_BACK -> Utils.isX(dir) ? of(dir.getOpposite(), axis) : this;
            case LEFT_RIGHT -> Utils.isZ(dir) ? of(dir.getOpposite(), axis) : this;
        };
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }



    public static DirectionAxis of(Direction dir, Direction.Axis axis)
    {
        DirectionAxis dirAxis = FROM_DIR_AXIS[dir.ordinal()][axis.ordinal()];
        if (dirAxis == null)
        {
            throw new IllegalArgumentException("Invalid dir/axis pair! Direction: " + dir + ", Axis: " + axis);
        }
        return dirAxis;
    }

    private static DirectionAxis[][] makeDirTable()
    {
        DirectionAxis[][] table = new DirectionAxis[6][3];
        for (DirectionAxis dirAxis : values())
        {
            Direction dir = dirAxis.dir;
            Direction.Axis axis = dirAxis.axis;
            table[dir.ordinal()][axis.ordinal()] = dirAxis;
        }
        return table;
    }
}
