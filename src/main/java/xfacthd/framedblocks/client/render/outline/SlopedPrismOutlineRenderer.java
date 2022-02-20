package xfacthd.framedblocks.client.render.outline;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class SlopedPrismOutlineRenderer extends PrismOutlineRenderer
{
    @Override
    public void drawCenterAndTriangle(MatrixStack pstack, IVertexBuilder builder)
    {
        OutlineRender.drawLine(builder, pstack, .5F, .5F, .5F, .5F, .5F, 1);
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, .5F, .5F, .5F);
        OutlineRender.drawLine(builder, pstack, .5F, .5F, .5F, 1, 0, 0);
    }

    @Override
    public void rotateMatrix(MatrixStack pstack, BlockState state)
    {
        Direction facing = state.get(BlockStateProperties.FACING);
        Direction orientation = state.get(PropertyHolder.ORIENTATION);

        if (facing.getAxis() == Direction.Axis.Y)
        {
            if (orientation != Direction.SOUTH)
            {
                pstack.rotate(Vector3f.YN.rotation(PI / 2F * orientation.getHorizontalIndex()));
            }
            if (facing == Direction.DOWN)
            {
                pstack.rotate(ZP_180);
            }
        }
        else
        {
            if (facing != Direction.SOUTH)
            {
                pstack.rotate(Vector3f.YN.rotation(PI / 2F * facing.getHorizontalIndex()));
            }
            if (orientation != Direction.DOWN)
            {
                int mult = 2;
                if (orientation == facing.rotateYCCW())
                {
                    mult = 1;
                }
                else if (orientation == facing.rotateY())
                {
                    mult = 3;
                }
                pstack.rotate(Vector3f.ZP.rotation(PI / 2F * mult));
            }
            pstack.rotate(XP_90);
        }
    }
}
