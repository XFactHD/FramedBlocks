package xfacthd.framedblocks.common.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;

public class FramedDoubleBlockItem extends BlockItem
{
    public FramedDoubleBlockItem(AbstractFramedDoubleBlock block)
    {
        super(block, new Properties());
        //noinspection ConstantConditions
        setRegistryName(block.getRegistryName());
    }

    @Override
    protected boolean allowdedIn(CreativeModeTab group) { return false; }
}