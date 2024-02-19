package xfacthd.framedblocks.client.data.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.render.OutlineRenderer;
import xfacthd.framedblocks.api.render.Quaternions;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class ElevatedSlopeEdgeOutlineRenderer implements OutlineRenderer
{
    public static final ElevatedSlopeEdgeOutlineRenderer INSTANCE = new ElevatedSlopeEdgeOutlineRenderer();

    private ElevatedSlopeEdgeOutlineRenderer() { }

    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        // Bottom face
        OutlineRenderer.drawLine(builder, poseStack, 0D, 0D, 0D, 1D, 0D, 0D);
        OutlineRenderer.drawLine(builder, poseStack, 0D, 0D, 1D, 1D, 0D, 1D);
        OutlineRenderer.drawLine(builder, poseStack, 0D, 0D, 0D, 0D, 0D, 1D);
        OutlineRenderer.drawLine(builder, poseStack, 1D, 0D, 0D, 1D, 0D, 1D);

        // Back face
        OutlineRenderer.drawLine(builder, poseStack, 0D, 1D, 1D, 1D, 1D, 1D);
        OutlineRenderer.drawLine(builder, poseStack, 0D, 0D, 1D, 0D, 1D, 1D);
        OutlineRenderer.drawLine(builder, poseStack, 1D, 0D, 1D, 1D, 1D, 1D);

        // Top face
        OutlineRenderer.drawLine(builder, poseStack, 0D, 1D, .5D, 0D, 1D, 1D);
        OutlineRenderer.drawLine(builder, poseStack, 1D, 1D, .5D, 1D, 1D, 1D);

        // Front face
        OutlineRenderer.drawLine(builder, poseStack, 0D, 0D, 0D, 0D, .5D, 0D);
        OutlineRenderer.drawLine(builder, poseStack, 1D, 0D, 0D, 1D, .5D, 0D);

        // Horizontal edges
        OutlineRenderer.drawLine(builder, poseStack, 0D, .5D,  0D, 1D, .5D,  0D);
        OutlineRenderer.drawLine(builder, poseStack, 0D,  1D, .5D, 1D,  1D, .5D);

        // Sloped edges
        OutlineRenderer.drawLine(builder, poseStack, 0D, .5D, 0D, 0D, 1D, .5D);
        OutlineRenderer.drawLine(builder, poseStack, 1D, .5D, 0D, 1D, 1D, .5D);
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
    }
}
