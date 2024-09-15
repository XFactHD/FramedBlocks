package xfacthd.framedblocks.common.blockentity.special;

import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.capability.IStorageBlockItemHandler;
import xfacthd.framedblocks.common.capability.StorageBlockItemStackHandler;
import xfacthd.framedblocks.common.menu.FramedStorageMenu;

import java.util.ArrayList;
import java.util.List;

public class FramedStorageBlockEntity extends FramedBlockEntity implements MenuProvider, Nameable, Clearable
{
    public static final Component TITLE = Utils.translate("title", "framed_secret_storage");
    public static final int SLOTS = 9 * 3;

    private final StorageBlockItemStackHandler itemHandler = createItemHandler(this, false);
    // TODO 1.21.2: remove overflow handling
    private List<ItemStack> overflow = null;
    private Component customName = null;

    public FramedStorageBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_SECRET_STORAGE.value(), pos, state);
    }

    protected FramedStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void open(ServerPlayer player)
    {
        player.openMenu(this);
    }

    public boolean isUsableByPlayer(Player player)
    {
        if (level().getBlockEntity(worldPosition) != this)
        {
            return false;
        }
        return !(player.distanceToSqr((double)worldPosition.getX() + 0.5D, (double)worldPosition.getY() + 0.5D, (double)worldPosition.getZ() + 0.5D) > 64.0D);
    }

    public List<ItemStack> getDrops()
    {
        List<ItemStack> drops = new ArrayList<>();
        for (int i = 0; i < itemHandler.getSlots(); i++)
        {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                drops.add(stack);
            }
        }
        if (overflow != null)
        {
            drops.addAll(overflow);
        }
        return drops;
    }

    @Override
    public void clearContent()
    {
        for (int i = 0; i < itemHandler.getSlots(); i++)
        {
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
        overflow = null;
    }

    public int getAnalogOutputSignal()
    {
        return getAnalogOutputSignal(itemHandler);
    }

    protected static int getAnalogOutputSignal(IStorageBlockItemHandler itemHandler)
    {
        int stacks = 0;
        float fullness = 0;

        for(int i = 0; i < itemHandler.getSlots(); ++i)
        {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                float sizeLimit = Math.min(itemHandler.getSlotLimit(i), stack.getMaxStackSize());
                fullness += (float)stack.getCount() / sizeLimit;

                stacks++;
            }
        }

        fullness /= (float)itemHandler.getSlots();
        return Mth.floor(fullness * 14F) + (stacks > 0 ? 1 : 0);
    }

    public IStorageBlockItemHandler getItemHandler()
    {
        return itemHandler;
    }

    public void setCustomName(Component customName)
    {
        this.customName = customName;
        setChangedWithoutSignalUpdate();
    }

    @Override
    public Component getName()
    {
        return customName != null ? customName : getDefaultName();
    }

    @Override
    public Component getCustomName()
    {
        return customName;
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        nbt.put("inventory", itemHandler.serializeNBT(provider));
        if (customName != null)
        {
            nbt.putString("custom_name", Component.Serializer.toJson(customName, provider));
        }
        saveOverflow(nbt, provider);
        super.saveAdditional(nbt, provider);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.loadAdditional(nbt, provider);
        itemHandler.deserializeNBT(provider, nbt.getCompound("inventory"));
        separateOverflow();
        loadOverflow(nbt, provider);
        if (nbt.contains("custom_name", Tag.TAG_STRING))
        {
            customName = Component.Serializer.fromJson(nbt.getString("custom_name"), provider);
        }
    }

    private void separateOverflow()
    {
        if (itemHandler.getSlots() > SLOTS)
        {
            List<ItemStack> stacks = itemHandler.getBackingList();
            overflow = NonNullList.withSize(stacks.size() - SLOTS, ItemStack.EMPTY);
            for (int i = SLOTS; i < stacks.size(); i++)
            {
                overflow.set(i - SLOTS, stacks.get(i));
            }
            itemHandler.setSize(SLOTS);
            for (int i = 0; i < SLOTS; i++)
            {
                itemHandler.setStackInSlot(i, stacks.get(i));
            }
        }
    }

    private void loadOverflow(CompoundTag nbt, HolderLookup.Provider provider)
    {
        if (nbt.contains("overflow"))
        {
            ListTag stackList = nbt.getList("overflow", Tag.TAG_COMPOUND);
            overflow = NonNullList.withSize(stackList.size(), ItemStack.EMPTY);
            for (int i = 0; i < stackList.size(); i++)
            {
                CompoundTag itemTag = stackList.getCompound(i);
                int slot = itemTag.getInt("Slot");
                if (slot >= 0 && slot < overflow.size())
                {
                    ItemStack.parse(provider, itemTag).ifPresent(stack -> overflow.set(slot, stack));
                }
            }
        }
    }

    private void saveOverflow(CompoundTag nbt, HolderLookup.Provider provider)
    {
        if (overflow != null)
        {
            ListTag stackList = new ListTag();
            for (int i = 0; i < overflow.size(); i++)
            {
                if (!overflow.get(i).isEmpty())
                {
                    CompoundTag itemTag = new CompoundTag();
                    itemTag.putInt("Slot", i);
                    stackList.add(overflow.get(i).save(provider, itemTag));
                }
            }
            nbt.put("overflow", stackList);
        }
    }

    protected Component getDefaultName()
    {
        return TITLE;
    }

    @Override
    public final Component getDisplayName()
    {
        return getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
    {
        return FramedStorageMenu.createSingle(windowId, inv, itemHandler);
    }



    public static StorageBlockItemStackHandler createItemHandler(@Nullable FramedStorageBlockEntity be, boolean doubleChest)
    {
        return new StorageBlockItemStackHandler(be, SLOTS * (doubleChest ? 2 : 1));
    }
}
