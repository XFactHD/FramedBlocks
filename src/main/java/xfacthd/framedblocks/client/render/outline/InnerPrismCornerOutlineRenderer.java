package xfacthd.framedblocks.client.render.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.render.OutlineRenderer;

public final class InnerPrismCornerOutlineRenderer implements OutlineRenderer
{
    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        //Bottom face
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 0, 0, 0, 1);
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 0, 1, 0, 0);
        OutlineRenderer.drawLine(builder, poseStack, 1, 0, 0, 1, 0, 1);
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 1, 1, 0, 1);

        //Back face
        OutlineRenderer.drawLine(builder, poseStack, 1, 0, 0, 1, 1, 0);
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 1, 0, 1, 1);
        OutlineRenderer.drawLine(builder, poseStack, 1, 1, 0, 1, 1, 1);

        //Right face
        OutlineRenderer.drawLine(builder, poseStack, 0, 1, 1, 1, 1, 1);
        OutlineRenderer.drawLine(builder, poseStack, 1, 0, 1, 1, 1, 1);

        //Slope edges
        OutlineRenderer.drawLine(builder, poseStack, 1, 1, 0, 0, 1, 1);
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 0, 1, 1, 0);
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 0, 0, 1, 1);
    }

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state)
    {
        OutlineRenderer.super.rotateMatrix(poseStack, state);

        if (state.getValue(FramedProperties.TOP))
        {
            OutlineRenderer.mirrorHorizontally(poseStack, true);
        }
    }
}
