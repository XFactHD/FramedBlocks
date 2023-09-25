package xfacthd.framedblocks.common.data.conpreds.misc;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;

public final class ChiseledBookshelfConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        return side != state.getValue(FramedProperties.FACING_HOR) || edge != null;
    }
}
