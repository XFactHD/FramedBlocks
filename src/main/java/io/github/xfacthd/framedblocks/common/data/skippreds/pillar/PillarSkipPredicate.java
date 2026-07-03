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
@CullTest(BlockType.FRAMED_PILLAR)
public final class PillarSkipPredicate implements SideSkipPredicate {
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
        if (PillarDirs.Pillar.testEarlyExit(axis, side)) {
            return false;
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType) {
            return switch (blockType) {
                case FRAMED_PILLAR -> testAgainstPillar(
                        axis, adjState, side
                );
                case FRAMED_WALL -> testAgainstWall(
                        axis, adjState, side
                );
                case FRAMED_THICK_LATTICE -> testAgainstThickLattice(
                        axis, adjState, side
                );
                case FRAMED_HALF_PILLAR -> testAgainstHalfPillar(
                        axis, adjState, side
                );
                case FRAMED_PILLAR_SOCKET -> testAgainstPillarSocket(
                        axis, adjState, side
                );
                case FRAMED_PYRAMID -> testAgainstPyramid(
                        axis, adjState, side
                );
                case FRAMED_ELEVATED_PYRAMID_SLAB -> testAgainstElevatedPyramidSlab(
                        axis, adjState, side
                );
                case FRAMED_UPPER_PYRAMID_SLAB -> testAgainstUpperPyramidSlab(
                        axis, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_PILLAR)
    private static boolean testAgainstPillar(
            Direction.Axis axis, BlockState adjState, Direction side
    ) {
        Direction.Axis adjAxis = adjState.getValue(BlockStateProperties.AXIS);
        return (PillarDirs.Pillar.isPillarDir(axis, side) && PillarDirs.Pillar.isPillarDir(adjAxis, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_WALL)
    private static boolean testAgainstWall(
            Direction.Axis axis, BlockState adjState, Direction side
    ) {
        boolean adjUp = adjState.getValue(BlockStateProperties.UP);
        return (PillarDirs.Pillar.isPillarDir(axis, side) && PillarDirs.Wall.isPillarDir(adjUp, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_THICK_LATTICE)
    private static boolean testAgainstThickLattice(
            Direction.Axis axis, BlockState adjState, Direction side
    ) {
        boolean adjXAxis = adjState.getValue(FramedProperties.X_AXIS);
        boolean adjYAxis = adjState.getValue(FramedProperties.Y_AXIS);
        boolean adjZAxis = adjState.getValue(FramedProperties.Z_AXIS);

        return (PillarDirs.Pillar.isPillarDir(axis, side) && PillarDirs.ThickLattice.isPillarDir(adjXAxis, adjYAxis, adjZAxis, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_PILLAR)
    private static boolean testAgainstHalfPillar(
            Direction.Axis axis, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        return (PillarDirs.Pillar.isPillarDir(axis, side) && PillarDirs.HalfPillar.isPillarDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_PILLAR_SOCKET)
    private static boolean testAgainstPillarSocket(
            Direction.Axis axis, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        return (PillarDirs.Pillar.isPillarDir(axis, side) && PillarDirs.PillarSocket.isPillarDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_PYRAMID)
    private static boolean testAgainstPyramid(
            Direction.Axis axis, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        PillarConnection adjConnection = adjState.getValue(PropertyHolder.PILLAR_CONNECTION);

        return (PillarDirs.Pillar.isPillarDir(axis, side) && SlopeDirs.Pyramid.isPillarDir(adjDir, adjConnection, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_PYRAMID_SLAB)
    private static boolean testAgainstElevatedPyramidSlab(
            Direction.Axis axis, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        PillarConnection adjConnection = adjState.getValue(PropertyHolder.PILLAR_CONNECTION);

        return (PillarDirs.Pillar.isPillarDir(axis, side) && SlopeDirs.ElevatedPyramidSlab.isPillarDir(adjDir, adjConnection, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_UPPER_PYRAMID_SLAB)
    private static boolean testAgainstUpperPyramidSlab(
            Direction.Axis axis, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        PillarConnection adjConnection = adjState.getValue(PropertyHolder.PILLAR_CONNECTION);

        return (PillarDirs.Pillar.isPillarDir(axis, side) && SlopeDirs.UpperPyramidSlab.isPillarDir(adjDir, adjConnection, side.getOpposite()));
    }
}
