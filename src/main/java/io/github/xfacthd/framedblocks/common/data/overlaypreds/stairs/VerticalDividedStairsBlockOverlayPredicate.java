package io.github.xfacthd.framedblocks.common.data.overlaypreds.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.StairsType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class VerticalDividedStairsBlockOverlayPredicate extends AbstractVerticalStairsBlockOverlayPredicate
{
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart)
    {
        return switch (state.getValue(PropertyHolder.STAIRS_TYPE))
        {
            case VERTICAL -> secondPart ? side != Direction.DOWN : side != Direction.UP;
            case TOP_FWD, TOP_BOTH, TOP_CCW -> !secondPart || side != Direction.DOWN;
            case BOTTOM_FWD, BOTTOM_CCW, BOTTOM_BOTH -> secondPart || side != Direction.UP;
        };
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);
        boolean top = type.isTop();
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;
        return switch (type)
        {
            case VERTICAL ->
            {
                if ((nullCullFace && DirUtils.isY(side)) || (!secondPart && edge == Direction.UP) || (secondPart && edge == Direction.DOWN))
                {
                    yield false;
                }
                yield supportsEdgeVertical(state, side, edge, nullCullFace);
            }
            case TOP_FWD, BOTTOM_FWD ->
            {
                if (secondPart == top)
                {
                    yield side != dirTwo.getOpposite() && edge != dirTwo.getOpposite();
                }
                if (edge == facing.getCounterClockWise() || (!unaligned && edge == facing.getOpposite()))
                {
                    yield false;
                }
                if (edge == dirTwo && ((!nullCullFace && side == facing.getOpposite()) || (nullCullFace && side == facing.getClockWise())))
                {
                    yield false;
                }
                yield supportsEdgeVertical(state, side, edge, nullCullFace);
            }
            case TOP_CCW, BOTTOM_CCW ->
            {
                if (secondPart == top)
                {
                    yield side != dirTwo.getOpposite() && edge != dirTwo.getOpposite();
                }
                if (edge == facing || (!unaligned && edge == facing.getClockWise()))
                {
                    yield false;
                }
                if (edge == dirTwo && ((!nullCullFace && side == facing.getClockWise()) || (nullCullFace && side == facing.getOpposite())))
                {
                    yield false;
                }
                yield supportsEdgeVertical(state, side, edge, nullCullFace);
            }
            case TOP_BOTH, BOTTOM_BOTH ->
            {
                if (secondPart == top)
                {
                    yield side != dirTwo.getOpposite() && edge != dirTwo.getOpposite();
                }
                if (unaligned && edge == dirTwo && (side == facing || side == facing.getCounterClockWise()))
                {
                    yield false;
                }
                yield supportsEdgeVertical(state, side, edge, nullCullFace);
            }
        };
    }
}
