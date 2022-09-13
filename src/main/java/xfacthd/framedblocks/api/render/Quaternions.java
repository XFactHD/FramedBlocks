package xfacthd.framedblocks.api.render;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

public final class Quaternions
{
    public static final Quaternion XP_90 = Vector3f.XP.rotationDegrees(90);
    public static final Quaternion XP_180 = Vector3f.XP.rotationDegrees(180);
    public static final Quaternion XN_90 = Vector3f.XN.rotationDegrees(90);

    public static final Quaternion YP_90 = Vector3f.YP.rotationDegrees(90);
    public static final Quaternion YN_90 = Vector3f.YN.rotationDegrees(90);

    public static final Quaternion ZP_90 = Vector3f.ZP.rotationDegrees(90);
    public static final Quaternion ZP_180 = Vector3f.ZP.rotationDegrees(180);
    public static final Quaternion ZN_90 = Vector3f.ZN.rotationDegrees(90);



    private Quaternions() { }
}
