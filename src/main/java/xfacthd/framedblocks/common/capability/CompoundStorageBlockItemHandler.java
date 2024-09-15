package xfacthd.framedblocks.common.capability;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xfacthd.framedblocks.common.menu.FramedStorageMenu;

public final class CompoundStorageBlockItemHandler implements IStorageBlockItemHandler
{
    private final IStorageBlockItemHandler innerOne;
    private final IStorageBlockItemHandler innerTwo;

    public CompoundStorageBlockItemHandler(IStorageBlockItemHandler innerOne, IStorageBlockItemHandler innerTwo)
    {
        this.innerOne = innerOne;
        this.innerTwo = innerTwo;
    }

    @Override
    public int getSlots()
    {
        return innerOne.getSlots() + innerTwo.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        int sizeOne = innerOne.getSlots();
        if (slot < sizeOne)
        {
            return innerOne.getStackInSlot(slot);
        }
        return innerTwo.getStackInSlot(slot - sizeOne);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack)
    {
        int sizeOne = innerOne.getSlots();
        if (slot < sizeOne)
        {
            innerOne.setStackInSlot(slot, stack);
        }
        else
        {
            innerTwo.setStackInSlot(slot - sizeOne, stack);
        }
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        int sizeOne = innerOne.getSlots();
        if (slot < sizeOne)
        {
            return innerOne.insertItem(slot, stack, simulate);
        }
        return innerTwo.insertItem(slot - sizeOne, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        int sizeOne = innerOne.getSlots();
        if (slot < sizeOne)
        {
            return innerOne.extractItem(slot, amount, simulate);
        }
        return innerTwo.extractItem(slot - sizeOne, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        int sizeOne = innerOne.getSlots();
        if (slot < sizeOne)
        {
            return innerOne.getSlotLimit(slot);
        }
        return innerTwo.getSlotLimit(slot - sizeOne);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        int sizeOne = innerOne.getSlots();
        if (slot < sizeOne)
        {
            return innerOne.isItemValid(slot, stack);
        }
        return innerTwo.isItemValid(slot - sizeOne, stack);
    }

    @Override
    public FramedStorageMenu createMenu(int windowId, Inventory inv)
    {
        return FramedStorageMenu.createDouble(windowId, inv, this);
    }

    @Override
    public boolean stillValid(Player player)
    {
        return innerOne.stillValid(player) && innerTwo.stillValid(player);
    }

    @Override
    public void open()
    {
        innerOne.open();
        innerTwo.open();
    }

    @Override
    public void close()
    {
        innerOne.close();
        innerTwo.close();
    }
}
