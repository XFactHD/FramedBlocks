package io.github.xfacthd.framedblocks.api.camo;

import io.github.xfacthd.framedblocks.api.util.ConfigView;
import net.minecraft.world.item.ItemStack;

/// Controls how a camo is applied to a framed block in the camo crafting recipe.
public interface CamoCraftingHandler<T extends CamoContainer<?, T>> {
    /// {@return whether a camo can be created from the provided stack in a crafting recipe without level and player context}
    ///
    /// @param stack   The item access holding the stack to create the camo from. Must not be modified
    /// @param consume Whether the camo item should be consumed. This is only a hint, the provided stack must not be modified
    boolean canApply(ItemStack stack, boolean consume);

    /// Compute the camo container to apply to the item in a crafting recipe from the provided stack
    ///
    /// @param stack   The item access holding the stack to create the camo from. Must not be modified
    /// @param consume Whether the camo item should be consumed. This is only a hint, the provided stack must not be modified
    /// @return The camo container to apply to the resulting stack
    T apply(ItemStack stack, boolean consume);

    /// Compute the crafting remainder after the stack held by the provided item access is used to apply a camo to
    /// a framed block in a crafting recipe.
    ///
    /// @param stack   The [ItemStack] the camo was created from. Must not be modified
    /// @param consume Whether the camo should be consumed (see [ConfigView.Server#shouldConsumeCamoItem()])
    /// @return The stack that should remain in the crafting grid
    ItemStack getRemainder(ItemStack stack, boolean consume);
}
