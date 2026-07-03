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
@CullTest(BlockType.FRAMED_WALL)
public final class WallSkipPredicate implements SideSkipPredicate {
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType) {
            boolean up = state.getValue(BlockStateProperties.UP);

            return switch (blockType) {
                case FRAMED_WALL -> testAgainstWall(
                        state, up, adjState, side
                );
                case FRAMED_THICK_LATTICE -> testAgainstThickLattice(
                        up, adjState, side
                );
                case FRAMED_PILLAR -> testAgainstPillar(
                        up, adjState, side
                );
                case FRAMED_HALF_PILLAR -> testAgainstHalfPillar(
                        up, adjState, side
                );
                case FRAMED_PILLAR_SOCKET -> testAgainstPillarSocket(
                        up, adjState, side
                );
                case FRAMED_PYRAMID -> testAgainstPyramid(
                        up, adjState, side
                );
                case FRAMED_ELEVATED_PYRAMID_SLAB -> testAgainstElevatedPyramidSlab(
                        up, adjState, side
                );
                case FRAMED_UPPER_PYRAMID_SLAB -> testAgainstUpperPyramidSlab(
                        up, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_WALL)
    private static boolean testAgainstWall(
            BlockState state, boolean up, BlockState adjState, Direction side
    ) {
        boolean adjUp = adjState.getValue(BlockStateProperties.UP);
        return PillarDirs.Wall.testWallArmDir(state, adjState, side) ||
               PillarDirs.Wall.testWallProfileDir(state, up, adjState, adjUp, side);
    }

    @CullTest.TestTarget(BlockType.FRAMED_THICK_LATTICE)
    private static boolean testAgainstThickLattice(
            boolean up, BlockState adjState, Direction side
    ) {
        boolean adjXAxis = adjState.getValue(FramedProperties.X_AXIS);
        boolean adjYAxis = adjState.getValue(FramedProperties.Y_AXIS);
        boolean adjZAxis = adjState.getValue(FramedProperties.Z_AXIS);

        return (PillarDirs.Wall.isPillarDir(up, side) && PillarDirs.ThickLattice.isPillarDir(adjXAxis, adjYAxis, adjZAxis, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_PILLAR)
    private static boolean testAgainstPillar(
            boolean up, BlockState adjState, Direction side
    ) {
        Direction.Axis adjAxis = adjState.getValue(BlockStateProperties.AXIS);
        return (PillarDirs.Wall.isPillarDir(up, side) && PillarDirs.Pillar.isPillarDir(adjAxis, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_PILLAR)
    private static boolean testAgainstHalfPillar(
            boolean up, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        return (PillarDirs.Wall.isPillarDir(up, side) && PillarDirs.HalfPillar.isPillarDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_PILLAR_SOCKET)
    private static boolean testAgainstPillarSocket(
            boolean up, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        return (PillarDirs.Wall.isPillarDir(up, side) && PillarDirs.PillarSocket.isPillarDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_PYRAMID)
    private static boolean testAgainstPyramid(
            boolean up, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        PillarConnection adjConnection = adjState.getValue(PropertyHolder.PILLAR_CONNECTION);

        return (PillarDirs.Wall.isPillarDir(up, side) && SlopeDirs.Pyramid.isPillarDir(adjDir, adjConnection, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_ELEVATED_PYRAMID_SLAB)
    private static boolean testAgainstElevatedPyramidSlab(
            boolean up, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        PillarConnection adjConnection = adjState.getValue(PropertyHolder.PILLAR_CONNECTION);

        return (PillarDirs.Wall.isPillarDir(up, side) && SlopeDirs.ElevatedPyramidSlab.isPillarDir(adjDir, adjConnection, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_UPPER_PYRAMID_SLAB)
    private static boolean testAgainstUpperPyramidSlab(
            boolean up, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        PillarConnection adjConnection = adjState.getValue(PropertyHolder.PILLAR_CONNECTION);

        return (PillarDirs.Wall.isPillarDir(up, side) && SlopeDirs.UpperPyramidSlab.isPillarDir(adjDir, adjConnection, side.getOpposite()));
    }
}
