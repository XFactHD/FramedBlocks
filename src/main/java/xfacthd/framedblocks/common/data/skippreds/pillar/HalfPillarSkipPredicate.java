package xfacthd.framedblocks.common.data.skippreds.pillar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.skippreds.CullTest;

@CullTest(BlockType.FRAMED_HALF_PILLAR)
public final class HalfPillarSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction face = state.getValue(BlockStateProperties.FACING);
        if (side == face && adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            return switch (type)
            {
                case FRAMED_HALF_PILLAR -> testAgainstHalfPillar(adjState, side);
                case FRAMED_PILLAR -> testAgainstPillar(adjState, side);
                case FRAMED_WALL -> testAgainstWall(adjState, side);
                case FRAMED_THICK_LATTICE -> testAgainstThickLattice(adjState, side);
                default -> false;
            };
        }
        return false;
    }

    @CullTest.SingleTarget(BlockType.FRAMED_HALF_PILLAR)
    private static boolean testAgainstHalfPillar(BlockState adjState, Direction side)
    {
        return adjState.getValue(BlockStateProperties.FACING) == side.getOpposite();
    }

    @CullTest.SingleTarget(BlockType.FRAMED_PILLAR)
    private static boolean testAgainstPillar(BlockState adjState, Direction side)
    {
        return adjState.getValue(BlockStateProperties.AXIS) == side.getAxis();
    }

    @CullTest.SingleTarget(BlockType.FRAMED_WALL)
    private static boolean testAgainstWall(BlockState adjState, Direction side)
    {
        return Utils.isY(side) && adjState.getValue(BlockStateProperties.UP);
    }

    @CullTest.SingleTarget(BlockType.FRAMED_THICK_LATTICE)
    private static boolean testAgainstThickLattice(BlockState adjState, Direction side)
    {
        return switch (side.getAxis())
        {
            case X -> adjState.getValue(FramedProperties.X_AXIS);
            case Y -> adjState.getValue(FramedProperties.Y_AXIS);
            case Z -> adjState.getValue(FramedProperties.Z_AXIS);
        };
    }
}