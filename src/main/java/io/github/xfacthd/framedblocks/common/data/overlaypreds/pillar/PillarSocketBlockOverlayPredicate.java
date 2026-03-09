package io.github.xfacthd.framedblocks.common.data.overlaypreds.pillar;

import io.github.xfacthd.framedblocks.api.predicate.overlay.AlwaysSolidBlockOverlayPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class PillarSocketBlockOverlayPredicate extends AlwaysSolidBlockOverlayPredicate
{
    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned)
    {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return !nullCullFace || edge != facing;
    }
}
