package xfacthd.framedblocks.client.data.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.render.OutlineRenderer;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public final class VerticalSlopedStairsOutlineRenderer implements OutlineRenderer
{
    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        // Back face
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 1, 0, 1, 1);
        OutlineRenderer.drawLine(builder, poseStack, 1, 0, 1, 1, 1, 1);
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 1, 1, 0, 1);
        OutlineRenderer.drawLine(builder, poseStack, 0, 1, 1, 1, 1, 1);

        // Front face
        OutlineRenderer.drawLine(builder, poseStack, 0, 1, .5F, 1, 1, .5F);
        OutlineRenderer.drawLine(builder, poseStack, 1, 0, .5F, 1, 1, .5F);
        OutlineRenderer.drawLine(builder, poseStack, 1, 0, .5F, 0, 1, .5F);

        // Middle face
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 0, 1, 0, 0);
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 0, 0, 1, 0);
        OutlineRenderer.drawLine(builder, poseStack, 1, 0, 0, 0, 1, 0);

        // Horizontal side edges
        OutlineRenderer.drawLine(builder, poseStack, 0, 0,   0, 0, 0, 1);
        OutlineRenderer.drawLine(builder, poseStack, 0, 1,   0, 0, 1, 1);
        OutlineRenderer.drawLine(builder, poseStack, 1, 0,   0, 1, 0, 1);
        OutlineRenderer.drawLine(builder, poseStack, 1, 1, .5F, 1, 1, 1);
    }

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state)
    {
        OutlineRenderer.super.rotateMatrix(poseStack, state);

        HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
        poseStack.mulPose(SlopePanelOutlineRenderer.ROTATIONS[rotation.ordinal()]);
    }
}
