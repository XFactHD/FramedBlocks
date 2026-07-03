package io.github.xfacthd.framedblocks.api.block;

/// Base implementation of a two-camo [IFramedBlock].
public abstract class AbstractFramedDoubleBlock extends AbstractFramedBlock implements IFramedDoubleBlock {
    public AbstractFramedDoubleBlock(IBlockType blockType, Properties props) {
        super(blockType, props);
    }
}
