package xfacthd.framedblocks.client.data.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.render.OutlineRenderer;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class FlatSlopeSlabCornerOutlineRenderer implements OutlineRenderer
{
    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        //Bottom face
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 0, 0, 0, 1);
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 0, 1, 0, 0);
        OutlineRenderer.drawLine(builder, poseStack, 1, 0, 0, 1, 0, 1);
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 1, 1, 0, 1);

        //Back edge
        OutlineRenderer.drawLine(builder, poseStack, 1, 0, 1, 1, .5F, 1);

        //Slope
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 1, 1, .5F, 1);
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 0, 1, .5F, 1);
        OutlineRenderer.drawLine(builder, poseStack, 1, 0, 0, 1, .5F, 1);
    }

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state)
    {
        OutlineRenderer.super.rotateMatrix(poseStack, state);

        boolean top = state.getValue(FramedProperties.TOP);
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);
        if (top)
        {
            OutlineRenderer.mirrorHorizontally(poseStack, true);
        }
        if (topHalf != top)
        {
            poseStack.translate(0, .5, 0);
        }
    }
}
