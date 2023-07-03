package xfacthd.framedblocks.common.data.conpreds.slopepanel;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.ConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public final class FlatSlopePanelCornerConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        boolean front = state.getValue(PropertyHolder.FRONT);
        Direction rotDir = rot.withFacing(facing);
        Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);

        if (!front)
        {
            if (side == facing)
            {
                return true;
            }
            else if (side == rotDir.getOpposite() || side == perpRotDir.getOpposite())
            {
                return edge == facing;
            }
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        boolean front = state.getValue(PropertyHolder.FRONT);
        Direction rotDir = rot.withFacing(facing);
        Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);

        if (front && side == facing)
        {
            return true;
        }
        else if (side == rotDir.getOpposite())
        {
            return edge == perpRotDir.getOpposite();
        }
        else if (side == perpRotDir.getOpposite())
        {
            return edge == rotDir.getOpposite();
        }
        else if (side == facing.getOpposite() || side == rotDir || side == perpRotDir)
        {
            return edge == rotDir.getOpposite() || edge == perpRotDir.getOpposite();
        }
        return false;
    }
}
