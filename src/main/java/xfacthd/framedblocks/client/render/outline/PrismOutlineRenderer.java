package xfacthd.framedblocks.client.render.outline;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class PrismOutlineRenderer implements OutlineRender
{
    protected static final float PI = (float) Math.PI;
    protected static final Quaternion ZP_180 = Vector3f.ZP.rotation(PI);
    protected static final Quaternion XP_90 = Vector3f.XP.rotation(PI / 2F);
    protected static final Quaternion YP_90 = Vector3f.YP.rotation(PI / 2F);
    protected static final Quaternion ZP_90 = Vector3f.ZP.rotation(PI / 2F);

    @Override
    public void draw(BlockState state, MatrixStack pstack, IVertexBuilder builder)
    {
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, 0, 0, 1);
        OutlineRender.drawLine(builder, pstack, 1, 0, 0, 1, 0, 1);
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, 1, 0, 0);
        OutlineRender.drawLine(builder, pstack, 0, 0, 1, 1, 0, 1);
        OutlineRender.drawLine(builder, pstack, 0, 0, 1, .5F, .5F, 1);
        OutlineRender.drawLine(builder, pstack, .5F, .5F, 1, 1, 0, 1);

        drawCenterAndTriangle(pstack, builder);
    }

    protected void drawCenterAndTriangle(MatrixStack pstack, IVertexBuilder builder)
    {
        OutlineRender.drawLine(builder, pstack, .5F, .5F, 0, .5F, .5F, 1);
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, .5F, .5F, 0);
        OutlineRender.drawLine(builder, pstack, .5F, .5F, 0, 1, 0, 0);
    }

    @Override
    public void rotateMatrix(MatrixStack pstack, BlockState state)
    {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);

        if (facing.getAxis() == Direction.Axis.Y)
        {
            if (facing == Direction.DOWN)
            {
                pstack.mulPose(ZP_180);
            }
            if (axis == Direction.Axis.X)
            {
                pstack.mulPose(YP_90);
            }
        }
        else
        {
            if (facing != Direction.SOUTH)
            {
                pstack.mulPose(Vector3f.YN.rotation(PI / 2F * facing.get2DDataValue()));
            }
            if (axis != Direction.Axis.Y)
            {
                pstack.mulPose(ZP_90);
            }
            pstack.mulPose(XP_90);
        }
    }
}
