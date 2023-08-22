package xfacthd.framedblocks.client.data.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.render.OutlineRenderer;
import xfacthd.framedblocks.common.block.ISlopeBlock;
import xfacthd.framedblocks.common.data.property.SlopeType;

public final class SlopeOutlineRenderer implements OutlineRenderer
{
    public static final SlopeOutlineRenderer INSTANCE = new SlopeOutlineRenderer();

    private SlopeOutlineRenderer() { }

    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        SlopeType type = ((ISlopeBlock) state.getBlock()).getSlopeType(state);

        if (type != SlopeType.HORIZONTAL)
        {
            //Back edges
            OutlineRenderer.drawLine(builder, poseStack, 0, 0, 1, 0, 1, 1);
            OutlineRenderer.drawLine(builder, poseStack, 1, 0, 1, 1, 1, 1);

            //Bottom face
            OutlineRenderer.drawLine(builder, poseStack, 0, 0, 0, 0, 0, 1);
            OutlineRenderer.drawLine(builder, poseStack, 0, 0, 0, 1, 0, 0);
            OutlineRenderer.drawLine(builder, poseStack, 1, 0, 0, 1, 0, 1);
            OutlineRenderer.drawLine(builder, poseStack, 0, 0, 1, 1, 0, 1);

            //Top edge
            OutlineRenderer.drawLine(builder, poseStack, 0, 1, 1, 1, 1, 1);

            //Slope
            OutlineRenderer.drawLine(builder, poseStack, 0, 0, 0, 0, 1, 1);
            OutlineRenderer.drawLine(builder, poseStack, 1, 0, 0, 1, 1, 1);
        }
        else
        {
            //Back
            OutlineRenderer.drawLine(builder, poseStack, 0, 0, 1, 1, 0, 1);
            OutlineRenderer.drawLine(builder, poseStack, 0, 1, 1, 1, 1, 1);
            OutlineRenderer.drawLine(builder, poseStack, 0, 0, 1, 0, 1, 1);
            OutlineRenderer.drawLine(builder, poseStack, 1, 0, 1, 1, 1, 1);

            //Left side
            OutlineRenderer.drawLine(builder, poseStack, 1, 0, 0, 1, 0, 1);
            OutlineRenderer.drawLine(builder, poseStack, 1, 1, 0, 1, 1, 1);
            OutlineRenderer.drawLine(builder, poseStack, 1, 0, 0, 1, 1, 0);

            //Slope
            OutlineRenderer.drawLine(builder, poseStack, 1, 0, 0, 0, 0, 1);
            OutlineRenderer.drawLine(builder, poseStack, 1, 1, 0, 0, 1, 1);
        }
    }

    @Override
    public Direction getRotationDir(BlockState state)
    {
        return ((ISlopeBlock) state.getBlock()).getFacing(state);
    }

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state)
    {
        OutlineRenderer.super.rotateMatrix(poseStack, state);

        if (((ISlopeBlock) state.getBlock()).getSlopeType(state) == SlopeType.TOP)
        {
            OutlineRenderer.mirrorHorizontally(poseStack, false);
        }
    }
}
