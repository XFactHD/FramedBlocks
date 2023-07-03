package xfacthd.framedblocks.common.data.conpreds.slopeslab;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;

public final class ElevatedDoubleSlopeSlabConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (side == facing || Utils.isY(side))
        {
            return true;
        }
        else if (side.getAxis() == facing.getClockWise().getAxis())
        {
            return edge != null && edge != facing.getOpposite();
        }
        return edge != null && Utils.isY(edge);
    }
}
