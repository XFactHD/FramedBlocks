package xfacthd.framedblocks.api.util;

import net.minecraft.core.Direction;

final class Lookups
{
    static final Direction.Axis[] PERP_AXIS = buildPerpAxisMapping();

    private static Direction.Axis[] buildPerpAxisMapping()
    {
        Direction.Axis[] mapping = new Direction.Axis[11];
        mapping[makePerpAxisIndex(Direction.Axis.X, Direction.Axis.Y)] = mapping[makePerpAxisIndex(Direction.Axis.Y, Direction.Axis.X)] = Direction.Axis.Z;
        mapping[makePerpAxisIndex(Direction.Axis.X, Direction.Axis.Z)] = mapping[makePerpAxisIndex(Direction.Axis.Z, Direction.Axis.X)] = Direction.Axis.Y;
        mapping[makePerpAxisIndex(Direction.Axis.Y, Direction.Axis.Z)] = mapping[makePerpAxisIndex(Direction.Axis.Z, Direction.Axis.Y)] = Direction.Axis.X;
        return mapping;
    }

    static int makePerpAxisIndex(Direction.Axis axis1, Direction.Axis axis2)
    {
        return axis1.ordinal() | (axis2.ordinal() << 2);
    }

    private Lookups() { }
}
