package xfacthd.framedblocks.common.data.skippreds.slopeslab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.SideSkipPredicate;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.skippreds.HalfTriangleDir;

public final class FlatElevatedInnerSlopeSlabCornerSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);

        if ((top && side == Direction.UP) || (!top && side == Direction.DOWN) || side == dir || side == dir.getCounterClockWise())
        {
            return SideSkipPredicate.CTM.test(level, pos, state, adjState, side);
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType)
        {
            return switch (blockType)
            {
                case FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedInnerSlopeSlabCorner(
                        level, pos, state, dir, top, adjState, side
                );
                case FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedSlopeSlabCorner(
                        level, pos, state, dir, top, adjState, side
                );
                case FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedDoubleSlopeSlabCorner(
                        level, pos, state, dir, top, adjState, side
                );
                case FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER -> testAgainstFlatElevatedInnerDoubleSlopeSlabCorner(
                        level, pos, state, dir, top, adjState, side
                );
                case FRAMED_ELEVATED_SLOPE_SLAB -> testAgainstElevatedSlopeSlab(
                        level, pos, state, dir, top, adjState, side
                );
                case FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB -> testAgainstElevatedDoubleSlopeSlab(
                        level, pos, state, dir, top, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstFlatElevatedInnerSlopeSlabCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getTriDir(dir, top, side).isEqualTo(getTriDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstFlatElevatedSlopeSlabCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getTriDir(dir, top, side).isEqualTo(FlatElevatedSlopeSlabCornerSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstFlatElevatedDoubleSlopeSlabCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatElevatedSlopeSlabCorner(level, pos, state, dir, top, states.getA(), side) ;
    }

    private static boolean testAgainstFlatElevatedInnerDoubleSlopeSlabCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatElevatedInnerSlopeSlabCorner(level, pos, state, dir, top, states.getA(), side);
    }

    private static boolean testAgainstElevatedSlopeSlab(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getTriDir(dir, top, side).isEqualTo(ElevatedSlopeSlabSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstElevatedDoubleSlopeSlab(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstElevatedSlopeSlab(level, pos, state, dir, top, states.getA(), side);
    }



    public static HalfTriangleDir getTriDir(Direction dir, boolean top, Direction side)
    {
        if (side == dir.getOpposite() || side == dir.getClockWise())
        {
            Direction longEdge = top ? Direction.UP : Direction.DOWN;
            Direction shortEdge = side == dir.getOpposite() ? dir.getCounterClockWise() : dir;
            return HalfTriangleDir.fromDirections(longEdge, shortEdge, false);
        }
        return HalfTriangleDir.NULL;
    }
}
