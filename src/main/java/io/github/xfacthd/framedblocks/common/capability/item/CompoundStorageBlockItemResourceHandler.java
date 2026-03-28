package io.github.xfacthd.framedblocks.common.capability.item;

import io.github.xfacthd.framedblocks.common.menu.FramedStorageMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class CompoundStorageBlockItemResourceHandler implements IStorageBlockItemResourceHandler {
    private final IStorageBlockItemResourceHandler innerOne;
    private final IStorageBlockItemResourceHandler innerTwo;

    public CompoundStorageBlockItemResourceHandler(IStorageBlockItemResourceHandler innerOne, IStorageBlockItemResourceHandler innerTwo) {
        this.innerOne = innerOne;
        this.innerTwo = innerTwo;
    }

    @Override
    public int size() {
        return innerOne.size() + innerTwo.size();
    }

    @Override
    public ItemResource getResource(int slot) {
        int sizeOne = innerOne.size();
        if (slot < sizeOne) {
            return innerOne.getResource(slot);
        }
        return innerTwo.getResource(slot - sizeOne);
    }

    @Override
    public long getAmountAsLong(int slot) {
        int sizeOne = innerOne.size();
        if (slot < sizeOne) {
            return innerOne.getAmountAsLong(slot);
        }
        return innerTwo.getAmountAsLong(slot - sizeOne);
    }

    @Override
    public int insert(int slot, ItemResource resource, int amount, TransactionContext transaction) {
        int sizeOne = innerOne.size();
        if (slot < sizeOne) {
            return innerOne.insert(slot, resource, amount, transaction);
        }
        return innerTwo.insert(slot - sizeOne, resource, amount, transaction);
    }

    @Override
    public int extract(int slot, ItemResource resource, int amount, TransactionContext transaction) {
        int sizeOne = innerOne.size();
        if (slot < sizeOne) {
            return innerOne.extract(slot, resource, amount, transaction);
        }
        return innerTwo.extract(slot - sizeOne, resource, amount, transaction);
    }

    @Override
    public long getCapacityAsLong(int slot, ItemResource resource) {
        int sizeOne = innerOne.size();
        if (slot < sizeOne) {
            return innerOne.getCapacityAsLong(slot, resource);
        }
        return innerTwo.getCapacityAsLong(slot - sizeOne, resource);
    }

    @Override
    public boolean isValid(int slot, ItemResource stack) {
        int sizeOne = innerOne.size();
        if (slot < sizeOne) {
            return innerOne.isValid(slot, stack);
        }
        return innerTwo.isValid(slot - sizeOne, stack);
    }

    @Override
    public FramedStorageMenu createMenu(int windowId, Inventory inv) {
        return FramedStorageMenu.createDouble(windowId, inv, this);
    }

    @Override
    public boolean stillValid(Player player) {
        return innerOne.stillValid(player) && innerTwo.stillValid(player);
    }

    @Override
    public void open() {
        innerOne.open();
        innerTwo.open();
    }

    @Override
    public void close() {
        innerOne.close();
        innerTwo.close();
    }

    @Override
    public IndexModifier<ItemResource> getIndexModifier() {
        int sizeOne = innerOne.size();
        IndexModifier<ItemResource> innerModOne = innerOne.getIndexModifier();
        IndexModifier<ItemResource> innerModTwo = innerTwo.getIndexModifier();
        return (index, resource, amount) -> {
            if (index < sizeOne) {
                innerModOne.set(index, resource, amount);
            } else {
                innerModTwo.set(index - sizeOne, resource, amount);
            }
        };
    }
}
