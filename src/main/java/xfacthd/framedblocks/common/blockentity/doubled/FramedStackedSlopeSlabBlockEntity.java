package xfacthd.framedblocks.common.blockentity.doubled;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

public class FramedStackedSlopeSlabBlockEntity extends FramedDoubleBlockEntity
{
    public FramedStackedSlopeSlabBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_STACKED_SLOPE_SLAB.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        if (hit.getDirection() == Direction.DOWN)
        {
            return top;
        }

        boolean upper = hit.getDirection() == Direction.UP || Mth.frac(hit.getLocation().y()) >= .5F;
        return upper != top;
    }
}
