package xfacthd.framedblocks.common.block.cube;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedCube extends FramedBlock
{
    public FramedCube()
    {
        super(BlockType.FRAMED_CUBE);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.ALT, false)
                .setValue(FramedProperties.REINFORCED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.SOLID, FramedProperties.ALT, FramedProperties.REINFORCED);
    }
}
