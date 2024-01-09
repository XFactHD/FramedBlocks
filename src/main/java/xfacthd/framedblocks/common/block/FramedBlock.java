package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import xfacthd.framedblocks.api.block.AbstractFramedBlock;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.common.data.*;

public abstract class FramedBlock extends AbstractFramedBlock
{
    protected FramedBlock(BlockType blockType)
    {
        this(blockType, IFramedBlock.createProperties(blockType));
    }

    protected FramedBlock(BlockType blockType, Properties props)
    {
        super(blockType, props);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type)
    {
        if (getBlockType() != BlockType.FRAMED_CUBE)
        {
            return false;
        }
        return super.isPathfindable(state, level, pos, type);
    }

    @Override
    public BlockType getBlockType()
    {
        return (BlockType) super.getBlockType();
    }
}
