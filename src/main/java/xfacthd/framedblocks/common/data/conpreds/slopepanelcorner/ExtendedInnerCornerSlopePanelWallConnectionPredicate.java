package xfacthd.framedblocks.common.data.conpreds.slopepanelcorner;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public final class ExtendedInnerCornerSlopePanelWallConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.withFacing(dir);
        Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);

        if (side == dir || side == rotDir.getOpposite() || side == perpRotDir.getOpposite())
        {
            return true;
        }
        else if (side == dir.getOpposite())
        {
            return edge == rotDir.getOpposite() || edge == perpRotDir.getOpposite();
        }
        else if (side == rotDir)
        {
            return edge == perpRotDir.getOpposite() || edge == dir;
        }
        else if (side == perpRotDir)
        {
            return edge == rotDir.getOpposite() || edge == dir;
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.withFacing(dir);
        Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);

        if (side == dir.getOpposite())
        {
            return edge == rotDir || edge == perpRotDir;
        }
        else if (side == rotDir)
        {
            return edge == dir.getOpposite() || edge == perpRotDir;
        }
        else if (side == perpRotDir)
        {
            return edge == dir.getOpposite() || edge == rotDir;
        }
        return false;
    }
}
