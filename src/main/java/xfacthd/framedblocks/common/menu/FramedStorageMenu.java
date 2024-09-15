package xfacthd.framedblocks.common.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.special.FramedStorageBlockEntity;
import xfacthd.framedblocks.common.capability.IStorageBlockItemHandler;
import xfacthd.framedblocks.common.util.FramedUtils;

public class FramedStorageMenu extends AbstractContainerMenu
{
    private final IStorageBlockItemHandler itemHandler;
    private final int maxSlotChest;

    public static FramedStorageMenu createSingle(int windowId, Inventory inv, IStorageBlockItemHandler itemHandler)
    {
        return new FramedStorageMenu(FBContent.MENU_TYPE_FRAMED_STORAGE.value(), windowId, inv, itemHandler);
    }

    public static FramedStorageMenu createDouble(int windowId, Inventory inv, IStorageBlockItemHandler itemHandler)
    {
        return new FramedStorageMenu(FBContent.MENU_TYPE_FRAMED_DOUBLE_CHEST.value(), windowId, inv, itemHandler);
    }

    public static FramedStorageMenu createSingle(int windowId, Inventory inv)
    {
        return createSingle(windowId, inv, FramedStorageBlockEntity.createItemHandler(null, false));
    }

    public static FramedStorageMenu createDouble(int windowId, Inventory inv)
    {
        return createDouble(windowId, inv, FramedStorageBlockEntity.createItemHandler(null, true));
    }

    private FramedStorageMenu(MenuType<?> type, int windowId, Inventory inv, IStorageBlockItemHandler itemHandler)
    {
        super(type, windowId);
        this.itemHandler = itemHandler;
        this.maxSlotChest = itemHandler.getSlots();

        int rows = getRowCount();
        int y = 18;
        for (int row = 0; row < rows; ++row)
        {
            for (int col = 0; col < 9; ++col)
            {
                addSlot(new SlotItemHandler(itemHandler, col + row * 9, 8 + col * 18, y));
            }
            y += 18;
        }
        FramedUtils.addPlayerInvSlots(this::addSlot, inv, 8, y + 13);
    }

    public int getRowCount()
    {
        return itemHandler.getSlots() / 9;
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
            if (index < maxSlotChest)
            {
                if (!moveItemStackTo(stack, maxSlotChest, slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!moveItemStackTo(stack, 0, maxSlotChest, false))
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
