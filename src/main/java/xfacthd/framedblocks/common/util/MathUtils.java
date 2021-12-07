package xfacthd.framedblocks.common.util;

import net.minecraft.util.math.vector.Vector3d;

public class MathUtils
{
    public static final float EPSILON = 1.0E-5F;

    public static Vector3d wrapVector(Vector3d vec, double min, double max)
    {
        return new Vector3d(
                wrap(vec.getX(), min, max),
                wrap(vec.getY(), min, max),
                wrap(vec.getZ(), min, max)
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
}