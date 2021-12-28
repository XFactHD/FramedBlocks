package xfacthd.framedblocks.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.container.FramedChestContainer;
import xfacthd.framedblocks.common.data.ChestState;
import xfacthd.framedblocks.common.data.PropertyHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FramedChestTileEntity extends FramedTileEntity implements INamedContainerProvider, ITickableTileEntity
{
    public static final ITextComponent TITLE = new TranslationTextComponent("title.framedblocks:framed_chest");

    private final ItemStackHandler itemHandler = new ItemStackHandler(9 * 4)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            FramedChestTileEntity.this.markDirty();
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private int openCount = 0;
    private long closeStart = 0;

    //Client-only
    private long lastChangeTime = 0;
    private ChestState lastState = ChestState.CLOSED;

    public FramedChestTileEntity() { super(FBContent.tileTypeFramedChest.get()); }

    @Override
    public void tick()
    {
        //noinspection ConstantConditions
        if (!world.isRemote() && (world.getGameTime() - closeStart) >= 10 && getBlockState().get(PropertyHolder.CHEST_STATE) == ChestState.CLOSING)
        {
            closeStart = 0;
            world.setBlockState(pos, getBlockState().with(PropertyHolder.CHEST_STATE, ChestState.CLOSED));
        }
    }

    public void open() { openCount++; }

    public void close()
    {
        if (openCount > 0)
        {
            openCount--;
            if (openCount == 0)
            {
                //noinspection ConstantConditions
                world.playSound(null, pos, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
                world.setBlockState(pos, getBlockState().with(PropertyHolder.CHEST_STATE, ChestState.CLOSING));

                closeStart = world.getGameTime();
            }
        }
    }

    public long getLastChangeTime(ChestState state)
    {
        if (lastChangeTime == 0 || state != lastState)
        {
            if ((lastState == ChestState.CLOSING && state == ChestState.OPENING) || (lastState == ChestState.OPENING && state == ChestState.CLOSING))
            {
                //noinspection ConstantConditions
                long diff = world.getGameTime() - lastChangeTime;
                lastChangeTime = world.getGameTime() - (diff < 10 ? 10 - diff : 0);
            }
            else
            {
                //noinspection ConstantConditions
                lastChangeTime = world.getGameTime();
            }
            lastState = state;
        }
        return lastChangeTime;
    }



    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    protected void invalidateCaps()
    {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public boolean isUsableByPlayer(PlayerEntity player)
    {
        //noinspection ConstantConditions
        if (world.getTileEntity(pos) != this)
        {
            return false;
        }
        return !(player.getDistanceSq((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D) > 64.0D);
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
        return MathHelper.floor(fullness * 14F) + (stacks > 0 ? 1 : 0);
    }



    @Override //Prevent writing inventory contents
    public CompoundNBT writeToBlueprint() { return super.write(new CompoundNBT()); }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.put("inventory", itemHandler.serializeNBT());
        return super.write(nbt);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt)
    {
        super.read(state, nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
    }



    @Override
    public ITextComponent getDisplayName() { return TITLE; }

    @Override
    public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
    {
        return new FramedChestContainer(windowId, inv, this);
    }
}