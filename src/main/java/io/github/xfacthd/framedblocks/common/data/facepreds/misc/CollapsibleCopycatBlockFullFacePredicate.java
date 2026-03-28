package io.github.xfacthd.framedblocks.common.data.facepreds.misc;

import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.common.block.cube.FramedCollapsibleCopycatBlock;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class CollapsibleCopycatBlockFullFacePredicate implements FullFacePredicate {
    @Override
    public boolean test(BlockState state, Direction side) {
        int solid = state.getValue(PropertyHolder.SOLID_FACES);
        int mask = ~(1 << side.getOpposite().ordinal());
        return (solid & mask) == (mask & FramedCollapsibleCopycatBlock.ALL_SOLID);
    }
}
