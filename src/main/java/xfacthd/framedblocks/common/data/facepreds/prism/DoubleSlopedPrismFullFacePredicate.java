package xfacthd.framedblocks.common.data.facepreds.prism;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.predicate.FullFacePredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class DoubleSlopedPrismFullFacePredicate implements FullFacePredicate
{
    @Override
    public boolean test(BlockState state, Direction side)
    {
        return side != state.getValue(PropertyHolder.FACING_DIR).orientation();
    }
}
