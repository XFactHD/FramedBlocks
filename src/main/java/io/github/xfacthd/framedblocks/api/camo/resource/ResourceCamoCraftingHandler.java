package io.github.xfacthd.framedblocks.api.camo.resource;

import io.github.xfacthd.framedblocks.api.camo.CamoCraftingHandler;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

/// Base crafting handler implementation for [Resource]-based camos.
public class ResourceCamoCraftingHandler<R extends Resource, C extends ResourceCamoContent<R, C>, T extends ResourceCamoContainer<R, C, T>> implements CamoCraftingHandler<T> {
    protected final ResourceCamoContainerFactory<R, C, T> factory;
    protected final TagKey<Item> blockedContainers;

    /// @param factory           The camo container factory to use for creating the camo containers during crafting
    /// @param blockedContainers Tag of items which can store this type of resource but must not be accepted as crafting ingredients
    public ResourceCamoCraftingHandler(ResourceCamoContainerFactory<R, C, T> factory, TagKey<Item> blockedContainers) {
        this.factory = factory;
        this.blockedContainers = blockedContainers;
    }

    @Override
    public boolean canApply(ItemStack stack, boolean consume) {
        if (!stack.is(blockedContainers)) {
            ItemAccess itemAccess = new CraftingItemAccess(stack);
            return factory.applyCamo(itemAccess, null, consume, false) != null;
        }
        return false;
    }

    @Override
    public T apply(ItemStack stack, boolean consume) {
        ItemAccess itemAccess = new CraftingItemAccess(stack);
        T camo = factory.applyCamo(itemAccess, null, consume, false);
        return assertResult(camo, stack, "apply");
    }

    @Override
    public ItemStack getRemainder(ItemStack stack, boolean consume) {
        if (consume) {
            CraftingItemAccess itemAccess = new CraftingItemAccess(stack.copy());
            // Perform a "dummy" application to force the creation of the remainder
            T camo = factory.applyCamo(itemAccess, null, true, true);
            assertResult(camo, stack, "getRemainder");
            return itemAccess.computeRemainder();
        }
        return stack.copyWithCount(1);
    }

    protected final T assertResult(@Nullable T camo, ItemStack stack, String method) {
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

    protected static final class CraftingItemAccess implements ItemAccess {
        private final ItemAccess stackAccess;
        private final Item item;
        @Nullable
        private ResourceStack<ItemResource> capturedRemainder = null;

        public CraftingItemAccess(ItemStack stack) {
            this.stackAccess = ItemAccess.forStack(stack);
            this.item = stack.getItem();
        }

        @Override
        public ItemResource getResource() {
            return stackAccess.getResource();
        }

        @Override
        public int getAmount() {
            return stackAccess.getAmount();
        }

        @Override
        public int insert(ItemResource resource, int amount, TransactionContext transaction) {
            if (resource.is(item)) {
                return stackAccess.insert(resource, amount, transaction);
            }

            // Capture "incompatible" items such as buckets resulting from emptying filled buckets
            capturedRemainder = new ResourceStack<>(resource, amount);
            return amount;
        }

        @Override
        public int extract(ItemResource resource, int amount, TransactionContext transaction) {
            return stackAccess.extract(resource, amount, transaction);
        }

        public ItemStack computeRemainder() {
            if (capturedRemainder != null) {
                return capturedRemainder.resource().toStack(capturedRemainder.amount());
            }
            return stackAccess.getResource().toStack();
        }
    }
}
