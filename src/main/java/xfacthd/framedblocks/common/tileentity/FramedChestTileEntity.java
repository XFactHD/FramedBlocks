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
import java.util.List;

public class FramedChestTileEntity extends FramedTileEntity implements INamedContainerProvider, ITickableTileEntity
{
    public static final ITextComponent TITLE = new TranslationTextComponent("title.framedblocks:framed_chest");

    private final ItemStackHandler itemHandler = new ItemStackHandler(9 * 4);
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private int openCount = 0;
    private long closeStart = 0;

    //Client-only
    private long lastChangeTime = 0;
    private ChestState lastState = ChestState.CLOSED;

    public FramedChestTileEntity() { super(FBContent.tileTypeFramedChest.get()); }

    @Override
    public void tick() //TODO: replace with an intelligent tile ticker in 1.17
    {
        //noinspection ConstantConditions
        if (!level.isClientSide() && (level.getGameTime() - closeStart) >= 10 && getBlockState().getValue(PropertyHolder.CHEST_STATE) == ChestState.CLOSING)
        {
            closeStart = 0;
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(PropertyHolder.CHEST_STATE, ChestState.CLOSED));
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
                level.playSound(null, worldPosition, SoundEvents.CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(PropertyHolder.CHEST_STATE, ChestState.CLOSING));

                closeStart = level.getGameTime();
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
                long diff = level.getGameTime() - lastChangeTime;
                lastChangeTime = level.getGameTime() - (diff < 10 ? 10 - diff : 0);
            }
            else
            {
                //noinspection ConstantConditions
                lastChangeTime = level.getGameTime();
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
        if (level.getBlockEntity(worldPosition) != this)
        {
            return false;
        }
        return !(player.distanceToSqr((double)worldPosition.getX() + 0.5D, (double)worldPosition.getY() + 0.5D, (double)worldPosition.getZ() + 0.5D) > 64.0D);
    }

    public void addDrops(List<ItemStack> drops)
    {
        for (int i = 0; i < itemHandler.getSlots(); i++)
        {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                drops.add(stack);
            }
        }
    }



    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        nbt.put("inventory", itemHandler.serializeNBT());
        return super.save(nbt);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        super.load(state, nbt);
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