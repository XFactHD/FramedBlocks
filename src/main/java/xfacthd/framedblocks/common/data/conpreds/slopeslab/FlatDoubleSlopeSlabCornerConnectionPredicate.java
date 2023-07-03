package xfacthd.framedblocks.common.data.conpreds.slopeslab;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.predicate.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class FlatDoubleSlopeSlabCornerConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        boolean top = state.getValue(PropertyHolder.TOP_HALF);
        Direction dir = top ? Direction.UP : Direction.DOWN;

        return side == dir || (!Utils.isY(side) && edge == dir);
    }
}
