package io.github.xfacthd.framedblocks.common.block.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.PrismCornerBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedDoublePrismCornerBlock extends FramedDoubleThreewayCornerBlock implements PrismCornerBlock {
    public FramedDoublePrismCornerBlock(Properties props) {
        super(BlockType.FRAMED_DOUBLE_PRISM_CORNER, props);
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
    public DoubleBlockParts calculateParts(BlockState state) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean offset = state.getValue(PropertyHolder.OFFSET);
        boolean altSlope = state.getValue(FramedProperties.ALT_SLOPE);

        return new DoubleBlockParts(
                FBContent.BLOCK_FRAMED_INNER_PRISM_CORNER.value().defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(FramedProperties.TOP, top)
                        .setValue(PropertyHolder.OFFSET, offset)
                        .setValue(FramedProperties.ALT_SLOPE, altSlope),
                FBContent.BLOCK_FRAMED_PRISM_CORNER.value().defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(FramedProperties.TOP, !top)
                        .setValue(PropertyHolder.OFFSET, !offset)
                        .setValue(FramedProperties.ALT_SLOPE, altSlope)
        );
    }

    @Override
    public boolean isOffsetOnOddPos() {
        return false;
    }
}
