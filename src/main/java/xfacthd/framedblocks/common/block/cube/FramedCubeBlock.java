package xfacthd.framedblocks.common.block.cube;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedCubeBlock extends FramedBlock
{
    public FramedCubeBlock()
    {
        super(BlockType.FRAMED_CUBE);
        registerDefaultState(defaultBlockState()
                .setValue(PropertyHolder.ALT, false)
                .setValue(PropertyHolder.REINFORCED, false)
                .setValue(PropertyHolder.SOLID_BG, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.SOLID, PropertyHolder.ALT, PropertyHolder.REINFORCED, PropertyHolder.SOLID_BG);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return state;
    }
}
