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

/// This class is machine-generated, any manual changes to this class will be overwritten.
@CullTest(BlockType.FRAMED_FENCE)
public final class FenceSkipPredicate implements SideSkipPredicate {
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType) {
            return switch (blockType) {
                case FRAMED_FENCE -> testAgainstFence(
                        state, adjState, side
                );
                case FRAMED_FENCE_GATE -> testAgainstFenceGate(
                        state, adjState, side
                );
                case FRAMED_LATTICE_BLOCK -> testAgainstLattice(
                        adjState, side
                );
                case FRAMED_POST -> testAgainstPost(
                        adjState, side
                );
                case FRAMED_PYRAMID -> testAgainstPyramid(
                        adjState, side
                );
                case FRAMED_ELEVATED_PYRAMID_SLAB -> testAgainstElevatedPyramidSlab(
                        adjState, side
                );
                case FRAMED_UPPER_PYRAMID_SLAB -> testAgainstUpperPyramidSlab(
                        adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_FENCE)
    private static boolean testAgainstFence(
            BlockState state, BlockState adjState, Direction side
    ) {
        return PillarDirs.Fence.testFenceArmDir(state, adjState, side) ||
               (PillarDirs.Fence.isPostDir(side) && PillarDirs.Fence.isPostDir(side.getOpposite()));
    }

    @CullTest.TestTarget(value = BlockType.FRAMED_FENCE_GATE, oneWay = true)
    private static boolean testAgainstFenceGate(
            BlockState state, BlockState adjState, Direction side
    ) {
        return PillarDirs.Fence.testFenceArmToGateDir(state, adjState, side);
    }

    @CullTest.TestTarget(BlockType.FRAMED_LATTICE_BLOCK)
    private static boolean testAgainstLattice(
            BlockState adjState, Direction side
    ) {
        boolean adjXAxis = adjState.getValue(FramedProperties.X_AXIS);
        boolean adjYAxis = adjState.getValue(FramedProperties.Y_AXIS);
        boolean adjZAxis = adjState.getValue(FramedProperties.Z_AXIS);

        return (PillarDirs.Fence.isPostDir(side) && PillarDirs.Lattice.isPostDir(adjXAxis, adjYAxis, adjZAxis, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_POST)
    private static boolean testAgainstPost(
            BlockState adjState, Direction side
    ) {
        Direction.Axis adjAxis = adjState.getValue(BlockStateProperties.AXIS);
        return (PillarDirs.Fence.isPostDir(side) && PillarDirs.Post.isPostDir(adjAxis, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_PYRAMID)
    private static boolean testAgainstPyramid(
            BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        PillarConnection adjConnection = adjState.getValue(PropertyHolder.PILLAR_CONNECTION);

        return (PillarDirs.Fence.isPostDir(side) && SlopeDirs.Pyramid.isPostDir(adjDir, adjConnection, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_PYRAMID_SLAB)
    private static boolean testAgainstElevatedPyramidSlab(
            BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        PillarConnection adjConnection = adjState.getValue(PropertyHolder.PILLAR_CONNECTION);

        return (PillarDirs.Fence.isPostDir(side) && SlopeDirs.ElevatedPyramidSlab.isPostDir(adjDir, adjConnection, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_UPPER_PYRAMID_SLAB)
    private static boolean testAgainstUpperPyramidSlab(
            BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        PillarConnection adjConnection = adjState.getValue(PropertyHolder.PILLAR_CONNECTION);

        return (PillarDirs.Fence.isPostDir(side) && SlopeDirs.UpperPyramidSlab.isPostDir(adjDir, adjConnection, side.getOpposite()));
    }
}
