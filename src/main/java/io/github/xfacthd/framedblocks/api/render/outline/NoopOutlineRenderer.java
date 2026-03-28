package io.github.xfacthd.framedblocks.api.render.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.block.state.BlockState;

final class NoopOutlineRenderer implements SimpleOutlineRenderer {
    @Override
    public void draw(BlockState state, LineDrawer drawer) {
        //NO-OP
    }

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state) {
        // NO-OP
    }
}
