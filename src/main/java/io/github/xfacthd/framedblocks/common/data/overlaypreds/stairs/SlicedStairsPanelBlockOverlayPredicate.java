package io.github.xfacthd.framedblocks.common.data.overlaypreds.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;

public final class SlicedStairsPanelBlockOverlayPredicate extends AbstractVerticalStairsBlockOverlayPredicate
{
    @Override
    public boolean supportsSolid(BlockState state, Direction side, boolean secondPart)
    {
        return !secondPart || Utils.isY(side);
    }

    @Override
    public boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(BlockStateProperties.HALF) == Half.TOP;
        Direction baseDir = top ? Direction.UP : Direction.DOWN;
        return switch (state.getValue(BlockStateProperties.STAIRS_SHAPE))
        {
            case STRAIGHT ->
            {
                if (secondPart)
                {
                    yield side != facing && edge != facing;
                }
                if (side == baseDir)
                {
                    yield edge != facing.getOpposite();
                }
                if (side == facing.getOpposite())
                {
                    yield edge != baseDir;
                }
                yield true;
            }
            case INNER_LEFT ->
            {
                if (secondPart)
                {
                    if (side == facing.getOpposite() || side == facing.getClockWise())
                    {
                        yield edge == facing.getOpposite() || edge == facing.getClockWise();
                    }
                    yield side != facing && side != facing.getCounterClockWise() && edge != facing && edge != facing.getCounterClockWise();
                }
                if (side == baseDir && (edge == facing.getOpposite() || edge == facing.getClockWise()))
                {
                    yield false;
                }
                if (edge == baseDir && (side == facing.getOpposite() || side == facing.getClockWise()))
                {
                    yield false;
                }
                yield supportsEdgeVertical(state, side, edge, nullCullFace);
            }
            case INNER_RIGHT ->
            {
                if (secondPart)
                {
                    if (side == facing.getOpposite() || side == facing.getCounterClockWise())
                    {
                        yield edge == facing.getOpposite() || edge == facing.getCounterClockWise();
                    }
                    yield side != facing && side != facing.getClockWise() && edge != facing && edge != facing.getClockWise();
                }
                if (side == baseDir && (edge == facing.getOpposite() || edge == facing.getCounterClockWise()))
                {
                    yield false;
                }
                if (edge == baseDir && (side == facing.getOpposite() || side == facing.getCounterClockWise()))
                {
                    yield false;
                }
                yield supportsEdgeVertical(state, side, edge, nullCullFace);
            }
            case OUTER_LEFT ->
            {
                if (!secondPart)
                {
                    if (side == facing.getOpposite() || side == facing.getClockWise())
                    {
                        yield edge != baseDir;
                    }
                    if (side == baseDir)
                    {
                        yield edge != facing.getOpposite() && edge != facing.getClockWise();
                    }
                    yield true;
                }
                if (Utils.isY(side) && unaligned && (edge == facing || edge == facing.getCounterClockWise()))
                {
                    yield false;
                }
                if (side == facing || side == facing.getCounterClockWise())
                {
                    yield false;
                }
                yield supportsEdgeVertical(facing.getOpposite(), side, edge, nullCullFace);
            }
            case OUTER_RIGHT ->
            {
                if (!secondPart)
                {
                    if (side == facing.getOpposite() || side == facing.getCounterClockWise())
                    {
                        yield edge != baseDir;
                    }
                    if (side == baseDir)
                    {
                        yield edge != facing.getOpposite() && edge != facing.getCounterClockWise();
                    }
                    yield true;
                }
                if (Utils.isY(side) && unaligned && (edge == facing || edge == facing.getClockWise()))
                {
                    yield false;
                }
                if (side == facing || side == facing.getClockWise())
                {
                    yield false;
                }
                yield supportsEdgeVertical(facing.getClockWise(), side, edge, nullCullFace);
            }
        };
    }
}
