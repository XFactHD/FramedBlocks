package io.github.xfacthd.framedblocks.common.compat.jei;

import io.github.xfacthd.framedblocks.client.screen.FramingSawWithEncoderScreen;
import net.minecraft.client.renderer.Rect2i;

import java.util.List;

public final class FramingSawWithEncoderGuiContainerHandler extends FramingSawGuiContainerHandler<FramingSawWithEncoderScreen> {
    @Override
    public List<Rect2i> getGuiExtraAreas(FramingSawWithEncoderScreen screen) {
        if (screen.getMenu().isInEncoderMode()) {
            return List.of(new Rect2i(
                    screen.getLeftPos() + FramingSawWithEncoderScreen.TAB_X,
                    screen.getTopPos() + FramingSawWithEncoderScreen.TAB_TOP_Y,
                    FramingSawWithEncoderScreen.TAB_WIDTH,
                    FramingSawWithEncoderScreen.TAB_HEIGHT * 2
            ));
        }
        return List.of();
    }
}
