package xfacthd.framedblocks.api.predicate.contex;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.IFramedBlock;

final class FullFaceConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        return ((IFramedBlock) state.getBlock()).getFullFacePredicate().test(state, side);
    }
}
