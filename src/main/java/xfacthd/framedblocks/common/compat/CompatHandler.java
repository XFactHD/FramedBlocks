package xfacthd.framedblocks.common.compat;

import net.neoforged.bus.api.IEventBus;
import xfacthd.framedblocks.common.compat.athena.AthenaCompat;
import xfacthd.framedblocks.common.compat.atlasviewer.AtlasViewerCompat;
import xfacthd.framedblocks.common.compat.buildinggadgets.BuildingGadgetsCompat;
import xfacthd.framedblocks.common.compat.create.CreateCompat;
import xfacthd.framedblocks.common.compat.ctm.CtmCompat;
import xfacthd.framedblocks.common.compat.emi.EmiCompat;
import xfacthd.framedblocks.common.compat.flywheel.FlywheelCompat;
import xfacthd.framedblocks.common.compat.jei.JeiCompat;
import xfacthd.framedblocks.common.compat.nocubes.NoCubesCompat;
import xfacthd.framedblocks.common.compat.rei.ReiCompat;
import xfacthd.framedblocks.common.compat.supplementaries.SupplementariesCompat;
import xfacthd.framedblocks.common.compat.xycraft.XyCraftCompat;

public final class CompatHandler
{
    public static void init(IEventBus modBus)
    {
        AthenaCompat.init();
        AtlasViewerCompat.init(modBus);
        BuildingGadgetsCompat.init(modBus);
        CreateCompat.init();
        CtmCompat.init();
        EmiCompat.init();
        FlywheelCompat.init();
        JeiCompat.init();
        NoCubesCompat.init();
        ReiCompat.init();
        SupplementariesCompat.init();
        XyCraftCompat.init();
    }

    public static void commonSetup()
    {
        CreateCompat.commonSetup();
    }



    private CompatHandler() { }
}
