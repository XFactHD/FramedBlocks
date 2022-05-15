package xfacthd.framedblocks.common.util;

import net.minecraft.world.phys.Vec3;

public final class MathUtils
{
    public static Vec3 wrapVector(Vec3 vec, double min, double max)
    {
        return new Vec3(
                wrap(vec.x(), min, max),
                wrap(vec.y(), min, max),
                wrap(vec.z(), min, max)
        );
    }

    public static double wrap(double val, double min, double max)
    {
        double dist = max - min;
        if (val > max)
        {
            return val - dist;
        }
        else if (val < min)
        {
            return val + dist;
        }
        else
        {
            return val;
        }
    }



    private MathUtils() { }
}