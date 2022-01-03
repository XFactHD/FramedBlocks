package xfacthd.framedblocks.common.container;

import com.google.common.base.Preconditions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.tileentity.FramedChestTileEntity;

public class FramedChestContainer extends Container
{
    private static final int MAX_SLOT_CHEST = 27;
    private final FramedChestTileEntity chest;

    public FramedChestContainer(int windowId, PlayerInventory inv, TileEntity chest)
    {
        super(FBContent.containerTypeFramedChest.get(), windowId);

        Preconditions.checkArgument(chest instanceof FramedChestTileEntity);
        this.chest = (FramedChestTileEntity) chest;

        this.chest.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler ->
        {
            for (int row = 0; row < 3; ++row)
            {
                for (int col = 0; col < 9; ++col)
                {
                    addSlot(new SlotItemHandler(handler, col + row * 9, 8 + col * 18, 18 + row * 18));
                }
            }
        });

        for (int row = 0; row < 3; ++row)
        {
            for (int col = 0; col < 9; ++col)
            {
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 85 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col)
        {
            addSlot(new Slot(inv, col, 8 + col * 18, 143));
        }
    }

    public FramedChestContainer(int windowId, PlayerInventory inv, PacketBuffer extraData)
    {
        this(windowId, inv, inv.player.world.getTileEntity(extraData.readBlockPos()));
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) { return chest.isUsableByPlayer(player); }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int index)
    {
        ItemStack remainder = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);
        if (slot != null && slot.getHasStack())
        {
            ItemStack stack = slot.getStack();
            remainder = stack.copy();
            if (index < MAX_SLOT_CHEST)
            {
                if (!mergeItemStack(stack, MAX_SLOT_CHEST, inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!mergeItemStack(stack, 0, MAX_SLOT_CHEST, false))
            {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return remainder;
    }

    @Override
    public void onContainerClosed(PlayerEntity player)
    {
        super.onContainerClosed(player);

        //noinspection ConstantConditions
        if (!chest.getWorld().isRemote())
        {
            chest.close();
        }
    }
}