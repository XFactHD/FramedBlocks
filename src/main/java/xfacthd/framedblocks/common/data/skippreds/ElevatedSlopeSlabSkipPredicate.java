package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.SideSkipPredicate;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class ElevatedSlopeSlabSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.CTM.test(level, pos, state, adjState, side)) { return true; }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);

            return switch (type)
            {
                case FRAMED_ELEVATED_SLOPE_SLAB -> testAgainstElevatedSlopeSlab(level, pos, dir, top, adjState, side);
                case FRAMED_SLOPE_SLAB, FRAMED_DOUBLE_SLOPE_SLAB -> testAgainstSlopeSlab(level, pos, dir, top, adjState, side);
                case FRAMED_INV_DOUBLE_SLOPE_SLAB -> testAgainstInverseDoubleSlopeSlab(level, pos, dir, top, adjState, side);
                case FRAMED_SLAB -> testAgainstSlab(level, pos, dir, top, adjState, side);
                case FRAMED_DOUBLE_SLAB -> testAgainstDoubleSlab(level, pos, dir, top, side);
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(level, pos, dir, top, adjState, side);
                case FRAMED_STAIRS -> testAgainstStairs(level, pos, dir, top, adjState, side);
                case FRAMED_VERTICAL_HALF_STAIRS -> testAgainstVerticalHalfStairs(level, pos, dir, top, adjState, side);
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstElevatedSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (adjTop != top) { return false; }

        if (side == dir.getClockWise() || side == dir.getCounterClockWise())
        {
            return adjDir == dir && SideSkipPredicate.compareState(level, pos, side);
        }
        if (side == dir.getOpposite())
        {
            return adjDir == dir.getOpposite() && SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTopHalf = adjState.getValue(PropertyHolder.TOP_HALF);

        return side == dir.getOpposite() && adjDir == dir && adjTopHalf == top && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstInverseDoubleSlopeSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (side != dir.getOpposite()) { return false; }

        return ((adjDir == dir && top) || (adjDir == dir.getOpposite() && !top)) && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return adjTop == top && side == dir.getOpposite() && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstDoubleSlab(BlockGetter level, BlockPos pos, Direction dir, boolean top, Direction side)
    {
        return side == dir.getOpposite() && SideSkipPredicate.compareState(level, pos, side, top ? Direction.UP : Direction.DOWN);
    }

    private static boolean testAgainstSlabEdge(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        return adjDir == dir && adjTop == top && side == dir.getOpposite() && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        StairsShape adjShape = adjState.getValue(BlockStateProperties.STAIRS_SHAPE);
        boolean adjTop = adjState.getValue(BlockStateProperties.HALF) == Half.TOP;

        if (adjTop != top || side != dir.getOpposite()) { return false; }

        return StairsSkipPredicate.isSlabSide(adjShape, adjDir, side.getOpposite()) && SideSkipPredicate.compareState(level, pos, side);
    }

    private static boolean testAgainstVerticalHalfStairs(BlockGetter level, BlockPos pos, Direction dir, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (side != dir.getOpposite() || adjTop != top) { return false; }

        if (adjDir == dir || adjDir == dir.getClockWise())
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    }
}
