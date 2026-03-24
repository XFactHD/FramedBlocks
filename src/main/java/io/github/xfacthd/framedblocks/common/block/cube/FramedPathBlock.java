package io.github.xfacthd.framedblocks.common.block.cube;

import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class FramedPathBlock extends FramedBlock
{
    public FramedPathBlock(Properties props)
    {
        super(BlockType.FRAMED_PATH, props);
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
        return defaultBlockState();
    }
}
