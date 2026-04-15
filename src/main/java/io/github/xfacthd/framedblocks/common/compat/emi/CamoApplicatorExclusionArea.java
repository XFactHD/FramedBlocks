package io.github.xfacthd.framedblocks.common.compat.emi;

import dev.emi.emi.api.EmiExclusionArea;
import dev.emi.emi.api.widget.Bounds;
import io.github.xfacthd.framedblocks.client.screen.CamoApplicatorScreen;

import java.util.function.Consumer;

public final class CamoApplicatorExclusionArea implements EmiExclusionArea<CamoApplicatorScreen> {
    @Override
    public void addExclusionArea(CamoApplicatorScreen screen, Consumer<Bounds> consumer) {
        boolean open = screen.isConfigOpen();
        consumer.accept(new Bounds(
                screen.getLeftPos() + CamoApplicatorScreen.CFG_TAB_X,
                screen.getTopPos() + CamoApplicatorScreen.CFG_TAB_Y,
                open ? CamoApplicatorScreen.CFG_TAB_WIDTH_OPEN : CamoApplicatorScreen.CFG_TAB_WIDTH_CLOSED,
                open ? CamoApplicatorScreen.CFG_TAB_HEIGHT_OPEN : CamoApplicatorScreen.CFG_TAB_HEIGHT_CLOSED
        ));
    }
}
