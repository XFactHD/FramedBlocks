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
import xfacthd.framedblocks.common.data.PropertyHolder;

public class SlopedPrismOutlineRenderer extends PrismOutlineRenderer
{
    private static final Quaternion[][] ZP_DIR = makeQuaternionArray();

    @Override
    public void drawCenterAndTriangle(PoseStack pstack, VertexConsumer builder)
    {
        // Center line
        OutlineRender.drawLine(builder, pstack, .5F, .5F, .5F, .5F, .5F, 1);

        // Front sloped triangle
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, .5F, .5F, .5F);
        OutlineRender.drawLine(builder, pstack, .5F, .5F, .5F, 1, 0, 0);
    }

    @Override
    public void rotateMatrix(PoseStack pstack, BlockState state)
    {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        Direction orientation = state.getValue(PropertyHolder.ORIENTATION);

        if (Utils.isY(facing))
        {
            if (orientation != Direction.SOUTH)
            {
                pstack.mulPose(YN_DIR[orientation.get2DDataValue()]);
            }
            if (facing == Direction.DOWN)
            {
                pstack.mulPose(Quaternions.ZP_180);
            }
        }
        else
        {
            if (facing != Direction.SOUTH)
            {
                pstack.mulPose(YN_DIR[facing.get2DDataValue()]);
            }
            if (orientation != Direction.DOWN)
            {
                pstack.mulPose(ZP_DIR[facing.get2DDataValue()][orientation.ordinal()]);
            }
            pstack.mulPose(Quaternions.XP_90);
        }
    }



    private static Quaternion[][] makeQuaternionArray()
    {
        Quaternion[][] array = new Quaternion[4][6];
        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            array[dir.get2DDataValue()] = new Quaternion[6];
            for (Direction orientation : Direction.values())
            {
                int mult = 2;
                if (orientation == dir.getCounterClockWise())
                {
                    mult = 1;
                }
                else if (orientation == dir.getClockWise())
                {
                    mult = 3;
                }
                array[dir.get2DDataValue()][orientation.ordinal()] = Vector3f.ZP.rotation(Mth.PI / 2F * mult);
            }
        }
        return array;
    }
}
