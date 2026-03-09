package io.github.xfacthd.framedblocks.common.data.facepreds.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class DoubleSlopeFullFacePredicate implements FullFacePredicate
{
    @Override
    public boolean test(BlockState state, Direction side)
    {
        if (state.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL)
        {
            return !DirUtils.isY(side);
        }
        else
        {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            return DirUtils.isY(side) || side == facing || side == facing.getOpposite();
        }
    }
}
