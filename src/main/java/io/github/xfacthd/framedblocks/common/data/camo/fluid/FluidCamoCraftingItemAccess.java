package io.github.xfacthd.framedblocks.common.data.camo.fluid;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

final class FluidCamoCraftingItemAccess implements ItemAccess {
    private final ItemAccess stackAccess;
    private final Item item;
    @Nullable
    private ResourceStack<ItemResource> capturedRemainder = null;

    public FluidCamoCraftingItemAccess(ItemStack stack) {
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
