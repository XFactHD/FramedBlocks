package xfacthd.framedblocks.client.data.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.render.OutlineRenderer;
import xfacthd.framedblocks.api.render.Quaternions;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class SlopeEdgeOutlineRenderer implements OutlineRenderer
{
    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        // Horizontal perpendicular edges
        OutlineRenderer.drawLine(builder, poseStack, 0D,  0D,  1D, 1D,  0D,  1D);
        OutlineRenderer.drawLine(builder, poseStack, 0D,  0D, .5D, 1D,  0D, .5D);
        OutlineRenderer.drawLine(builder, poseStack, 0D, .5D,  1D, 1D, .5D,  1D);

        // Vertical edges
        OutlineRenderer.drawLine(builder, poseStack, 0D, 0D, 1D, 0D, .5D, 1D);
        OutlineRenderer.drawLine(builder, poseStack, 1D, 0D, 1D, 1D, .5D, 1D);

        // Horizontal parallel edges
        OutlineRenderer.drawLine(builder, poseStack, 0D, 0D, .5D, 0D, 0D, 1D);
        OutlineRenderer.drawLine(builder, poseStack, 1D, 0D, .5D, 1D, 0D, 1D);

        // Sloped edges
        OutlineRenderer.drawLine(builder, poseStack, 0D, 0D, .5D, 0D, .5D, 1D);
        OutlineRenderer.drawLine(builder, poseStack, 1D, 0D, .5D, 1D, .5D, 1D);
    }

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state)
    {
        OutlineRenderer.super.rotateMatrix(poseStack, state);
        switch (state.getValue(PropertyHolder.SLOPE_TYPE))
        {
            case TOP -> poseStack.mulPose(Quaternions.ZP_180);
            case HORIZONTAL -> poseStack.mulPose(Quaternions.ZP_90);
        }
        if (state.getValue(PropertyHolder.ALT_TYPE))
        {
            poseStack.translate(0F, .5F, -.5F);
        }
    }
}
