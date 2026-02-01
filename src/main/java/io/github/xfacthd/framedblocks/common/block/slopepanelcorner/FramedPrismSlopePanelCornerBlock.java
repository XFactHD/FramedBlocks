package io.github.xfacthd.framedblocks.common.block.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class FramedPrismSlopePanelCornerBlock extends FramedCornerSlopePanelBlock
{

    public FramedPrismSlopePanelCornerBlock(BlockType type, Properties props)
    {
        super(type, props);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.OFFSET, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.OFFSET);
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
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return super.getJadeRenderState(state).setValue(FramedProperties.OFFSET, state.getValue(FramedProperties.OFFSET));
    }
}
