package xfacthd.framedblocks.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import xfacthd.framedblocks.FramedBlocks;

public class FramedHammerItem extends Item
{
    public FramedHammerItem()
    {
        super(new Properties()
                .stacksTo(1)
                .tab(FramedBlocks.FRAMED_GROUP)
        );
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) { return true; }

    @Override
    public ItemStack getContainerItem(ItemStack stack) { return stack.copy(); }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player)
    {
        return true;
    }
}