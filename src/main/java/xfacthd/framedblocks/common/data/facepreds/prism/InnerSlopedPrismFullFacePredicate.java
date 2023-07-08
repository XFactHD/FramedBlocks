package xfacthd.framedblocks.common.data.facepreds.prism;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CompoundDirection;

public final class InnerSlopedPrismFullFacePredicate implements FullFacePredicate
{
    @Override
    public boolean test(BlockState state, Direction side)
    {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        return side != cmpDir.direction() && side != cmpDir.orientation();
    }
}
