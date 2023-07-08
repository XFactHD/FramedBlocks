package xfacthd.framedblocks.common.data.conpreds.pane;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;

public final class PaneConnectionPredicate implements ConnectionPredicate
{
    public static final PaneConnectionPredicate INSTANCE = new PaneConnectionPredicate();

    private PaneConnectionPredicate() { }

    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        return switch (edge)
        {
            case UP, DOWN -> true;
            case NORTH -> state.getValue(BlockStateProperties.NORTH);
            case SOUTH -> state.getValue(BlockStateProperties.SOUTH);
            case WEST -> state.getValue(BlockStateProperties.WEST);
            case EAST -> state.getValue(BlockStateProperties.EAST);
        };
    }
}
