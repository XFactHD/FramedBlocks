package io.github.xfacthd.framedblocks.common.data.skippreds.misc;

import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.common.block.cube.FramedChestBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;

@CullTest(BlockType.FRAMED_CHEST)
public final class ChestSkipPredicate implements SideSkipPredicate {
    @Override
    @CullTest.TestTarget(BlockType.FRAMED_CHEST)
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        if (!adjState.is(state.getBlock())) {
            return false;
        }

        ChestType type = state.getValue(BlockStateProperties.CHEST_TYPE);
        if (type == ChestType.SINGLE) {
            return false;
        }

        ChestType adjType = adjState.getValue(BlockStateProperties.CHEST_TYPE);
        if (type != adjType.getOpposite()) {
            return false;
        }

        return FramedChestBlock.getConnectionDirection(state) == side && FramedChestBlock.getConnectionDirection(adjState) == side.getOpposite();
    }
}
