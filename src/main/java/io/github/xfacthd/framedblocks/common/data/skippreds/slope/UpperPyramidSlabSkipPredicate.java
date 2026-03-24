package io.github.xfacthd.framedblocks.common.data.skippreds.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.PillarConnection;
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import io.github.xfacthd.framedblocks.common.data.skippreds.pillar.PillarDirs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 This class is machine-generated, any manual changes to this class will be overwritten.
 */
@CullTest(value = BlockType.FRAMED_UPPER_PYRAMID_SLAB, noSelfTest = true)
public final class UpperPyramidSlabSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(BlockStateProperties.FACING);
        PillarConnection connection = state.getValue(PropertyHolder.PILLAR_CONNECTION);
        if (SlopeDirs.UpperPyramidSlab.testEarlyExit(dir, connection, side))
        {
            return false;
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType)
        {
            return switch (blockType)
            {
                case FRAMED_WALL -> testAgainstWall(
                        dir, connection, adjState, side
                );
                case FRAMED_FENCE -> testAgainstFence(
                        dir, connection, side
                );
                case FRAMED_LATTICE_BLOCK -> testAgainstLattice(
                        dir, connection, adjState, side
                );
                case FRAMED_THICK_LATTICE -> testAgainstThickLattice(
                        dir, connection, adjState, side
                );
                case FRAMED_PILLAR -> testAgainstPillar(
                        dir, connection, adjState, side
                );
                case FRAMED_HALF_PILLAR -> testAgainstHalfPillar(
                        dir, connection, adjState, side
                );
                case FRAMED_PILLAR_SOCKET -> testAgainstPillarSocket(
                        dir, connection, adjState, side
                );
                case FRAMED_POST -> testAgainstPost(
                        dir, connection, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_WALL)
    private static boolean testAgainstWall(
            Direction dir, PillarConnection connection, BlockState adjState, Direction side
    )
    {
        boolean adjUp = adjState.getValue(BlockStateProperties.UP);
        return (SlopeDirs.UpperPyramidSlab.isPillarDir(dir, connection, side) && PillarDirs.Wall.isPillarDir(adjUp, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_FENCE)
    private static boolean testAgainstFence(
            Direction dir, PillarConnection connection, Direction side
    )
    {
        return (SlopeDirs.UpperPyramidSlab.isPostDir(dir, connection, side) && PillarDirs.Fence.isPostDir(side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_LATTICE_BLOCK)
    private static boolean testAgainstLattice(
            Direction dir, PillarConnection connection, BlockState adjState, Direction side
    )
    {
        boolean adjXAxis = adjState.getValue(FramedProperties.X_AXIS);
        boolean adjYAxis = adjState.getValue(FramedProperties.Y_AXIS);
        boolean adjZAxis = adjState.getValue(FramedProperties.Z_AXIS);

        return (SlopeDirs.UpperPyramidSlab.isPostDir(dir, connection, side) && PillarDirs.Lattice.isPostDir(adjXAxis, adjYAxis, adjZAxis, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_THICK_LATTICE)
    private static boolean testAgainstThickLattice(
            Direction dir, PillarConnection connection, BlockState adjState, Direction side
    )
    {
        boolean adjXAxis = adjState.getValue(FramedProperties.X_AXIS);
        boolean adjYAxis = adjState.getValue(FramedProperties.Y_AXIS);
        boolean adjZAxis = adjState.getValue(FramedProperties.Z_AXIS);

        return (SlopeDirs.UpperPyramidSlab.isPillarDir(dir, connection, side) && PillarDirs.ThickLattice.isPillarDir(adjXAxis, adjYAxis, adjZAxis, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_PILLAR)
    private static boolean testAgainstPillar(
            Direction dir, PillarConnection connection, BlockState adjState, Direction side
    )
    {
        Direction.Axis adjAxis = adjState.getValue(BlockStateProperties.AXIS);
        return (SlopeDirs.UpperPyramidSlab.isPillarDir(dir, connection, side) && PillarDirs.Pillar.isPillarDir(adjAxis, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HALF_PILLAR)
    private static boolean testAgainstHalfPillar(
            Direction dir, PillarConnection connection, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        return (SlopeDirs.UpperPyramidSlab.isPillarDir(dir, connection, side) && PillarDirs.HalfPillar.isPillarDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_PILLAR_SOCKET)
    private static boolean testAgainstPillarSocket(
            Direction dir, PillarConnection connection, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(BlockStateProperties.FACING);
        return (SlopeDirs.UpperPyramidSlab.isPillarDir(dir, connection, side) && PillarDirs.PillarSocket.isPillarDir(adjDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_POST)
    private static boolean testAgainstPost(
            Direction dir, PillarConnection connection, BlockState adjState, Direction side
    )
    {
        Direction.Axis adjAxis = adjState.getValue(BlockStateProperties.AXIS);
        return (SlopeDirs.UpperPyramidSlab.isPostDir(dir, connection, side) && PillarDirs.Post.isPostDir(adjAxis, side.getOpposite()));
    }
}
