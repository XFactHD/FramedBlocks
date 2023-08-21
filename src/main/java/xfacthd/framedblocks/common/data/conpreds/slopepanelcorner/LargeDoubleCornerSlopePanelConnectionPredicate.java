package xfacthd.framedblocks.common.data.conpreds.slopepanelcorner;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;

public final class LargeDoubleCornerSlopePanelConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);

        if (side == dir.getOpposite() || side == dir.getClockWise())
        {
            return true;
        }
        else if (Utils.isY(side))
        {
            return edge == dir.getOpposite() || edge == dir.getClockWise();
        }
        else if (side == dir)
        {
            return edge == dir.getClockWise();
        }
        else if (side == dir.getCounterClockWise())
        {
            return edge == dir.getOpposite();
        }
        return false;
    }
}
