package io.github.xfacthd.framedblocks.api.render;

import com.mojang.math.Axis;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

/// Holds quaternions for common rotations.
public final class Quaternions {
    /// Identity quaternion.
    public static final Quaternionfc ONE = new Quaternionf();

    /// Positive 90-degree rotation around the X axis.
    public static final Quaternionfc XP_90 = Axis.XP.rotationDegrees(90);
    /// 180-degree rotation around the X axis.
    public static final Quaternionfc XP_180 = Axis.XP.rotationDegrees(180);
    /// Negative 90-degree rotation around the X axis.
    public static final Quaternionfc XN_90 = Axis.XN.rotationDegrees(90);

    /// Positive 90-degree rotation around the Y axis.
    public static final Quaternionfc YP_90 = Axis.YP.rotationDegrees(90);
    /// 180-degree rotation around the Y axis.
    public static final Quaternionfc YP_180 = Axis.YP.rotationDegrees(180);
    /// Negative 90-degree rotation around the Y axis.
    public static final Quaternionfc YN_90 = Axis.YN.rotationDegrees(90);

    /// Positive 90-degree rotation around the Z axis.
    public static final Quaternionfc ZP_90 = Axis.ZP.rotationDegrees(90);
    /// 180-degree rotation around the Z axis.
    public static final Quaternionfc ZP_180 = Axis.ZP.rotationDegrees(180);
    /// Negative 90-degree rotation around the Z axis.
    public static final Quaternionfc ZN_90 = Axis.ZN.rotationDegrees(90);

    private Quaternions() { }
}
