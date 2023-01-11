package xfacthd.framedblocks.common.data.skippreds.prism;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.SideSkipPredicate;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.skippreds.HalfDir;

public final class PrismSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(BlockStateProperties.FACING);
        if (side == dir.getOpposite())
        {
            return SideSkipPredicate.CTM.test(level, pos, state, adjState, side);
        }

        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
        if (axis == dir.getAxis() || side.getAxis() != axis) { return false; }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            return switch (type)
            {
                case FRAMED_PRISM -> testAgainstPrism(level, pos, state, dir, axis, adjState, side);
                case FRAMED_SLOPED_PRISM -> testAgainstSlopedPrism(level, pos, state, dir, axis, adjState, side);
                case FRAMED_DOUBLE_PRISM -> testAgainstDoublePrism(level, pos, state, dir, axis, adjState, side);
                case FRAMED_DOUBLE_SLOPED_PRISM -> testAgainstDoubleSlopedPrism(level, pos, state, dir, axis, adjState, side);
                default -> false;
            };
        }
        return false;
    }

    private static boolean testAgainstPrism(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, Direction.Axis axis, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        Direction.Axis adjAxis = adjState.getValue(BlockStateProperties.AXIS);

        if (getTriDir(dir, axis, side).isEqualTo(getTriDir(adjDir, adjAxis, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstSlopedPrism(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, Direction.Axis axis, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        Direction adjOrientation = adjState.getValue(PropertyHolder.ORIENTATION);

        if (getTriDir(dir, axis, side).isEqualTo(SlopedPrismSkipPredicate.getTriDir(adjDir, adjOrientation, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDoublePrism(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, Direction.Axis axis, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPrism(level, pos, state, dir, axis, states.getA(), side);
    }

    private static boolean testAgainstDoubleSlopedPrism(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, Direction.Axis axis, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopedPrism(level, pos, state, dir, axis, states.getA(), side);
    }



    public static HalfDir getTriDir(Direction dir, Direction.Axis axis, Direction side)
    {
        if (dir.getAxis() != axis && side.getAxis() == axis)
        {
            return HalfDir.fromDirections(side, dir);
        }
        return HalfDir.NULL;
    }
}
