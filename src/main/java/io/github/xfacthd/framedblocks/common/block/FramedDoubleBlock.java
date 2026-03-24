package io.github.xfacthd.framedblocks.common.block;

import io.github.xfacthd.framedblocks.api.block.AbstractFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;

import java.util.function.UnaryOperator;

public abstract class FramedDoubleBlock extends AbstractFramedDoubleBlock implements IFramedDoubleBlockInternal
{
    protected FramedDoubleBlock(BlockType blockType, Properties props)
    {
        this(blockType, props, UnaryOperator.identity());
    }

    protected FramedDoubleBlock(BlockType blockType, Properties props, UnaryOperator<Properties> propertyModifier)
    {
        super(blockType, propertyModifier.apply(IFramedBlock.applyDefaultProperties(props, blockType)));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType)
    {
        return false;
    }

    @Override
    public BlockType getBlockType()
    {
        return (BlockType) super.getBlockType();
    }
}
