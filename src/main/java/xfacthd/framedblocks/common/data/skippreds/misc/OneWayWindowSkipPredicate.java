package xfacthd.framedblocks.common.data.skippreds.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.predicate.SideSkipPredicate;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.NullableDirection;

public class OneWayWindowSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() != FBContent.blockFramedOneWayWindow.get())
        {
            return SideSkipPredicate.CTM.test(level, pos, state, adjState, side);
        }

        NullableDirection face = state.getValue(PropertyHolder.NULLABLE_FACE);
        NullableDirection adjFace = adjState.getValue(PropertyHolder.NULLABLE_FACE);
        if (face == adjFace)
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    }
}
