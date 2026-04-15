package io.github.xfacthd.framedblocks.common.compat.jei;

import io.github.xfacthd.framedblocks.client.screen.CamoApplicatorScreen;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rect2i;

import java.util.List;

public final class CamoApplicatorGuiContainerHandler implements IGuiContainerHandler<CamoApplicatorScreen> {
    @Override
    public List<Rect2i> getGuiExtraAreas(CamoApplicatorScreen screen) {
        boolean open = screen.isConfigOpen();
        return List.of(new Rect2i(
                screen.getLeftPos() + CamoApplicatorScreen.CFG_TAB_X,
                screen.getTopPos() + CamoApplicatorScreen.CFG_TAB_Y,
                open ? CamoApplicatorScreen.CFG_TAB_WIDTH_OPEN : CamoApplicatorScreen.CFG_TAB_WIDTH_CLOSED,
                open ? CamoApplicatorScreen.CFG_TAB_HEIGHT_OPEN : CamoApplicatorScreen.CFG_TAB_HEIGHT_CLOSED
        ));
    }
}
