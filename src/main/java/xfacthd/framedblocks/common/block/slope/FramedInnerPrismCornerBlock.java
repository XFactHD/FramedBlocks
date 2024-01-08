package xfacthd.framedblocks.common.block.slope;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedInnerPrismCornerBlock extends FramedPrismCornerBlock
{
    public FramedInnerPrismCornerBlock(BlockType type)
    {
        super(type);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.SOLID);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return FBContent.BLOCK_FRAMED_INNER_PRISM_CORNER.value()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
