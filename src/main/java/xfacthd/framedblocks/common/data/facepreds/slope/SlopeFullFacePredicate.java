package xfacthd.framedblocks.common.data.facepreds.slope;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import xfacthd.framedblocks.common.block.ISlopeBlock;
import xfacthd.framedblocks.common.data.property.SlopeType;

public final class SlopeFullFacePredicate implements FullFacePredicate
{
    public static final SlopeFullFacePredicate INSTANCE = new SlopeFullFacePredicate();

    private SlopeFullFacePredicate() { }

    @Override
    public boolean test(BlockState state, Direction side)
    {
        ISlopeBlock block = (ISlopeBlock) state.getBlock();
        SlopeType type = block.getSlopeType(state);
        if (side == Direction.UP && type == SlopeType.TOP)
        {
            return true;
        }
        else if (side == Direction.DOWN && type == SlopeType.BOTTOM)
        {
            return true;
        }
        else if (type == SlopeType.HORIZONTAL)
        {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            return side == facing || side == facing.getCounterClockWise();
        }
        return block.getFacing(state) == side;
    }
}
