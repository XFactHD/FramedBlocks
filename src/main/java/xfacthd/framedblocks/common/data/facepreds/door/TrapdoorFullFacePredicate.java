package xfacthd.framedblocks.common.data.facepreds.door;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;

public final class TrapdoorFullFacePredicate implements FullFacePredicate
{
    public static final TrapdoorFullFacePredicate INSTANCE = new TrapdoorFullFacePredicate();

    private TrapdoorFullFacePredicate() { }

    @Override
    public boolean test(BlockState state, Direction side)
    {
        if (state.getValue(BlockStateProperties.OPEN))
        {
            return state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite() == side;
        }
        else if (state.getValue(BlockStateProperties.HALF) == Half.TOP)
        {
            return side == Direction.UP;
        }
        return side == Direction.DOWN;
    }
}
