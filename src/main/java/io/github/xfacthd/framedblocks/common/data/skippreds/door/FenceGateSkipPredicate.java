package io.github.xfacthd.framedblocks.common.data.skippreds.door;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import io.github.xfacthd.framedblocks.common.data.skippreds.pillar.PillarDirs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WallSide;

@CullTest(BlockType.FRAMED_FENCE_GATE)
public final class FenceGateSkipPredicate implements SideSkipPredicate {
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        Direction dir = state.getValue(FenceGateBlock.FACING);
        boolean perp = dir.getClockWise().getAxis() == side.getAxis();
        if (perp && adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type) {
            boolean inWall = state.getValue(FenceGateBlock.IN_WALL);

            return switch (type) {
                case FRAMED_FENCE_GATE -> testAgainstFenceGate(
                        dir, inWall, adjState
                );
                case FRAMED_WALL -> testAgainstWall(
                        inWall, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_FENCE_GATE)
    private static boolean testAgainstFenceGate(Direction dir, boolean inWall, BlockState adjState) {
        Direction adjDir = adjState.getValue(FenceGateBlock.FACING);
        return adjDir.getAxis() == dir.getAxis() && inWall == adjState.getValue(FenceGateBlock.IN_WALL);
    }

    @CullTest.TestTarget(value = BlockType.FRAMED_WALL, oneWay = true)
    private static boolean testAgainstWall(boolean inWall, BlockState adjState, Direction side) {
        return inWall && PillarDirs.Wall.getWallSide(adjState, side.getOpposite()) != WallSide.NONE;
    }
}
