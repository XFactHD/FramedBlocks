package xfacthd.framedblocks.common.data.skippreds.pillar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.skippreds.CullTest;

@CullTest(BlockType.FRAMED_PILLAR)
public final class PillarSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
        if (axis == side.getAxis() && adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            return switch (type)
            {
                case FRAMED_PILLAR -> testAgainstPillar(
                        axis, adjState
                );
                case FRAMED_HALF_PILLAR -> testAgainstHalfPillar(
                        adjState, side
                );
                case FRAMED_WALL -> testAgainstWall(
                        axis, adjState
                );
                case FRAMED_THICK_LATTICE -> testAgainstThickLattice(
                        axis, adjState
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_PILLAR)
    private static boolean testAgainstPillar(Direction.Axis axis, BlockState adjState)
    {
        return axis == adjState.getValue(BlockStateProperties.AXIS);
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_PILLAR)
    private static boolean testAgainstHalfPillar(BlockState adjState, Direction side)
    {
        return adjState.getValue(BlockStateProperties.FACING) == side.getOpposite();
    }

    @CullTest.TestTarget(BlockType.FRAMED_WALL)
    private static boolean testAgainstWall(Direction.Axis axis, BlockState adjState)
    {
        return axis == Direction.Axis.Y && adjState.getValue(BlockStateProperties.UP);
    }

    @CullTest.TestTarget(BlockType.FRAMED_THICK_LATTICE)
    private static boolean testAgainstThickLattice(Direction.Axis axis, BlockState adjState)
    {
        return switch (axis)
        {
            case X -> adjState.getValue(FramedProperties.X_AXIS);
            case Y -> adjState.getValue(FramedProperties.Y_AXIS);
            case Z -> adjState.getValue(FramedProperties.Z_AXIS);
        };
    }
}