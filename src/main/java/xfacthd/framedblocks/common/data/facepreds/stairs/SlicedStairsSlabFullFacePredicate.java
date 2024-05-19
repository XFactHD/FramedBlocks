package xfacthd.framedblocks.common.data.facepreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;

public final class SlicedStairsSlabFullFacePredicate implements FullFacePredicate
{
    @Override
    public boolean test(BlockState state, Direction side)
    {
        boolean top = state.getValue(StairBlock.HALF) == Half.TOP;
        return top ? side == Direction.UP : side == Direction.DOWN;
    }
}
