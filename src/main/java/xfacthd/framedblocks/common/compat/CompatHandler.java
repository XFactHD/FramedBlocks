package xfacthd.framedblocks.common.compat;

import xfacthd.framedblocks.common.compat.athena.AthenaCompat;
import xfacthd.framedblocks.common.compat.atlasviewer.AtlasViewerCompat;
import xfacthd.framedblocks.common.compat.buildinggadgets.BuildingGadgetsCompat;
import xfacthd.framedblocks.common.compat.create.CreateCompat;
import xfacthd.framedblocks.common.compat.ctm.CtmCompat;
import xfacthd.framedblocks.common.compat.emi.EmiCompat;
import xfacthd.framedblocks.common.compat.flywheel.FlywheelCompat;
import xfacthd.framedblocks.common.compat.jei.JeiCompat;
import xfacthd.framedblocks.common.compat.modernfix.ModernFixCompat;
import xfacthd.framedblocks.common.compat.nocubes.NoCubesCompat;
import xfacthd.framedblocks.common.compat.rei.ReiCompat;
import xfacthd.framedblocks.common.compat.starlight.StarlightCompat;
import xfacthd.framedblocks.common.compat.supplementaries.SupplementariesCompat;
import xfacthd.framedblocks.common.compat.xycraft.XyCraftCompat;

public final class CompatHandler
{
    public static void init()
    {
        AthenaCompat.init();
        AtlasViewerCompat.init();
        BuildingGadgetsCompat.init();
        CreateCompat.init();
        CtmCompat.init();
        EmiCompat.init();
        FlywheelCompat.init();
        JeiCompat.init();
        ModernFixCompat.init();
        NoCubesCompat.init();
        ReiCompat.init();
        StarlightCompat.init();
        SupplementariesCompat.init();
        XyCraftCompat.init();
    }

    public static void commonSetup()
    {
        CreateCompat.commonSetup();
    }



    private CompatHandler() { }
}
