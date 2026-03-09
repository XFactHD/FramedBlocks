package io.github.xfacthd.framedblocks.api.predicate.overlay;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AlwaysSolidBlockOverlayPredicate implements BlockOverlayPredicate
{
    @Override
    public final boolean supportsSolid(BlockState state, Direction side, boolean secondPart)
    {
        return true;
    }
}
