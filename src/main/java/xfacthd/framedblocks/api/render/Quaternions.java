package xfacthd.framedblocks.api.render;

import com.mojang.math.Axis;
import org.joml.Quaternionf;

public final class Quaternions
{
    public static final Quaternionf ONE = new Quaternionf();

    public static final Quaternionf XP_90 = Axis.XP.rotationDegrees(90);
    public static final Quaternionf XP_180 = Axis.XP.rotationDegrees(180);
    public static final Quaternionf XN_90 = Axis.XN.rotationDegrees(90);

    public static final Quaternionf YP_90 = Axis.YP.rotationDegrees(90);
    public static final Quaternionf YN_90 = Axis.YN.rotationDegrees(90);

    public static final Quaternionf ZP_90 = Axis.ZP.rotationDegrees(90);
    public static final Quaternionf ZP_180 = Axis.ZP.rotationDegrees(180);
    public static final Quaternionf ZN_90 = Axis.ZN.rotationDegrees(90);



    private Quaternions() { }
}
