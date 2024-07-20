package xfacthd.framedblocks.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.data.BlockType;

public abstract class AbstractFramedDoubleBlock extends FramedBlock implements IFramedDoubleBlock
{
    public AbstractFramedDoubleBlock(BlockType blockType)
    {
        super(blockType);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.SOLID);
    }
}
