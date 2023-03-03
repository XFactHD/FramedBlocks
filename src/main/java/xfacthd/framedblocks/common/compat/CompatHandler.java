package xfacthd.framedblocks.common.compat;

import xfacthd.framedblocks.common.compat.buildinggadgets.BuildingGadgetsCompat;
import xfacthd.framedblocks.common.compat.create.CreateCompat;
import xfacthd.framedblocks.common.compat.ctm.CtmCompat;
import xfacthd.framedblocks.common.compat.flywheel.FlywheelCompat;
import xfacthd.framedblocks.common.compat.jei.JeiCompat;
import xfacthd.framedblocks.common.compat.nocubes.NoCubesCompat;
import xfacthd.framedblocks.common.compat.rubidium.RubidiumCompat;
import xfacthd.framedblocks.common.compat.supplementaries.SupplementariesCompat;

public final class CompatHandler
{
    public static void init()
    {
        BuildingGadgetsCompat.init();
        CreateCompat.init();
        //CtmCompat.init(); //TODO: add proper support for CTM's CT context
        FlywheelCompat.init();
        JeiCompat.init();
        NoCubesCompat.init();
        RubidiumCompat.init();
        SupplementariesCompat.init();
    }

    public static void commonSetup()
    {
        CreateCompat.commonSetup();
    }



    private CompatHandler() { }
}
