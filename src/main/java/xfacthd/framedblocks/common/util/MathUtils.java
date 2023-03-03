package xfacthd.framedblocks.common.util;

import com.google.common.math.IntMath;
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

    public static long lcm(int a, int b)
    {
        return (long) a * (long) (b / IntMath.gcd(a, b));
    }



    private MathUtils() { }
}