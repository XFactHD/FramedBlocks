package xfacthd.framedblocks.common.data.conpreds.misc;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;

public final class BookshelfConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        return Utils.isY(side) || edge != null;
    }
}
