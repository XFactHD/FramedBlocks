package xfacthd.framedblocks.common.data.skippreds.door;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WallSide;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.skippreds.CullTest;
import xfacthd.framedblocks.common.data.skippreds.pillar.WallSkipPredicate;

@CullTest(BlockType.FRAMED_FENCE_GATE)
public final class FenceGateSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(FenceGateBlock.FACING);
        boolean perp = dir.getClockWise().getAxis() == side.getAxis();
        if (perp && adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            boolean inWall = state.getValue(FenceGateBlock.IN_WALL);

            return switch (type)
            {
                case FRAMED_FENCE_GATE -> testAgainstFenceGate(dir, inWall, adjState);
                case FRAMED_WALL -> testAgainstWall(inWall, adjState, side);
                default -> false;
            };
        }
        return false;
    }

    @CullTest.SingleTarget(BlockType.FRAMED_FENCE_GATE)
    private static boolean testAgainstFenceGate(Direction dir, boolean inWall, BlockState adjState)
    {
        Direction adjDir = adjState.getValue(FenceGateBlock.FACING);
        return adjDir.getAxis() == dir.getAxis() && inWall == adjState.getValue(FenceGateBlock.IN_WALL);
    }

    @CullTest.SingleTarget(value = BlockType.FRAMED_WALL, oneWay = true)
    private static boolean testAgainstWall(boolean inWall, BlockState adjState, Direction side)
    {
        return inWall && WallSkipPredicate.getArm(adjState, side.getOpposite()) != WallSide.NONE;
    }
}
