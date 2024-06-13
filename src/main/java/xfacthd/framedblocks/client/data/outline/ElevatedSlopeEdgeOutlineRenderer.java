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
        OutlineRenderer.drawLine(builder, poseStack, 0F, 0F, 0F, 1F, 0F, 0F);
        OutlineRenderer.drawLine(builder, poseStack, 0F, 0F, 1F, 1F, 0F, 1F);
        OutlineRenderer.drawLine(builder, poseStack, 0F, 0F, 0F, 0F, 0F, 1F);
        OutlineRenderer.drawLine(builder, poseStack, 1F, 0F, 0F, 1F, 0F, 1F);

        // Back face
        OutlineRenderer.drawLine(builder, poseStack, 0F, 1F, 1F, 1F, 1F, 1F);
        OutlineRenderer.drawLine(builder, poseStack, 0F, 0F, 1F, 0F, 1F, 1F);
        OutlineRenderer.drawLine(builder, poseStack, 1F, 0F, 1F, 1F, 1F, 1F);

        // Top face
        OutlineRenderer.drawLine(builder, poseStack, 0F, 1F, .5F, 0F, 1F, 1F);
        OutlineRenderer.drawLine(builder, poseStack, 1F, 1F, .5F, 1F, 1F, 1F);

        // Front face
        OutlineRenderer.drawLine(builder, poseStack, 0F, 0F, 0F, 0F, .5F, 0F);
        OutlineRenderer.drawLine(builder, poseStack, 1F, 0F, 0F, 1F, .5F, 0F);

        // Horizontal edges
        OutlineRenderer.drawLine(builder, poseStack, 0F, .5F,  0F, 1F, .5F,  0F);
        OutlineRenderer.drawLine(builder, poseStack, 0F,  1F, .5F, 1F,  1F, .5F);

        // Sloped edges
        OutlineRenderer.drawLine(builder, poseStack, 0F, .5F, 0F, 0F, 1F, .5F);
        OutlineRenderer.drawLine(builder, poseStack, 1F, .5F, 0F, 1F, 1F, .5F);
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
