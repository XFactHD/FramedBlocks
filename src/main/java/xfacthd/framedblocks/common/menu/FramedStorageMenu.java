package xfacthd.framedblocks.common.menu;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedChestBlockEntity;
import xfacthd.framedblocks.common.blockentity.FramedStorageBlockEntity;
import xfacthd.framedblocks.common.util.FramedUtils;

public class FramedStorageMenu extends AbstractContainerMenu
{
    private static final int MAX_SLOT_CHEST = 27;
    private final FramedStorageBlockEntity blockEntity;

    public FramedStorageMenu(int windowId, Inventory inv, BlockEntity blockEntity)
    {
        super(FBContent.menuTypeFramedStorage.get(), windowId);

        Preconditions.checkArgument(blockEntity instanceof FramedStorageBlockEntity);
        this.blockEntity = (FramedStorageBlockEntity) blockEntity;

        IItemHandler blockInv = this.blockEntity
                .getCapability(ForgeCapabilities.ITEM_HANDLER)
                .orElseThrow(IllegalStateException::new);
        //noinspection ConstantConditions
        if (this.blockEntity.getLevel().isClientSide())
        {
            blockInv = new ItemStackHandler(blockInv.getSlots());
        }

        for (int row = 0; row < 3; ++row)
        {
            for (int col = 0; col < 9; ++col)
            {
                addSlot(new SlotItemHandler(blockInv, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }
        FramedUtils.addPlayerInvSlots(this::addSlot, inv, 8, 85);
    }

    public FramedStorageMenu(int windowId, Inventory inv, FriendlyByteBuf extraData)
    {
        this(windowId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    @Override
    public boolean stillValid(Player player) { return blockEntity.isUsableByPlayer(player); }

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

        //noinspection ConstantConditions
        if (!blockEntity.getLevel().isClientSide() && blockEntity instanceof FramedChestBlockEntity chest)
        {
            chest.close();
        }
    }
}