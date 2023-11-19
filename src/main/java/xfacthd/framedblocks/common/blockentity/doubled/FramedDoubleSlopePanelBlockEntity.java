package xfacthd.framedblocks.common.blockentity.doubled;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedDoubleSlopePanelBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleSlopePanelBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_DOUBLE_SLOPE_PANEL.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction face = hit.getDirection();
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (face == facing)
        {
            return false;
        }
        if (face == facing.getOpposite())
        {
            return true;
        }

        Direction orientation = getBlockState().getValue(PropertyHolder.ROTATION).withFacing(facing);
        if (face == orientation)
        {
            return true;
        }
        if (face == orientation.getOpposite())
        {
            return false;
        }

        Vec3 vec = Utils.fraction(hit.getLocation());

        double hor = Utils.isX(facing) ? vec.x() : vec.z();
        if (!Utils.isPositive(facing))
        {
            hor = 1D - hor;
        }
        if (!getBlockState().getValue(PropertyHolder.FRONT))
        {
            hor -= .5D;
        }

        double vert = Utils.isY(orientation) ? vec.y() : (Utils.isX(facing) ? vec.z() : vec.x());
        if (orientation == Direction.DOWN || (!Utils.isY(orientation) && !Utils.isPositive(orientation)))
        {
            vert = 1F - vert;
        }
        return (hor * 2D) < vert;
    }
}
