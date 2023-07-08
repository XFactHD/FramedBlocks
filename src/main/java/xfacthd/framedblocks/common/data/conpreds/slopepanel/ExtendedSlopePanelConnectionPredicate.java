package xfacthd.framedblocks.common.data.conpreds.slopepanel;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public final class ExtendedSlopePanelConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.withFacing(facing);

        if (side == facing || side == rotDir.getOpposite())
        {
            return true;
        }
        else if (side == rotDir)
        {
            return edge == facing;
        }
        else if (side.getAxis() == rotDir.getClockWise(facing.getAxis()).getAxis())
        {
            return edge == facing || edge == rotDir.getOpposite();
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.withFacing(facing);

        if (side == facing.getOpposite() || side == rotDir)
        {
            return edge.getAxis() == rotDir.getClockWise(facing.getAxis()).getAxis();
        }
        else if (side.getAxis() == rotDir.getClockWise(facing.getAxis()).getAxis())
        {
            return edge == rotDir;
        }
        return false;
    }
}
