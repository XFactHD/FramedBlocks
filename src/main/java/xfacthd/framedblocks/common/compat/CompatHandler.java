package xfacthd.framedblocks.common.compat;

import xfacthd.framedblocks.common.compat.buildinggadgets.BuildingGadgetsCompat;
import xfacthd.framedblocks.common.compat.create.CreateCompat;
import xfacthd.framedblocks.common.compat.flywheel.FlywheelCompat;
import xfacthd.framedblocks.common.compat.nocubes.NoCubesCompat;
import xfacthd.framedblocks.common.compat.supplementaries.SupplementariesCompat;

public final class CompatHandler
{
    public static void init()
    {
        BuildingGadgetsCompat.init();
        FlywheelCompat.init();
        SupplementariesCompat.init();
        NoCubesCompat.init();
    }

    public static void commonSetup()
    {
        CreateCompat.init();
    }



    private CompatHandler() { }
}
