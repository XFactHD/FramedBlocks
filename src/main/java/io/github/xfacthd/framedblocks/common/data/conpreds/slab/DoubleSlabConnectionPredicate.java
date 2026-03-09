package io.github.xfacthd.framedblocks.common.data.conpreds.slab;

import io.github.xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class DoubleSlabConnectionPredicate extends NonDetailedConnectionPredicate
{
    public static final DoubleSlabConnectionPredicate INSTANCE = new DoubleSlabConnectionPredicate();

    private DoubleSlabConnectionPredicate() { }

    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        return DirUtils.isY(side) || (edge != null && DirUtils.isY(edge));
    }
}
