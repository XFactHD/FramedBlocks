package io.github.xfacthd.framedblocks.common.capability.item;

import io.github.xfacthd.framedblocks.common.blockentity.special.FramedChestBlockEntity;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedStorageBlockEntity;
import io.github.xfacthd.framedblocks.common.menu.FramedStorageMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jspecify.annotations.Nullable;

public final class StorageBlockItemResourceHandler extends ItemStacksResourceHandler implements IStorageBlockItemResourceHandler {
    @Nullable
    private final FramedStorageBlockEntity be;

    public StorageBlockItemResourceHandler(@Nullable FramedStorageBlockEntity be, int slots) {
        super(slots);
        this.be = be;
    }

    @Override
    protected void onContentsChanged(int slot, ItemStack prevContents) {
        if (be != null) {
            be.setChanged();
        }
    }

    @Override
    public FramedStorageMenu createMenu(int windowId, Inventory inv) {
        return FramedStorageMenu.createSingle(windowId, inv, this);
    }

    @Override
    public boolean stillValid(Player player) {
        return be != null && be.isUsableByPlayer(player);
    }

    @Override
    public void open() {
        if (be instanceof FramedChestBlockEntity chest) {
            chest.doOpen();
        }
    }

    @Override
    public void close() {
        if (be instanceof FramedChestBlockEntity chest) {
            chest.close();
        }
    }

    @Override
    public IndexModifier<ItemResource> getIndexModifier() {
        return this::set;
    }
}
