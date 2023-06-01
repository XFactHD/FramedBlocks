package xfacthd.framedblocks.client.data.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.client.OutlineRender;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class HalfSlopeOutlineRenderer implements OutlineRender
{
    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        //Back edges
        OutlineRender.drawLine(builder, poseStack,  0, 0, 1,  0, 1, 1);
        OutlineRender.drawLine(builder, poseStack, .5, 0, 1, .5, 1, 1);

        //Bottom face
        OutlineRender.drawLine(builder, poseStack,  0, 0, 0,  0, 0, 1);
        OutlineRender.drawLine(builder, poseStack,  0, 0, 0, .5, 0, 0);
        OutlineRender.drawLine(builder, poseStack, .5, 0, 0, .5, 0, 1);
        OutlineRender.drawLine(builder, poseStack,  0, 0, 1, .5, 0, 1);

        //Top edge
        OutlineRender.drawLine(builder, poseStack, 0, 1, 1, .5, 1, 1);

        //Slope
        OutlineRender.drawLine(builder, poseStack,  0, 0, 0,  0, 1, 1);
        OutlineRender.drawLine(builder, poseStack, .5, 0, 0, .5, 1, 1);
    }

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state)
    {
        OutlineRender.super.rotateMatrix(poseStack, state);

        boolean top = state.getValue(FramedProperties.TOP);

        if (state.getValue(PropertyHolder.RIGHT) == top)
        {
            poseStack.translate(top ? -.5 : .5, 0, 0);
        }

        if (top)
        {
            OutlineRender.mirrorHorizontally(poseStack, false);
        }
    }
}
