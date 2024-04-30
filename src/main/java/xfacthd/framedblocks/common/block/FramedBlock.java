package xfacthd.framedblocks.common.block;

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
    protected boolean isPathfindable(BlockState state, PathComputationType type)
    {
        if (getBlockType() != BlockType.FRAMED_CUBE)
        {
            return false;
        }
        return super.isPathfindable(state, type);
    }

    @Override
    public BlockType getBlockType()
    {
        return (BlockType) super.getBlockType();
    }
}
