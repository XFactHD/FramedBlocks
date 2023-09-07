package xfacthd.framedblocks.common.data.skippreds.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.NullableDirection;
import xfacthd.framedblocks.common.data.skippreds.CullTest;

@CullTest(BlockType.FRAMED_ONE_WAY_WINDOW)
public final class OneWayWindowSkipPredicate implements SideSkipPredicate
{
    @Override
    @CullTest.SingleTarget(BlockType.FRAMED_ONE_WAY_WINDOW)
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() == state.getBlock())
        {
            NullableDirection face = state.getValue(PropertyHolder.NULLABLE_FACE);
            NullableDirection adjFace = adjState.getValue(PropertyHolder.NULLABLE_FACE);
            return face == adjFace;
        }
        return false;
    }
}
