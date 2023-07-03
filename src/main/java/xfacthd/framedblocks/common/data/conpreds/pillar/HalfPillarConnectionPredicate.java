package xfacthd.framedblocks.common.data.conpreds.pillar;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.predicate.ConnectionPredicate;

public final class HalfPillarConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return side.getAxis() != facing.getAxis() && edge == facing;
    }
}
