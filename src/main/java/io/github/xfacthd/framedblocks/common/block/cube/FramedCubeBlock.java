package io.github.xfacthd.framedblocks.common.block.cube;

import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedCubeBlock extends FramedBlock
{
    public FramedCubeBlock(Properties props)
    {
        super(BlockType.FRAMED_CUBE, props);
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
        builder.add(PropertyHolder.ALT, PropertyHolder.REINFORCED, PropertyHolder.SOLID_BG);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState();
    }

    @Override
    @Nullable
    public Direction getHorizontalOrientation(BlockState state)
    {
        return null;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return state;
    }
}
