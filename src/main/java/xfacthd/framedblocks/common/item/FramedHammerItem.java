package xfacthd.framedblocks.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
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
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player)
    {
        return true;
    }
}