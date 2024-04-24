package xfacthd.framedblocks.common.blockentity.special;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.common.FBContent;

import java.util.ArrayList;
import java.util.List;

public class FramedChiseledBookshelfBlockEntity extends FramedBlockEntity
{
    private final ItemStackHandler itemHandler = new ItemStackHandler(6);
    private int lastInteractedSlot = -1;

    public FramedChiseledBookshelfBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_CHISELED_BOOKSHELF.value(), pos, state);
    }

    public void placeBook(ItemStack stack, int slot)
    {
        itemHandler.setStackInSlot(slot, stack);
        updateState(slot);
        setChanged();
    }

    public ItemStack takeBook(int slot)
    {
        ItemStack stack = itemHandler.getStackInSlot(slot);
        itemHandler.setStackInSlot(slot, ItemStack.EMPTY);
        updateState(slot);
        setChanged();
        return stack;
    }

    private void updateState(int slot)
    {
        lastInteractedSlot = slot;

        BlockState state = getBlockState();
        for (int i = 0; i < ChiseledBookShelfBlockEntity.MAX_BOOKS_IN_STORAGE; i++)
        {
            BooleanProperty prop = ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(i);
            state = state.setValue(prop, !itemHandler.getStackInSlot(i).isEmpty());
        }
        level().setBlockAndUpdate(worldPosition, state);
    }

    public void forceStateUpdate()
    {
        updateState(lastInteractedSlot);
    }

    public IItemHandler getItemHandler()
    {
        return itemHandler;
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
        return drops;
    }

    public void clearContents()
    {
        for (int i = 0; i < itemHandler.getSlots(); i++)
        {
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public int getAnalogOutputSignal()
    {
        return lastInteractedSlot + 1;
    }

    @Override //Prevent writing inventory contents
    public CompoundTag writeToBlueprint(HolderLookup.Provider provider)
    {
        CompoundTag tag = saveWithoutMetadata(provider);
        tag.remove("inventory");
        tag.remove("last_slot");
        return tag;
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        nbt.put("inventory", itemHandler.serializeNBT(provider));
        nbt.putInt("last_slot", lastInteractedSlot);
        super.saveAdditional(nbt, provider);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.loadAdditional(nbt, provider);
        itemHandler.deserializeNBT(provider, nbt.getCompound("inventory"));
        lastInteractedSlot = nbt.getInt("last_slot");
    }
}
