package xfacthd.framedblocks.common.block.slope;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedPrismCornerBlock extends FramedThreewayCornerBlock
{
    public FramedPrismCornerBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.OFFSET, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.OFFSET);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) { return null; }

        if (getBlockType() == BlockType.FRAMED_PRISM_CORNER)
        {
            state = state.setValue(FramedProperties.OFFSET, context.getClickedPos().getY() % 2 != 0);
        }
        else if (getBlockType() == BlockType.FRAMED_INNER_PRISM_CORNER)
        {
            state = state.setValue(FramedProperties.OFFSET, context.getClickedPos().getY() % 2 == 0);
        }

        return state;
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        if (player.getMainHandItem().getItem() == FBContent.ITEM_FRAMED_HAMMER.value())
        {
            level.setBlockAndUpdate(pos, state.setValue(FramedProperties.OFFSET, !state.getValue(FramedProperties.OFFSET)));
            return true;
        }
        return super.handleBlockLeftClick(state, level, pos, player);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return super.getJadeRenderState(state).setValue(FramedProperties.OFFSET, state.getValue(FramedProperties.OFFSET));
    }
}
