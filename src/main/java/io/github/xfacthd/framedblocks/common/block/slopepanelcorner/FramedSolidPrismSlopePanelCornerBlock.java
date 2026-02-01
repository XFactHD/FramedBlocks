package io.github.xfacthd.framedblocks.common.block.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

// TODO 1.21.6: remove
public class FramedSolidPrismSlopePanelCornerBlock extends FramedPrismSlopePanelCornerBlock
{
    public FramedSolidPrismSlopePanelCornerBlock(BlockType type, Properties props)
    {
        super(type, props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.SOLID);
    }
}
