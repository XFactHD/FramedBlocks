package io.github.xfacthd.framedblocks.client.screen.widget;

import io.github.xfacthd.framedblocks.client.screen.pip.SpinningItemPictureInPictureRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public final class BlockPreviewTooltipComponent implements TooltipComponent, ClientTooltipComponent {
    private static final int SIZE = 36;
    private static final float STACK_SCALE = 48;

    private final TrackingItemStackRenderState renderState;

    public BlockPreviewTooltipComponent(TrackingItemStackRenderState renderState) {
        this.renderState = renderState;
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        graphics.submitPictureInPictureRenderState(new SpinningItemPictureInPictureRenderer.RenderState(
                renderState,
                (int) (System.currentTimeMillis() / 20 % 360),
                x, y, x + SIZE, y + SIZE, STACK_SCALE, graphics.peekScissorStack()
        ));
    }

    @Override
    public int getWidth(Font font) {
        return SIZE;
    }

    @Override
    public int getHeight(Font font) {
        return SIZE;
    }
}
