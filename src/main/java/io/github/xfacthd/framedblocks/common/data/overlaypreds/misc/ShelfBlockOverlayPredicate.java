package io.github.xfacthd.framedblocks.common.data.overlaypreds.misc;

import io.github.xfacthd.framedblocks.api.predicate.overlay.AlwaysSolidBlockOverlayPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ShelfBlockOverlayPredicate extends AlwaysSolidBlockOverlayPredicate {
    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned) {
        if (nullCullFace && DirUtils.isY(side)) {
            return edge != state.getValue(ShelfBlock.FACING).getOpposite();
        }
        return true;
    }
}
