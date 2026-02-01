package io.github.xfacthd.framedblocks.common.data.conpreds.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class SmallInnerPrismSlopePanelCornerWallConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction backDirOne = rot.withFacing(dir);
        Direction backDirTwo = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
        if (side == dir || side == backDirOne || side == backDirTwo)
        {
            return true;
        }
        if (side == dir.getOpposite())
        {
            return edge == backDirOne || edge == backDirTwo;
        }
        if (side == backDirOne.getOpposite())
        {
            return edge == dir || edge == backDirTwo;
        }
        if (side == backDirTwo.getOpposite())
        {
            return edge == dir || edge == backDirOne;
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction backDirOne = rot.withFacing(dir);
        Direction backDirTwo = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
        if (side == dir.getOpposite())
        {
            return edge == backDirOne.getOpposite() || edge == backDirTwo.getOpposite();
        }
        if (side == backDirOne.getOpposite() || side == backDirTwo.getOpposite())
        {
            return edge == dir.getOpposite();
        }
        return false;
    }
}
