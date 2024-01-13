package xfacthd.framedblocks.common.blockentity.doubled.stairs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;

public class FramedVerticalDividedStairsBlockEntity extends FramedDoubleBlockEntity
{
    public FramedVerticalDividedStairsBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_VERTICAL_DIVIDED_STAIRS.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction side = hit.getDirection();
        if (side == Direction.UP)
        {
            return true;
        }
        if (side == Direction.DOWN)
        {
            return false;
        }
        return Utils.fraction(hit.getLocation()).y > .5;
    }
}
