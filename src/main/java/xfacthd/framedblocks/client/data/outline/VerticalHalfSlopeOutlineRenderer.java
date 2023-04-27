package xfacthd.framedblocks.client.data.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.render.OutlineRenderer;

public final class VerticalHalfSlopeOutlineRenderer implements OutlineRenderer
{
    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        //Back
        OutlineRenderer.drawLine(builder, poseStack, 0,  0, 1, 1,  0, 1);
        OutlineRenderer.drawLine(builder, poseStack, 0, .5, 1, 1, .5, 1);
        OutlineRenderer.drawLine(builder, poseStack, 0,  0, 1, 0, .5, 1);
        OutlineRenderer.drawLine(builder, poseStack, 1,  0, 1, 1, .5, 1);

        //Left side
        OutlineRenderer.drawLine(builder, poseStack, 1,  0, 0, 1,  0, 1);
        OutlineRenderer.drawLine(builder, poseStack, 1, .5, 0, 1, .5, 1);
        OutlineRenderer.drawLine(builder, poseStack, 1,  0, 0, 1, .5, 0);

        //Slope
        OutlineRenderer.drawLine(builder, poseStack, 1,  0, 0, 0,  0, 1);
        OutlineRenderer.drawLine(builder, poseStack, 1, .5, 0, 0, .5, 1);
    }

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state)
    {
        OutlineRenderer.super.rotateMatrix(poseStack, state);

        if (state.getValue(FramedProperties.TOP))
        {
            poseStack.translate(0, .5, 0);
        }
    }
}
