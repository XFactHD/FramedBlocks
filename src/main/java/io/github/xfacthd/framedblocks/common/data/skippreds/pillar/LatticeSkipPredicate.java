package io.github.xfacthd.framedblocks.common.data.skippreds.pillar;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.PillarConnection;
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import io.github.xfacthd.framedblocks.common.data.skippreds.slope.SlopeDirs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 This class is machine-generated, any manual changes to this class will be overwritten.
 */
@CullTest(BlockType.FRAMED_LATTICE_BLOCK)
public final class LatticeSkipPredicate implements SideSkipPredicate {
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        boolean xAxis = state.getValue(FramedProperties.X_AXIS);
        boolean yAxis = state.getValue(FramedProperties.Y_AXIS);
        boolean zAxis = state.getValue(FramedProperties.Z_AXIS);
        if (PillarDirs.Lattice.testEarlyExit(xAxis, yAxis, zAxis, side)) {
            return false;
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType) {
            return switch (blockType) {
                case FRAMED_LATTICE_BLOCK -> testAgainstLattice(
                        xAxis, yAxis, zAxis, adjState, side
                );
                case FRAMED_FENCE -> testAgainstFence(
                        xAxis, yAxis, zAxis, side
                );
                case FRAMED_POST -> testAgainstPost(
                        xAxis, yAxis, zAxis, adjState, side
                );
                case FRAMED_PYRAMID -> testAgainstPyramid(
                        xAxis, yAxis, zAxis, adjState, side
                );
                case FRAMED_ELEVATED_PYRAMID_SLAB -> testAgainstElevatedPyramidSlab(
                        xAxis, yAxis, zAxis, adjState, side
                );
                case FRAMED_UPPER_PYRAMID_SLAB -> testAgainstUpperPyramidSlab(
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
    ) {
        boolean adjXAxis = adjState.getValue(FramedProperties.X_AXIS);
        boolean adjYAxis = adjState.getValue(FramedProperties.Y_AXIS);
        boolean adjZAxis = adjState.getValue(FramedProperties.Z_AXIS);

        return (PillarDirs.Lattice.isPostDir(xAxis, yAxis, zAxis, side) && PillarDirs.Lattice.isPostDir(adjXAxis, adjYAxis, adjZAxis, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FENCE)
    private static boolean testAgainstFence(
            boolean xAxis, boolean yAxis, boolean zAxis, Direction side
    ) {
        return (PillarDirs.Lattice.isPostDir(xAxis, yAxis, zAxis, side) && PillarDirs.Fence.isPostDir(side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_POST)
    private static boolean testAgainstPost(
            boolean xAxis, boolean yAxis, boolean zAxis, BlockState adjState, Direction side
    ) {
        Direction.Axis adjAxis = adjState.getValue(BlockStateProperties.AXIS);
        return (PillarDirs.Lattice.isPostDir(xAxis, yAxis, zAxis, side) && PillarDirs.Post.isPostDir(adjAxis, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_PYRAMID)
    private static boolean testAgainstPyramid(
            boolean xAxis, boolean yAxis, boolean zAxis, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        PillarConnection adjConnection = adjState.getValue(PropertyHolder.PILLAR_CONNECTION);

        return (PillarDirs.Lattice.isPostDir(xAxis, yAxis, zAxis, side) && SlopeDirs.Pyramid.isPostDir(adjDir, adjConnection, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_PYRAMID_SLAB)
    private static boolean testAgainstElevatedPyramidSlab(
            boolean xAxis, boolean yAxis, boolean zAxis, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        PillarConnection adjConnection = adjState.getValue(PropertyHolder.PILLAR_CONNECTION);

        return (PillarDirs.Lattice.isPostDir(xAxis, yAxis, zAxis, side) && SlopeDirs.ElevatedPyramidSlab.isPostDir(adjDir, adjConnection, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_UPPER_PYRAMID_SLAB)
    private static boolean testAgainstUpperPyramidSlab(
            boolean xAxis, boolean yAxis, boolean zAxis, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        PillarConnection adjConnection = adjState.getValue(PropertyHolder.PILLAR_CONNECTION);

        return (PillarDirs.Lattice.isPostDir(xAxis, yAxis, zAxis, side) && SlopeDirs.UpperPyramidSlab.isPostDir(adjDir, adjConnection, side.getOpposite()));
    }
}
