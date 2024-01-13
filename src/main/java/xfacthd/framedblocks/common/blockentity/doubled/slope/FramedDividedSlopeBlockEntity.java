package xfacthd.framedblocks.common.blockentity.doubled.slope;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;

public class FramedDividedSlopeBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDividedSlopeBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_DIVIDED_SLOPE.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction dir;
        if (getBlockState().getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL)
        {
            dir = Direction.UP;
        }
        else
        {
            Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
            dir = facing.getClockWise();
        }

        Direction side = hit.getDirection();
        if (side == dir)
        {
            return true;
        }
        if (side == dir.getOpposite())
        {
            return false;
        }

        return Utils.fractionInDir(hit.getLocation(), dir) > .5;
    }
}
