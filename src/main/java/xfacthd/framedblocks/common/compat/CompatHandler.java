package xfacthd.framedblocks.common.compat;

import net.neoforged.bus.api.IEventBus;
import xfacthd.framedblocks.common.compat.ae2.AppliedEnergisticsCompat;
import xfacthd.framedblocks.common.compat.athena.AthenaCompat;
import xfacthd.framedblocks.common.compat.atlasviewer.AtlasViewerCompat;
import xfacthd.framedblocks.common.compat.buildinggadgets.BuildingGadgetsCompat;
import xfacthd.framedblocks.common.compat.create.CreateCompat;
import xfacthd.framedblocks.common.compat.diagonalblocks.DiagonalBlocksCompat;
import xfacthd.framedblocks.common.compat.supplementaries.SupplementariesCompat;

public final class CompatHandler
{
    public static void init(IEventBus modBus)
    {
        AppliedEnergisticsCompat.init(modBus);
        AthenaCompat.init();
        AtlasViewerCompat.init(modBus);
        BuildingGadgetsCompat.init(modBus);
        CreateCompat.init();
        DiagonalBlocksCompat.init(modBus);
        SupplementariesCompat.init();
    }

    public static void commonSetup()
    {
        CreateCompat.commonSetup();
    }



    private CompatHandler() { }
}
