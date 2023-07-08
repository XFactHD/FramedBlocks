package xfacthd.framedblocks.common.data.conpreds.door;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;

public final class TrapdoorConnectionPredicate implements ConnectionPredicate
{
    public static final TrapdoorConnectionPredicate INSTANCE = new TrapdoorConnectionPredicate();

    private TrapdoorConnectionPredicate() { }

    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        boolean open = state.getValue(BlockStateProperties.OPEN);

        Direction fullFace = facing.getOpposite();
        if (!open)
        {
            Half half = state.getValue(BlockStateProperties.HALF);
            fullFace = half == Half.BOTTOM ? Direction.DOWN : Direction.UP;
        }

        if (side == fullFace)
        {
            return true;
        }
        else if (side.getAxis() != fullFace.getAxis())
        {
            return edge == fullFace;
        }

        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        boolean open = state.getValue(BlockStateProperties.OPEN);

        Direction fullFace = facing;
        if (!open)
        {
            Half half = state.getValue(BlockStateProperties.HALF);
            fullFace = half == Half.BOTTOM ? Direction.UP : Direction.DOWN;
        }

        if (side == fullFace)
        {
            return true;
        }
        else if (side.getAxis() != fullFace.getAxis())
        {
            return edge == fullFace.getOpposite() || edge.getAxis() != fullFace.getAxis();
        }

        return false;
    }
}
