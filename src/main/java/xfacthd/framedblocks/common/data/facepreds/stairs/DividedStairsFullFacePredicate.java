package xfacthd.framedblocks.common.data.facepreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StairsShape;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;

public final class DividedStairsFullFacePredicate implements FullFacePredicate
{
    @Override
    public boolean test(BlockState state, Direction side)
    {
        Direction facing = state.getValue(StairBlock.FACING);
        StairsShape shape = state.getValue(StairBlock.SHAPE);
        if (shape == StairsShape.INNER_LEFT)
        {
            return side == facing.getCounterClockWise();
        }
        if (shape == StairsShape.INNER_RIGHT)
        {
            return side == facing.getClockWise();
        }
        return false;
    }
}
