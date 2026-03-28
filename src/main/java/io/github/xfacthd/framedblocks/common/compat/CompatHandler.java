package io.github.xfacthd.framedblocks.common.compat;

import io.github.xfacthd.framedblocks.common.compat.additionalplacements.AdditionalPlacementsCompat;
import io.github.xfacthd.framedblocks.common.compat.ae2.AppliedEnergisticsCompat;
import io.github.xfacthd.framedblocks.common.compat.amendments.AmendmentsCompat;
import io.github.xfacthd.framedblocks.common.compat.atlasviewer.AtlasViewerCompat;
import io.github.xfacthd.framedblocks.common.compat.buildinggadgets.BuildingGadgetsCompat;
import io.github.xfacthd.framedblocks.common.compat.create.CreateCompat;
import io.github.xfacthd.framedblocks.common.compat.diagonalblocks.DiagonalBlocksCompat;
import io.github.xfacthd.framedblocks.common.compat.jei.JeiCompat;
import io.github.xfacthd.framedblocks.common.compat.searchables.SearchablesCompat;
import net.neoforged.bus.api.IEventBus;

public final class CompatHandler {
    public static void init(IEventBus modBus) {
        AdditionalPlacementsCompat.init();
        AppliedEnergisticsCompat.init(modBus);
        AmendmentsCompat.init();
        AtlasViewerCompat.init(modBus);
        BuildingGadgetsCompat.init(modBus);
        DiagonalBlocksCompat.init(modBus);
        JeiCompat.init();
        SearchablesCompat.init();
    }

    public static void commonSetup() {
        CreateCompat.commonSetup();
    }

    private CompatHandler() { }
}
