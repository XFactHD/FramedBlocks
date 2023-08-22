package xfacthd.framedblocks.common.data.conpreds.slopepanel;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public final class FlatExtendedSlopePanelCornerConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.withFacing(facing);
        Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);

        if (side == facing)
        {
            return true;
        }
        else if (side == rotDir || side == perpRotDir)
        {
            return edge == facing;
        }
        else if (side == rotDir.getOpposite())
        {
            return edge == facing || edge == perpRotDir.getOpposite();
        }
        else if (side == perpRotDir.getOpposite())
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
        Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);

        if (side == rotDir || side == perpRotDir)
        {
            return edge.getAxis() != facing.getAxis();
        }
        else if (side == rotDir.getOpposite())
        {
            return edge == perpRotDir;
        }
        else if (side == perpRotDir.getOpposite())
        {
            return edge == rotDir;
        }
        else if (side == facing.getOpposite())
        {
            return true;
        }
        return false;
    }
}
