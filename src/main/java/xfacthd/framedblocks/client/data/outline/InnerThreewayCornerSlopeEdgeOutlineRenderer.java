package xfacthd.framedblocks.client.data.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.render.OutlineRenderer;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class InnerThreewayCornerSlopeEdgeOutlineRenderer implements OutlineRenderer
{
    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        if (state.getValue(PropertyHolder.ALT_TYPE))
        {
            //Back face
            OutlineRenderer.drawLine(builder, poseStack,   0, .5F, .5F, .5F, .5F, .5F);
            OutlineRenderer.drawLine(builder, poseStack,   0,   1, .5F, .5F,   1, .5F);
            OutlineRenderer.drawLine(builder, poseStack,   0, .5F, .5F,   0,   1, .5F);
            OutlineRenderer.drawLine(builder, poseStack, .5F, .5F, .5F, .5F,   1, .5F);

            //Left face
            OutlineRenderer.drawLine(builder, poseStack, .5F, .5F, 0, .5F, .5F, .5F);
            OutlineRenderer.drawLine(builder, poseStack, .5F,   1, 0, .5F,   1, .5F);
            OutlineRenderer.drawLine(builder, poseStack, .5F, .5F, 0, .5F,   1,   0);

            //Bottom face
            OutlineRenderer.drawLine(builder, poseStack, 0, .5F, 0,   0, .5F, .5F);
            OutlineRenderer.drawLine(builder, poseStack, 0, .5F, 0, .5F, .5F,   0);

            //Slope edges
            OutlineRenderer.drawLine(builder, poseStack,    0,  .5F,    0,    0,    1,  .5F);
            OutlineRenderer.drawLine(builder, poseStack,    0,  .5F,    0,  .5F,    1,    0);
            OutlineRenderer.drawLine(builder, poseStack,  .5F,    1,    0,    0,    1,  .5F);
            OutlineRenderer.drawLine(builder, poseStack,    0,  .5F,    0, .25F, .75F, .25F);
            OutlineRenderer.drawLine(builder, poseStack, .25F, .75F, .25F,    0,    1,  .5F);
            OutlineRenderer.drawLine(builder, poseStack, .25F, .75F, .25F,  .5F,    1,    0);
        }
        else
        {
            //Back face
            OutlineRenderer.drawLine(builder, poseStack,   0,   0,   1,   1,   0,   1);
            OutlineRenderer.drawLine(builder, poseStack,   0, .5F,   1, .5F, .5F,   1);
            OutlineRenderer.drawLine(builder, poseStack,   0,   0,   1,   0, .5F,   1);
            OutlineRenderer.drawLine(builder, poseStack,   1,   0,   1,   1,   1,   1);
            OutlineRenderer.drawLine(builder, poseStack, .5F, .5F,   1, .5F,   1,   1);
            OutlineRenderer.drawLine(builder, poseStack, .5F,   1,   1,   1,   1,   1);

            //Left face
            OutlineRenderer.drawLine(builder, poseStack, 1,   0,   0, 1,   0,   1);
            OutlineRenderer.drawLine(builder, poseStack, 1, .5F,   0, 1, .5F, .5F);
            OutlineRenderer.drawLine(builder, poseStack, 1,   0,   0, 1, .5F,   0);
            OutlineRenderer.drawLine(builder, poseStack, 1, .5F, .5F, 1,   1, .5F);
            OutlineRenderer.drawLine(builder, poseStack, 1,   1, .5F, 1,   1,   1);

            //Bottom face
            OutlineRenderer.drawLine(builder, poseStack,   0, 0, .5F,   0, 0,   1);
            OutlineRenderer.drawLine(builder, poseStack, .5F, 0,   0,   1, 0,   0);
            OutlineRenderer.drawLine(builder, poseStack,   0, 0, .5F, .5F, 0, .5F);
            OutlineRenderer.drawLine(builder, poseStack, .5F, 0,   0, .5F, 0, .5F);

            //Slope edges
            OutlineRenderer.drawLine(builder, poseStack,    0,    0,  .5F,    0,  .5F,    1);
            OutlineRenderer.drawLine(builder, poseStack,  .5F,    0,    0,    1,  .5F,    0);
            OutlineRenderer.drawLine(builder, poseStack,  .5F,    0,  .5F, .75F, .25F, .75F);
            OutlineRenderer.drawLine(builder, poseStack, .75F, .25F, .75F,  .5F,  .5F,    1);
            OutlineRenderer.drawLine(builder, poseStack, .75F, .25F, .75F,    1,  .5F,  .5F);
            OutlineRenderer.drawLine(builder, poseStack,    1,    1,  .5F,  .5F,    1,    1);
        }
    }

    @Override
    public Direction getRotationDir(BlockState state)
    {
        Direction dir = OutlineRenderer.super.getRotationDir(state);
        if (state.getValue(PropertyHolder.RIGHT))
        {
            dir = dir.getClockWise();
        }
        return dir;
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
