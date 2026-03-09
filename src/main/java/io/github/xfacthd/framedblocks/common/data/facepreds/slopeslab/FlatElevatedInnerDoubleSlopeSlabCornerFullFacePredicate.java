package io.github.xfacthd.framedblocks.common.data.facepreds.slopeslab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class FlatElevatedInnerDoubleSlopeSlabCornerFullFacePredicate implements FullFacePredicate
{
    @Override
    public boolean test(BlockState state, Direction side)
    {
        if (DirUtils.isY(side))
        {
            return true;
        }

        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        return side == facing || side == facing.getCounterClockWise();
    }
}
