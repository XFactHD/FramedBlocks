package xfacthd.framedblocks.common.block.cube;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedGlowingCube extends FramedBlock
{
    public FramedGlowingCube()
    {
        super(BlockType.FRAMED_GLOWING_CUBE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.SOLID);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return state;
    }
}
