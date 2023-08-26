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

@CullTest(BlockType.FRAMED_THICK_LATTICE)
public final class ThickLatticeSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            boolean xAxis = state.getValue(FramedProperties.X_AXIS);
            boolean yAxis = state.getValue(FramedProperties.Y_AXIS);
            boolean zAxis = state.getValue(FramedProperties.Z_AXIS);

            return switch (type)
            {
                case FRAMED_THICK_LATTICE -> testAgainstThickLattice(xAxis, yAxis, zAxis, adjState, side);
                case FRAMED_WALL -> testAgainstWall(yAxis, adjState, side);
                case FRAMED_PILLAR -> testAgainstPillar(xAxis, yAxis, zAxis, adjState, side);
                case FRAMED_HALF_PILLAR -> testAgainstHalfPillar(xAxis, yAxis, zAxis, adjState, side);
                default -> false;
            };
        }
        return false;
    }

    @CullTest.SingleTarget(BlockType.FRAMED_THICK_LATTICE)
    private static boolean testAgainstThickLattice(
            boolean xAxis, boolean yAxis, boolean zAxis, BlockState adjState, Direction side
    )
    {
        return switch (side.getAxis())
        {
            case X -> xAxis && adjState.getValue(FramedProperties.X_AXIS);
            case Y -> yAxis && adjState.getValue(FramedProperties.Y_AXIS);
            case Z -> zAxis && adjState.getValue(FramedProperties.Z_AXIS);
        };
    }

    @CullTest.SingleTarget(BlockType.FRAMED_WALL)
    private static boolean testAgainstWall(
            boolean yAxis, BlockState adjState, Direction side
    )
    {
        if (yAxis && Utils.isY(side))
        {
            return adjState.getValue(BlockStateProperties.UP);
        }
        return false;
    }

    @CullTest.SingleTarget(BlockType.FRAMED_PILLAR)
    private static boolean testAgainstPillar(
            boolean xAxis, boolean yAxis, boolean zAxis, BlockState adjState, Direction side
    )
    {
        Direction.Axis adjAxis = adjState.getValue(BlockStateProperties.AXIS);
        return adjAxis == side.getAxis() && switch (adjAxis)
        {
            case X -> xAxis;
            case Y -> yAxis;
            case Z -> zAxis;
        };
    }

    @CullTest.SingleTarget(BlockType.FRAMED_HALF_PILLAR)
    private static boolean testAgainstHalfPillar(
            boolean xAxis, boolean yAxis, boolean zAxis, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        return side == adjDir.getOpposite() && switch (adjDir.getAxis())
        {
            case X -> xAxis;
            case Y -> yAxis;
            case Z -> zAxis;
        };
    }
}
