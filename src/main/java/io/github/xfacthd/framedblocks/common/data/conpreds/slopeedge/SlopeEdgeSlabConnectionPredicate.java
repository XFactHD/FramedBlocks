package io.github.xfacthd.framedblocks.common.data.conpreds.slopeedge;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class SlopeEdgeSlabConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction backFace = top ? Direction.UP : Direction.DOWN;
        boolean backFaceAligned = top == state.getValue(PropertyHolder.TOP_HALF);

        if (side == backFace)
        {
            return backFaceAligned;
        }
        else if (side == backFace.getOpposite())
        {
            return backFaceAligned && edge == dir;
        }
        else if (side == dir)
        {
            return backFaceAligned ? edge == backFace : edge == backFace.getOpposite();
        }
        else if (dir != dir.getOpposite())
        {
            return backFaceAligned && edge == backFace;
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction backFace = top ? Direction.UP : Direction.DOWN;
        boolean backFaceAligned = top == state.getValue(PropertyHolder.TOP_HALF);

        if (side == backFace)
        {
            return !backFaceAligned;
        }
        else if (side == backFace.getOpposite())
        {
            if (edge == dir)
            {
                return !backFaceAligned;
            }
            return edge != dir.getOpposite();
        }
        else if (side.getAxis() == dir.getAxis())
        {
            return !DirUtils.isY(edge);
        }
        else
        {
            if (edge == backFace.getOpposite())
            {
                return !backFaceAligned;
            }
            return edge == dir;
        }
    }
}
