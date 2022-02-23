package xfacthd.framedblocks.client.render.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.client.OutlineRender;

public class PrismOutlineRenderer implements OutlineRender
{
    protected static final Quaternion ZP_180 = Vector3f.ZP.rotation(Mth.PI);
    protected static final Quaternion XP_90 = Vector3f.XP.rotation(Mth.PI / 2F);
    protected static final Quaternion YP_90 = Vector3f.YP.rotation(Mth.PI / 2F);
    protected static final Quaternion ZP_90 = Vector3f.ZP.rotation(Mth.PI / 2F);

    @Override
    public void draw(BlockState state, PoseStack pstack, VertexConsumer builder)
    {
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, 0, 0, 1);
        OutlineRender.drawLine(builder, pstack, 1, 0, 0, 1, 0, 1);
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, 1, 0, 0);
        OutlineRender.drawLine(builder, pstack, 0, 0, 1, 1, 0, 1);
        OutlineRender.drawLine(builder, pstack, 0, 0, 1, .5F, .5F, 1);
        OutlineRender.drawLine(builder, pstack, .5F, .5F, 1, 1, 0, 1);

        drawCenterAndTriangle(pstack, builder);
    }

    protected void drawCenterAndTriangle(PoseStack pstack, VertexConsumer builder)
    {
        OutlineRender.drawLine(builder, pstack, .5F, .5F, 0, .5F, .5F, 1);
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, .5F, .5F, 0);
        OutlineRender.drawLine(builder, pstack, .5F, .5F, 0, 1, 0, 0);
    }

    @Override
    public void rotateMatrix(PoseStack pstack, BlockState state)
    {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);

        if (Utils.isY(facing))
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
                pstack.mulPose(Vector3f.YN.rotation(Mth.PI / 2F * facing.get2DDataValue()));
            }
            if (axis != Direction.Axis.Y)
            {
                pstack.mulPose(ZP_90);
            }
            pstack.mulPose(XP_90);
        }
    }
}
