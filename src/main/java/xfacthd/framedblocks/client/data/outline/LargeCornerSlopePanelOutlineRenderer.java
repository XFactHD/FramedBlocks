package xfacthd.framedblocks.client.data.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.render.OutlineRenderer;

public final class LargeCornerSlopePanelOutlineRenderer implements OutlineRenderer
{
    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        // Base
        OutlineRenderer.drawLine(builder, poseStack,   0, 0,   0,   0, 0,   1);
        OutlineRenderer.drawLine(builder, poseStack,   0, 0,   0,   1, 0,   0);
        OutlineRenderer.drawLine(builder, poseStack, .5F, 0, .5F,   1, 0, .5F);
        OutlineRenderer.drawLine(builder, poseStack, .5F, 0, .5F, .5F, 0,   1);
        OutlineRenderer.drawLine(builder, poseStack,   1, 0,   0,   1, 0, .5F);
        OutlineRenderer.drawLine(builder, poseStack,   0, 0,   1, .5F, 0,   1);

        // Back vertical
        OutlineRenderer.drawLine(builder, poseStack, .5F, 0, .5F, .5F, 1, .5F);
        OutlineRenderer.drawLine(builder, poseStack, .5F, 0,   1, .5F, 1,   1);
        OutlineRenderer.drawLine(builder, poseStack,   1, 0, .5F,   1, 1, .5F);

        // Top
        OutlineRenderer.drawLine(builder, poseStack, .5F, 1, .5F,   1, 1, .5F);
        OutlineRenderer.drawLine(builder, poseStack, .5F, 1, .5F, .5F, 1,   1);

        // Slopes
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 0, .5F, 1, .5F);
        OutlineRenderer.drawLine(builder, poseStack, 1, 0, 0,   1, 1, .5F);
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 1, .5F, 1,   1);
    }

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state)
    {
        OutlineRenderer.super.rotateMatrix(poseStack, state);

        if (state.getValue(FramedProperties.TOP))
        {
            OutlineRenderer.mirrorHorizontally(poseStack, true);
        }
    }
}
