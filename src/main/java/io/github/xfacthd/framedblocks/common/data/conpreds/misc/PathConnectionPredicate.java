package io.github.xfacthd.framedblocks.common.data.conpreds.misc;

import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class PathConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        return side == Direction.DOWN || (side != Direction.UP && edge == Direction.DOWN);
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        return side == Direction.UP || (side != Direction.DOWN && !DirUtils.isY(edge));
    }
}
