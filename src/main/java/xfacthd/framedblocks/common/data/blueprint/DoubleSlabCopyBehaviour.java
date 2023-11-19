package xfacthd.framedblocks.common.data.blueprint;

import net.minecraft.world.item.ItemStack;
import xfacthd.framedblocks.common.FBContent;

import java.util.Optional;

public final class DoubleSlabCopyBehaviour extends DoubleBlockCopyBehaviour
{
    @Override
    public Optional<ItemStack> getBlockItem()
    {
        return Optional.of(new ItemStack(FBContent.BLOCK_FRAMED_SLAB.value(), 2));
    }
}
