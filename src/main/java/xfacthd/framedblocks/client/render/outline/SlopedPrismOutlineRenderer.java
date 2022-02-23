package xfacthd.framedblocks.client.render.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.client.OutlineRender;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class SlopedPrismOutlineRenderer extends PrismOutlineRenderer
{
    @Override
    public void drawCenterAndTriangle(PoseStack pstack, VertexConsumer builder)
    {
        OutlineRender.drawLine(builder, pstack, .5F, .5F, .5F, .5F, .5F, 1);
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
                pstack.mulPose(Vector3f.YN.rotation(Mth.PI / 2F * orientation.get2DDataValue()));
            }
            if (facing == Direction.DOWN)
            {
                pstack.mulPose(ZP_180);
            }
        }
        else
        {
            if (facing != Direction.SOUTH)
            {
                pstack.mulPose(Vector3f.YN.rotation(Mth.PI / 2F * facing.get2DDataValue()));
            }
            if (orientation != Direction.DOWN)
            {
                int mult = 2;
                if (orientation == facing.getCounterClockWise())
                {
                    mult = 1;
                }
                else if (orientation == facing.getClockWise())
                {
                    mult = 3;
                }
                pstack.mulPose(Vector3f.ZP.rotation(Mth.PI / 2F * mult));
            }
            pstack.mulPose(XP_90);
        }
    }
}
