package xfacthd.framedblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import xfacthd.framedblocks.common.data.BlockType;

import javax.annotation.Nullable;

public class FramedCube extends FramedBlock
{
    public FramedCube() { super(BlockType.FRAMED_CUBE); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return withWater(getDefaultState(), context.getWorld(), context.getPos());
    }
}
