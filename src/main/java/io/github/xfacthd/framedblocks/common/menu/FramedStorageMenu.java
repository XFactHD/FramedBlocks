package io.github.xfacthd.framedblocks.common.menu;

import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedStorageBlockEntity;
import io.github.xfacthd.framedblocks.common.capability.item.IStorageBlockItemResourceHandler;
import io.github.xfacthd.framedblocks.common.util.FramedUtils;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class FramedStorageMenu extends AbstractContainerMenu {
    private final IStorageBlockItemResourceHandler itemHandler;
    private final int maxSlotChest;

    public static FramedStorageMenu createSingle(int windowId, Inventory inv, IStorageBlockItemResourceHandler itemHandler) {
        return new FramedStorageMenu(FBContent.MENU_TYPE_FRAMED_STORAGE.value(), windowId, inv, itemHandler);
    }

    public static FramedStorageMenu createDouble(int windowId, Inventory inv, IStorageBlockItemResourceHandler itemHandler) {
        return new FramedStorageMenu(FBContent.MENU_TYPE_FRAMED_DOUBLE_CHEST.value(), windowId, inv, itemHandler);
    }

    public static FramedStorageMenu createSingle(int windowId, Inventory inv) {
        return createSingle(windowId, inv, FramedStorageBlockEntity.createItemHandler(null, false));
    }

    public static FramedStorageMenu createDouble(int windowId, Inventory inv) {
        return createDouble(windowId, inv, FramedStorageBlockEntity.createItemHandler(null, true));
    }

    private FramedStorageMenu(MenuType<?> type, int windowId, Inventory inv, IStorageBlockItemResourceHandler itemHandler) {
        super(type, windowId);
        this.itemHandler = itemHandler;
        this.maxSlotChest = itemHandler.size();

        int rows = getRowCount();
        int y = 18;
        IndexModifier<ItemResource> indexModifier = itemHandler.getIndexModifier();
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < 9; ++col) {
                addSlot(new ResourceHandlerSlot(itemHandler, indexModifier, col + row * 9, 8 + col * 18, y));
            }
            y += 18;
        }
        FramedUtils.addPlayerInvSlots(this::addSlot, inv, 8, y + 13);
    }

    public int getRowCount() {
        return itemHandler.size() / 9;
    }

    @Override
    public boolean stillValid(Player player) {
        return itemHandler.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack remainder = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            remainder = stack.copy();
            if (index < maxSlotChest) {
                if (!moveItemStackTo(stack, maxSlotChest, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 0, maxSlotChest, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return remainder;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        itemHandler.close();
    }
}
