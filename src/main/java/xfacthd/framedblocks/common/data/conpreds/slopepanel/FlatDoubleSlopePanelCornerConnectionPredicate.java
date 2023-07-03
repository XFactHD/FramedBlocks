package xfacthd.framedblocks.common.data.conpreds.slopepanel;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class FlatDoubleSlopePanelCornerConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean front = state.getValue(PropertyHolder.FRONT);
        Direction dir = front ? facing.getOpposite() : facing;

        return side == dir || (side.getAxis() != dir.getAxis() && edge == dir);
    }
}
