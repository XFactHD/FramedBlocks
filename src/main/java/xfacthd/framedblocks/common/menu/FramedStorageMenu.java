package xfacthd.framedblocks.common.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.special.FramedStorageBlockEntity;
import xfacthd.framedblocks.common.capability.StorageBlockItemStackHandler;
import xfacthd.framedblocks.common.util.FramedUtils;

public class FramedStorageMenu extends AbstractContainerMenu
{
    private static final int MAX_SLOT_CHEST = 27;
    private final StorageBlockItemStackHandler itemHandler;

    public FramedStorageMenu(int windowId, Inventory inv, StorageBlockItemStackHandler itemHandler)
    {
        super(FBContent.MENU_TYPE_FRAMED_STORAGE.value(), windowId);
        this.itemHandler = itemHandler;
        for (int row = 0; row < 3; ++row)
        {
            for (int col = 0; col < 9; ++col)
            {
                addSlot(new SlotItemHandler(itemHandler, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }
        FramedUtils.addPlayerInvSlots(this::addSlot, inv, 8, 85);
    }

    public FramedStorageMenu(int windowId, Inventory inv)
    {
        this(windowId, inv, FramedStorageBlockEntity.createItemHandler(null));
    }

    @Override
    public boolean stillValid(Player player)
    {
        return itemHandler.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        ItemStack remainder = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem())
        {
            ItemStack stack = slot.getItem();
            remainder = stack.copy();
            if (index < MAX_SLOT_CHEST)
            {
                if (!moveItemStackTo(stack, MAX_SLOT_CHEST, slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!moveItemStackTo(stack, 0, MAX_SLOT_CHEST, false))
            {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty())
            {
                slot.set(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
            }
        }

        return remainder;
    }

    @Override
    public void removed(Player player)
    {
        super.removed(player);
        itemHandler.close();
    }
}
