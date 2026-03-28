package io.github.xfacthd.framedblocks.common.data.camo.fluid;

import io.github.xfacthd.framedblocks.api.camo.CamoCraftingHandler;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

final class FluidCamoCraftingHandler implements CamoCraftingHandler<FluidCamoContainer> {
    static final FluidCamoCraftingHandler INSTANCE = new FluidCamoCraftingHandler();

    private FluidCamoCraftingHandler() { }

    @Override
    public boolean canApply(ItemStack stack, boolean consume) {
        if (!stack.is(Utils.CRAFTING_BLOCKED_FLUID_CONTAINERS)) {
            ItemAccess itemAccess = new FluidCamoCraftingItemAccess(stack);
            return FluidCamoContainerFactory.applyCamo(itemAccess, null, consume, false) != null;
        }
        return false;
    }

    @Override
    public FluidCamoContainer apply(ItemStack stack, boolean consume) {
        ItemAccess itemAccess = new FluidCamoCraftingItemAccess(stack);
        FluidCamoContainer camo = FluidCamoContainerFactory.applyCamo(itemAccess, null, consume, false);
        return assertResult(camo, stack, "apply");
    }

    @Override
    public ItemStack getRemainder(ItemStack stack, boolean consume) {
        if (consume) {
            FluidCamoCraftingItemAccess itemAccess = new FluidCamoCraftingItemAccess(stack.copy());
            // Perform a "dummy" application to force the creation of the remainder
            FluidCamoContainer camo = FluidCamoContainerFactory.applyCamo(itemAccess, null, true, true);
            assertResult(camo, stack, "getRemainder");
            return itemAccess.computeRemainder();
        }
        return stack.copyWithCount(1);
    }

    private static FluidCamoContainer assertResult(@Nullable FluidCamoContainer camo, ItemStack stack, String method) {
        if (camo == null) {
            throw new IllegalStateException(String.format(
                    Locale.ROOT,
                    "CamoCraftingHandler#%s() called with invalid input, CamoCraftingHandler#canApply() was likely not called: %s",
                    method,
                    Utils.formatItemStack(stack)
            ));
        }
        return camo;
    }
}
