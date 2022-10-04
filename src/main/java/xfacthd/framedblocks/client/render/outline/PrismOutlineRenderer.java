package xfacthd.framedblocks.client.render.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.render.Quaternions;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.client.OutlineRender;

public class PrismOutlineRenderer implements OutlineRender
{
    protected static final Quaternion[] YN_DIR = makeQuaternionArray();

    @Override
    public void draw(BlockState state, PoseStack pstack, VertexConsumer builder)
    {
        // Base edges
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, 0, 0, 1);
        OutlineRender.drawLine(builder, pstack, 1, 0, 0, 1, 0, 1);
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, 1, 0, 0);
        OutlineRender.drawLine(builder, pstack, 0, 0, 1, 1, 0, 1);

        // Back triangle
        OutlineRender.drawLine(builder, pstack, 0, 0, 1, .5F, .5F, 1);
        OutlineRender.drawLine(builder, pstack, .5F, .5F, 1, 1, 0, 1);

        drawCenterAndTriangle(pstack, builder);
    }

    protected void drawCenterAndTriangle(PoseStack pstack, VertexConsumer builder)
    {
        // Center line
        OutlineRender.drawLine(builder, pstack, .5F, .5F, 0, .5F, .5F, 1);

        // Front triangle
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
                pstack.mulPose(Quaternions.ZP_180);
            }
            if (axis == Direction.Axis.X)
            {
                pstack.mulPose(Quaternions.YP_90);
            }
        }
        else
        {
            if (facing != Direction.SOUTH)
            {
                pstack.mulPose(YN_DIR[facing.get2DDataValue()]);
            }
            if (axis != Direction.Axis.Y)
            {
                pstack.mulPose(Quaternions.ZP_90);
            }
            pstack.mulPose(Quaternions.XP_90);
        }
    }



    private static Quaternion[] makeQuaternionArray()
    {
        Quaternion[] array = new Quaternion[4];
        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            array[dir.get2DDataValue()] = Vector3f.YN.rotation(Mth.PI / 2F * dir.get2DDataValue());
        }
        return array;
    }
}
