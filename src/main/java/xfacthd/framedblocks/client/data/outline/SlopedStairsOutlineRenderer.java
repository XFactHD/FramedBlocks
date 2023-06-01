package xfacthd.framedblocks.client.data.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.client.OutlineRender;

public final class SlopedStairsOutlineRenderer implements OutlineRender
{
    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        // Bottom face
        OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 0, 0, 1);
        OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 1, 0, 0);
        OutlineRender.drawLine(builder, poseStack, 1, 0, 0, 1, 0, 1);
        OutlineRender.drawLine(builder, poseStack, 0, 0, 1, 1, 0, 1);

        // Center face
        OutlineRender.drawLine(builder, poseStack, 0, .5, 0, 0, .5, 1);
        OutlineRender.drawLine(builder, poseStack, 0, .5, 0, 1, .5, 0);

        // Top face
        OutlineRender.drawLine(builder, poseStack, 1, 1, 0, 1, 1, 1);
        OutlineRender.drawLine(builder, poseStack, 0, 1, 1, 1, 1, 1);

        // Vertical edges
        OutlineRender.drawLine(builder, poseStack, 0, 0, 1, 0, 1, 1);
        OutlineRender.drawLine(builder, poseStack, 1, 0, 0, 1, 1, 0);
        OutlineRender.drawLine(builder, poseStack, 1, 0, 1, 1, 1, 1);

        // Front vertical edge
        OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 0, .5, 0);

        // Top diagonal edge
        OutlineRender.drawLine(builder, poseStack, 0, 1, 1, 1, 1, 0);

        // Center diagonal edge
        OutlineRender.drawLine(builder, poseStack, 0, .5, 1, 1, .5, 0);
    }

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state)
    {
        OutlineRender.super.rotateMatrix(poseStack, state);

        if (state.getValue(FramedProperties.TOP))
        {
            OutlineRender.mirrorHorizontally(poseStack, true);
        }
    }
}
