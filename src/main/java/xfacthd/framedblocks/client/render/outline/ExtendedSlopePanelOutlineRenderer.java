package xfacthd.framedblocks.client.render.outline;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.Rotation;

public class ExtendedSlopePanelOutlineRenderer implements OutlineRender
{
    @Override
    public void draw(BlockState state, MatrixStack matrixStack, IVertexBuilder builder)
    {
        OutlineRender.drawLine(builder, matrixStack, 0, 0,  0, 1, 0,  0);
        OutlineRender.drawLine(builder, matrixStack, 0, 0,  1, 1, 0,  1);
        OutlineRender.drawLine(builder, matrixStack, 0, 0,  0, 0, 0,  1);
        OutlineRender.drawLine(builder, matrixStack, 1, 0,  0, 1, 0,  1);
        OutlineRender.drawLine(builder, matrixStack, 0, 0,  1, 0, 1,  1);
        OutlineRender.drawLine(builder, matrixStack, 1, 0,  1, 1, 1,  1);
        OutlineRender.drawLine(builder, matrixStack, 0, 0,  0, 0, 1, .5);
        OutlineRender.drawLine(builder, matrixStack, 1, 0,  0, 1, 1, .5);
        OutlineRender.drawLine(builder, matrixStack, 0, 1, .5, 1, 1, .5);
        OutlineRender.drawLine(builder, matrixStack, 0, 1,  1, 1, 1,  1);
        OutlineRender.drawLine(builder, matrixStack, 0, 1, .5, 0, 1,  1);
        OutlineRender.drawLine(builder, matrixStack, 1, 1, .5, 1, 1,  1);
    }

    @Override
    public void rotateMatrix(MatrixStack matrixStack, BlockState state)
    {
        OutlineRender.super.rotateMatrix(matrixStack, state);

        Rotation rotation = state.getValue(PropertyHolder.ROTATION);
        matrixStack.mulPose(SlopePanelOutlineRenderer.ROTATIONS.get(rotation));
    }
}
