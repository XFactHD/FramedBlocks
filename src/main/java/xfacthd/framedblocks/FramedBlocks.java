package xfacthd.framedblocks;

import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Mod;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.IFramedBlock;

@Mod(FramedBlocks.MODID)
public class FramedBlocks
{
	public static final String MODID = "framedblocks";
	
    public static final ItemGroup FRAMED_GROUP = new ItemGroup("framed_blocks")
    {
        @Override
        public ItemStack createIcon() { return new ItemStack(FBContent.blockFramedCube); }

        @Override
        public void fill(NonNullList<ItemStack> items)
        {
            super.fill(items);
            items.sort((s1, s2) ->
            {
                if (s1.getItem() == FBContent.itemFramedHammer) { return 1; }
                if (s2.getItem() == FBContent.itemFramedHammer) { return -1; }

                Block b1 = ((BlockItem) s1.getItem()).getBlock();
                Block b2 = ((BlockItem) s2.getItem()).getBlock();
                return ((IFramedBlock)b1).getBlockType().compareTo(((IFramedBlock)b2).getBlockType());
            });
        }
    };
}
