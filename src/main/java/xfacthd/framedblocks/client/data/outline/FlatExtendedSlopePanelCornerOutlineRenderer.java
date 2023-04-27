package xfacthd.framedblocks.client.data.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.render.OutlineRenderer;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public final class FlatExtendedSlopePanelCornerOutlineRenderer implements OutlineRenderer
{
    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        // Back edges
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 1, 1, 0, 1);
        OutlineRenderer.drawLine(builder, poseStack, 0, 1, 1, 1, 1, 1);
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 1, 0, 1, 1);
        OutlineRenderer.drawLine(builder, poseStack, 1, 0, 1, 1, 1, 1);

        // Side edges
        OutlineRenderer.drawLine(builder, poseStack, 0, 0,  0, 0, 0, 1);
        OutlineRenderer.drawLine(builder, poseStack, 0, 1, .5, 0, 1, 1);
        OutlineRenderer.drawLine(builder, poseStack, 1, 0, .5, 1, 0, 1);
        OutlineRenderer.drawLine(builder, poseStack, 1, 1, .5, 1, 1, 1);

        //Front edges
        OutlineRenderer.drawLine(builder, poseStack, 0, 1, .5, 1, 1, .5);
        OutlineRenderer.drawLine(builder, poseStack, 1, 0, .5, 1, 1, .5);

        // Slopes
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 0, 1, 0, .5);
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 0, 1, 1, .5);
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 0, 0, 1, .5);
    }

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state)
    {
        OutlineRenderer.super.rotateMatrix(poseStack, state);

        HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
        poseStack.mulPose(SlopePanelOutlineRenderer.ROTATIONS[rotation.ordinal()]);
    }
}
