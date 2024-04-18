package xfacthd.framedblocks.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TransientCraftingContainer.class)
public interface AccessorTransientCraftingContainer
{
    @Accessor("items")
    NonNullList<ItemStack> framedblocks$getItems();
}
