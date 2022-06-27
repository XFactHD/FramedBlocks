package xfacthd.framedblocks.common.data.blueprint;

import net.minecraft.world.item.ItemStack;
import xfacthd.framedblocks.common.FBContent;

import java.util.Optional;

public class DoublePanelCopyBehaviour extends DoubleBlockCopyBehaviour
{
    @Override
    public Optional<ItemStack> getBlockItem()
    {
        return Optional.of(new ItemStack(FBContent.blockFramedPanel.get(), 2));
    }
}
