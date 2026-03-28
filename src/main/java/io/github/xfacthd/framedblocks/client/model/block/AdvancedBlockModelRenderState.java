package io.github.xfacthd.framedblocks.client.model.block;

import net.minecraft.client.renderer.block.BlockModelRenderState;

public final class AdvancedBlockModelRenderState extends BlockModelRenderState {
    private boolean animated;

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    public boolean isAnimated() {
        return animated;
    }

    @Override
    public void clear() {
        super.clear();
        animated = false;
    }
}
