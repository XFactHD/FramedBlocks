package xfacthd.framedblocks.common.data.facepreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;

public final class StairsFullFacePredicate implements FullFacePredicate
{
    @Override
    public boolean test(BlockState state, Direction side)
    {
        if (side == Direction.UP)
        {
            return state.getValue(BlockStateProperties.HALF) == Half.TOP;
        }
        else if (side == Direction.DOWN)
        {
            return state.getValue(BlockStateProperties.HALF) == Half.BOTTOM;
        }
        else
        {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            StairsShape shape = state.getValue(BlockStateProperties.STAIRS_SHAPE);
            if (shape == StairsShape.STRAIGHT)
            {
                return facing == side;
            }
            else if (shape == StairsShape.INNER_LEFT)
            {
                return facing == side || facing.getCounterClockWise() == side;
            }
            else if (shape == StairsShape.INNER_RIGHT)
            {
                return facing == side || facing.getClockWise() == side;
            }
            else
            {
                return false;
            }
        }
    }
}
