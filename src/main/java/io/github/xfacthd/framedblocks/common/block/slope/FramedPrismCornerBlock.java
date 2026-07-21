package io.github.xfacthd.framedblocks.common.block.slope;

import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.PrismCornerBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedPrismCornerBlock extends FramedThreewayCornerBlock implements PrismCornerBlock {
    private final boolean offsetOnOddPos;

    public FramedPrismCornerBlock(BlockType type, Properties props) {
        super(type, props);
        this.offsetOnOddPos = type == BlockType.FRAMED_PRISM_CORNER;
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.OFFSET, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.OFFSET);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state != null ? applyOffset(state, context) : null;
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player) {
        if (player.getMainHandItem().getItem() == FBContent.ITEM_FRAMED_HAMMER.value()) {
            level.setBlockAndUpdate(pos, state.setValue(PropertyHolder.OFFSET, !state.getValue(PropertyHolder.OFFSET)));
            return true;
        }
        return super.handleBlockLeftClick(state, level, pos, player);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return super.getJadeRenderState(state).setValue(PropertyHolder.OFFSET, state.getValue(PropertyHolder.OFFSET));
    }

    @Override
    public boolean isOffsetOnOddPos() {
        return offsetOnOddPos;
    }
}
