package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.menu.FramedChestMenu;
import xfacthd.framedblocks.common.data.ChestState;
import xfacthd.framedblocks.common.data.PropertyHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class FramedChestBlockEntity extends FramedBlockEntity implements MenuProvider
{
    public static final Component TITLE = new TranslatableComponent("title.framedblocks:framed_chest");

    private final ItemStackHandler itemHandler = new ItemStackHandler(9 * 4);
    //private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler); //TODO: revert to empty when onLoad() is called again
    private int openCount = 0;
    private long closeStart = 0;

    //Client-only
    private long lastChangeTime = 0;
    private ChestState lastState = ChestState.CLOSED;

    public FramedChestBlockEntity(BlockPos pos, BlockState state) { super(FBContent.blockEntityTypeFramedChest.get(), pos, state); }

    public static void tick(Level level, BlockPos pos, BlockState state, FramedChestBlockEntity tile)
    {
        if (!level.isClientSide() && (level.getGameTime() - tile.closeStart) >= 10 && state.getValue(PropertyHolder.CHEST_STATE) == ChestState.CLOSING)
        {
            tile.closeStart = 0;
            level.setBlockAndUpdate(pos, state.setValue(PropertyHolder.CHEST_STATE, ChestState.CLOSED));
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
                level.playSound(null, worldPosition, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
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
    public void invalidateCaps()
    {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public boolean isUsableByPlayer(Player player)
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



    @Override //Prevent writing inventory contents
    public CompoundTag writeToBlueprint() { return super.save(new CompoundTag()); }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        nbt.put("inventory", itemHandler.serializeNBT());
        return super.save(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
    }



    @Override
    public Component getDisplayName() { return TITLE; }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
    {
        return new FramedChestMenu(windowId, inv, this);
    }
}