package xfacthd.framedblocks.api.util;

import net.minecraft.core.Direction;

final class Lookups
{
    static final Direction.Axis[] PERP_AXIS = buildPerpAxisMapping();
    static final Direction[] NORMALS = buildNormalMapping();

    static int makePerpAxisIndex(Direction.Axis axis1, Direction.Axis axis2)
    {
        return axis1.ordinal() | (axis2.ordinal() << 2);
    }

    static int makeNormalIndex(int x, int y, int z)
    {
        x = Math.clamp(x, -1, 1);
        y = Math.clamp(y, -1, 1);
        z = Math.clamp(z, -1, 1);
        return ((x & 0b11) | (y & 0b11) << 2 | (z & 0b11) << 4);
    }

    private static Direction.Axis[] buildPerpAxisMapping()
    {
        Direction.Axis[] mapping = new Direction.Axis[11];
        mapping[makePerpAxisIndex(Direction.Axis.X, Direction.Axis.Y)] = mapping[makePerpAxisIndex(Direction.Axis.Y, Direction.Axis.X)] = Direction.Axis.Z;
        mapping[makePerpAxisIndex(Direction.Axis.X, Direction.Axis.Z)] = mapping[makePerpAxisIndex(Direction.Axis.Z, Direction.Axis.X)] = Direction.Axis.Y;
        mapping[makePerpAxisIndex(Direction.Axis.Y, Direction.Axis.Z)] = mapping[makePerpAxisIndex(Direction.Axis.Z, Direction.Axis.Y)] = Direction.Axis.X;
        return mapping;
    }

    private static Direction[] buildNormalMapping()
    {
        Direction[] mapping = new Direction[64];
        for (Direction dir : Direction.values())
        {
            mapping[makeNormalIndex(dir.getStepX(), dir.getStepY(), dir.getStepZ())] = dir;
        }
        return mapping;
    }

    private Lookups() { }
}
