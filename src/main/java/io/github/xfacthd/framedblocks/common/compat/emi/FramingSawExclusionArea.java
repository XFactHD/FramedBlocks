package io.github.xfacthd.framedblocks.common.compat.emi;

import dev.emi.emi.api.EmiExclusionArea;
import dev.emi.emi.api.widget.Bounds;
import io.github.xfacthd.framedblocks.client.screen.FramingSawWithEncoderScreen;

import java.util.function.Consumer;

public final class FramingSawExclusionArea implements EmiExclusionArea<FramingSawWithEncoderScreen> {
    @Override
    public void addExclusionArea(FramingSawWithEncoderScreen screen, Consumer<Bounds> consumer) {
        if (screen.getMenu().isInEncoderMode()) {
            consumer.accept(new Bounds(
                    screen.getGuiLeft() + FramingSawWithEncoderScreen.TAB_X,
                    screen.getGuiTop() + FramingSawWithEncoderScreen.TAB_TOP_Y,
                    FramingSawWithEncoderScreen.TAB_WIDTH,
                    FramingSawWithEncoderScreen.TAB_HEIGHT * 2
            ));
        }
    }
}
