package xfacthd.framedblocks.client.data.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.util.client.OutlineRender;

public final class InverseDoubleSlopeSlabOutlineRenderer implements OutlineRender
{
    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        //Back vertical edges
        OutlineRender.drawLine(builder, poseStack, 0, .5, 1, 0, 1, 1);
        OutlineRender.drawLine(builder, poseStack, 1, .5, 1, 1, 1, 1);

        //Center horizontal edges
        OutlineRender.drawLine(builder, poseStack, 0, .5, 0, 1, .5, 0);
        OutlineRender.drawLine(builder, poseStack, 0, .5, 1, 1, .5, 1);

        //Top edge
        OutlineRender.drawLine(builder, poseStack, 0, 1, 1, 1, 1, 1);

        //Top slope
        OutlineRender.drawLine(builder, poseStack, 0, .5, 0, 0, 1, 1);
        OutlineRender.drawLine(builder, poseStack, 1, .5, 0, 1, 1, 1);

        //Bottom edge
        OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 1, 0, 0);

        //Bottom slope
        OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 0, .5, 1);
        OutlineRender.drawLine(builder, poseStack, 1, 0, 0, 1, .5, 1);

        //Front vertical edges
        OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 0, .5, 0);
        OutlineRender.drawLine(builder, poseStack, 1, 0, 0, 1, .5, 0);
    }
}
