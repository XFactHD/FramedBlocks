package xfacthd.framedblocks.common.data.skippreds.pillar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.skippreds.CullTest;

@CullTest(BlockType.FRAMED_LATTICE_BLOCK)
public final class LatticeSkipPredicate implements SideSkipPredicate
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
                case FRAMED_LATTICE_BLOCK -> testAgainstLattice(
                        xAxis, yAxis, zAxis, adjState, side
                );
                case FRAMED_FENCE -> testAgainstWall(
                        yAxis, side
                );
                case FRAMED_POST -> testAgainstPost(
                        xAxis, yAxis, zAxis, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_LATTICE_BLOCK)
    private static boolean testAgainstLattice(
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

    @CullTest.TestTarget(BlockType.FRAMED_FENCE)
    private static boolean testAgainstWall(boolean yAxis, Direction side)
    {
        return yAxis && Utils.isY(side);
    }

    @CullTest.TestTarget(BlockType.FRAMED_POST)
    private static boolean testAgainstPost(
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
}
