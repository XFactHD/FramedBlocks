package xfacthd.framedblocks.common.util;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public abstract class ExternalItemHandler implements IItemHandler
{
    private final IItemHandler wrapped;

    public ExternalItemHandler(IItemHandler wrapped)
    {
        this.wrapped = wrapped;
    }

    @Override
    public final int getSlots()
    {
        return wrapped.getSlots();
    }

    @Override
    public final ItemStack getStackInSlot(int slot)
    {
        return wrapped.getStackInSlot(slot);
    }

    @Override
    public final ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        return wrapped.insertItem(slot, stack, simulate);
    }

    @Override
    public final ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (!canExtract(slot))
        {
            return ItemStack.EMPTY;
        }
        return wrapped.extractItem(slot, amount, simulate);
    }

    @Override
    public final int getSlotLimit(int slot)
    {
        return wrapped.getSlotLimit(slot);
    }

    @Override
    public final boolean isItemValid(int slot, @NotNull ItemStack stack)
    {
        return wrapped.isItemValid(slot, stack);
    }

    protected abstract boolean canExtract(int slot);
}
