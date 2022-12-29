package xfacthd.framedblocks.common.data.skippreds.prism;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.SideSkipPredicate;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class PrismSkipPredicate implements SideSkipPredicate
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
        if (side.getAxis() == axis && adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            return switch (type)
            {
                case FRAMED_PRISM -> testAgainstPrism(level, pos, dir, axis, adjState, side);
                case FRAMED_SLOPED_PRISM -> testAgainstSlopedPrism(level, pos, dir, adjState, side);
                case FRAMED_DOUBLE_PRISM -> testAgainstDoublePrism(level, pos, dir, axis, adjState, side);
                case FRAMED_DOUBLE_SLOPED_PRISM -> testAgainstDoubleSlopedPrism(level, pos, dir, adjState, side);
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstPrism(BlockGetter level, BlockPos pos, Direction dir, Direction.Axis axis, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        Direction.Axis adjAxis = adjState.getValue(BlockStateProperties.AXIS);

        Direction camoDir = dir.getOpposite();
        return adjDir == dir && adjAxis == axis && SideSkipPredicate.compareState(level, pos, side, camoDir, camoDir);
    }

    private static boolean testAgainstSlopedPrism(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        Direction adjOrientation = adjState.getValue(PropertyHolder.ORIENTATION);

        Direction camoDir = dir.getOpposite();
        return adjDir == dir && adjOrientation == side.getOpposite() && SideSkipPredicate.compareState(level, pos, side, camoDir, camoDir);
    }

    private static boolean testAgainstDoublePrism(BlockGetter level, BlockPos pos, Direction dir, Direction.Axis axis, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        Direction.Axis adjAxis = adjState.getValue(BlockStateProperties.AXIS);

        Direction camoDir = dir.getOpposite();
        return adjDir == dir.getOpposite() && adjAxis == axis && SideSkipPredicate.compareState(level, pos, side, camoDir, camoDir);
    }

    private static boolean testAgainstDoubleSlopedPrism(BlockGetter level, BlockPos pos, Direction dir, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        Direction adjOrientation = adjState.getValue(PropertyHolder.ORIENTATION);

        Direction camoDir = dir.getOpposite();
        return adjDir == dir.getOpposite() && adjOrientation == side.getOpposite() && SideSkipPredicate.compareState(level, pos, side, camoDir, camoDir);
    }
}
