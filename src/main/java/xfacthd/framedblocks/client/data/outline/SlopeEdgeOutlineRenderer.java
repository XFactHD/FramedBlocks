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
        OutlineRenderer.drawLine(builder, poseStack, 0F,  0F,  1F, 1F,  0F,  1F);
        OutlineRenderer.drawLine(builder, poseStack, 0F,  0F, .5F, 1F,  0F, .5F);
        OutlineRenderer.drawLine(builder, poseStack, 0F, .5F,  1F, 1F, .5F,  1F);

        // Vertical edges
        OutlineRenderer.drawLine(builder, poseStack, 0F, 0F, 1F, 0F, .5F, 1F);
        OutlineRenderer.drawLine(builder, poseStack, 1F, 0F, 1F, 1F, .5F, 1F);

        // Horizontal parallel edges
        OutlineRenderer.drawLine(builder, poseStack, 0F, 0F, .5F, 0F, 0F, 1F);
        OutlineRenderer.drawLine(builder, poseStack, 1F, 0F, .5F, 1F, 0F, 1F);

        // Sloped edges
        OutlineRenderer.drawLine(builder, poseStack, 0F, 0F, .5F, 0F, .5F, 1F);
        OutlineRenderer.drawLine(builder, poseStack, 1F, 0F, .5F, 1F, .5F, 1F);
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
