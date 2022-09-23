package xfacthd.framedblocks.client.render.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.util.client.OutlineRender;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.util.FramedUtils;

public class SlopeOutlineRenderer implements OutlineRender
{
    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        SlopeType type = FramedUtils.getSlopeType(state);

        if (type != SlopeType.HORIZONTAL)
        {
            //Back edges
            OutlineRender.drawLine(builder, poseStack, 0, 0, 1, 0, 1, 1);
            OutlineRender.drawLine(builder, poseStack, 1, 0, 1, 1, 1, 1);

            //Bottom face
            OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 0, 0, 1);
            OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 1, 0, 0);
            OutlineRender.drawLine(builder, poseStack, 1, 0, 0, 1, 0, 1);
            OutlineRender.drawLine(builder, poseStack, 0, 0, 1, 1, 0, 1);

            //Top edge
            OutlineRender.drawLine(builder, poseStack, 0, 1, 1, 1, 1, 1);

            //Slope
            OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 0, 1, 1);
            OutlineRender.drawLine(builder, poseStack, 1, 0, 0, 1, 1, 1);
        }
        else
        {
            //Back
            OutlineRender.drawLine(builder, poseStack, 0, 0, 1, 1, 0, 1);
            OutlineRender.drawLine(builder, poseStack, 0, 1, 1, 1, 1, 1);
            OutlineRender.drawLine(builder, poseStack, 0, 0, 1, 0, 1, 1);
            OutlineRender.drawLine(builder, poseStack, 1, 0, 1, 1, 1, 1);

            //Left side
            OutlineRender.drawLine(builder, poseStack, 1, 0, 0, 1, 0, 1);
            OutlineRender.drawLine(builder, poseStack, 1, 1, 0, 1, 1, 1);
            OutlineRender.drawLine(builder, poseStack, 1, 0, 0, 1, 1, 0);

            //Slope
            OutlineRender.drawLine(builder, poseStack, 1, 0, 0, 0, 0, 1);
            OutlineRender.drawLine(builder, poseStack, 1, 1, 0, 0, 1, 1);
        }
    }

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state)
    {
        OutlineRender.super.rotateMatrix(poseStack, state);

        if (FramedUtils.getSlopeType(state) == SlopeType.TOP)
        {
            OutlineRender.mirrorHorizontally(poseStack, false);
        }
    }
}
