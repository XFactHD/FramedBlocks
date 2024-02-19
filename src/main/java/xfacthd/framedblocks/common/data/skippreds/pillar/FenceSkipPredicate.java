package xfacthd.framedblocks.common.data.skippreds.pillar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.skippreds.CullTest;

@CullTest(BlockType.FRAMED_FENCE)
public final class FenceSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            return switch (type)
            {
                case FRAMED_FENCE -> testAgainstFence(
                        state, adjState, side
                );
                case FRAMED_FENCE_GATE -> testAgainstFenceGate(
                        state, adjState, side
                );
                case FRAMED_POST -> testAgainstPost(
                        adjState, side
                );
                case FRAMED_LATTICE_BLOCK -> testAgainstLattice(
                        adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_FENCE)
    private static boolean testAgainstFence(BlockState state, BlockState adjState, Direction side)
    {
        return Utils.isY(side) || (hasFenceArm(state, side) && hasFenceArm(adjState, side.getOpposite()));
    }

    @CullTest.TestTarget(value = BlockType.FRAMED_FENCE_GATE, oneWay = true)
    private static boolean testAgainstFenceGate(BlockState state, BlockState adjState, Direction side)
    {
        if (!Utils.isY(side) && hasFenceArm(state, side))
        {
            Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
            return adjDir.getClockWise().getAxis() == side.getAxis();
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_POST)
    private static boolean testAgainstPost(BlockState adjState, Direction side)
    {
        return Utils.isY(side) && adjState.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y;
    }

    @CullTest.TestTarget(BlockType.FRAMED_LATTICE_BLOCK)
    private static boolean testAgainstLattice(BlockState adjState, Direction side)
    {
        return Utils.isY(side) && adjState.getValue(FramedProperties.Y_AXIS);
    }



    private static boolean hasFenceArm(BlockState state, Direction side)
    {
        return switch (side)
        {
            case NORTH -> state.getValue(FenceBlock.NORTH);
            case EAST -> state.getValue(FenceBlock.EAST);
            case SOUTH -> state.getValue(FenceBlock.SOUTH);
            case WEST -> state.getValue(FenceBlock.WEST);
            default -> throw new IllegalArgumentException("Invalid fence arm side: " + side);
        };
    }
}
