package xfacthd.framedblocks.common.util;

import com.google.common.base.Preconditions;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.item.FramedToolItem;

public final class FramedCreativeTab extends CreativeModeTab
{
    public FramedCreativeTab() { super("framed_blocks"); }

    @Override
    public ItemStack makeIcon() { return new ItemStack(FBContent.blockFramedCube.get()); }

    @Override
    public void fillItemList(NonNullList<ItemStack> items)
    {
        super.fillItemList(items);
        items.sort((s1, s2) ->
        {
            Item itemOne = s1.getItem();
            Item itemTwo = s2.getItem();

            if (itemOne instanceof FramedToolItem toolOne && itemTwo instanceof FramedToolItem toolTwo)
            {
                return toolOne.getType().compareTo(toolTwo.getType());
            }
            else if (itemOne instanceof FramedToolItem) { return 1; }
            else if (itemTwo instanceof FramedToolItem) { return -1; }

            Preconditions.checkArgument(
                    itemOne instanceof BlockItem bi && bi.getBlock() instanceof IFramedBlock,
                    String.format("Invalid item in FramedBlocks creative tab: %s", ForgeRegistries.ITEMS.getKey(itemOne))
            );
            Preconditions.checkArgument(
                    itemTwo instanceof BlockItem bi && bi.getBlock() instanceof IFramedBlock,
                    String.format("Invalid item in FramedBlocks creative tab: %s", ForgeRegistries.ITEMS.getKey(itemOne))
            );

            Block b1 = ((BlockItem) itemOne).getBlock();
            Block b2 = ((BlockItem) itemTwo).getBlock();
            return ((IFramedBlock) b1).getBlockType().compareTo(((IFramedBlock) b2).getBlockType());
        });
    }
}
