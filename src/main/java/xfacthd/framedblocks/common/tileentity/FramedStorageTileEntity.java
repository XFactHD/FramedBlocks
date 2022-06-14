package xfacthd.framedblocks.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.INameable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.container.FramedStorageContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FramedStorageTileEntity extends FramedTileEntity implements INamedContainerProvider, INameable
{
    public static final ITextComponent TITLE = new TranslationTextComponent("title.framedblocks:framed_secret_storage");

    private final ItemStackHandler itemHandler = new ItemStackHandler(9 * 4)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            FramedStorageTileEntity.this.setChanged();
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private ITextComponent customName = null;

    public FramedStorageTileEntity() { super(FBContent.tileTypeFramedSecretStorage.get()); }

    protected FramedStorageTileEntity(TileEntityType<?> type) { super(type); }

    public void open(ServerPlayerEntity player) { NetworkHooks.openGui(player, this, worldPosition); }

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

    public void setCustomName(ITextComponent customName)
    {
        this.customName = customName;
        setChanged();
    }

    @Override
    public ITextComponent getName() { return customName != null ? customName : getDefaultName(); }

    @Override
    public ITextComponent getCustomName() { return customName; }



    @Override //Prevent writing inventory contents
    public CompoundNBT writeToBlueprint() { return super.save(new CompoundNBT()); }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        nbt.put("inventory", itemHandler.serializeNBT());
        if (customName != null)
        {
            nbt.putString("custom_name", ITextComponent.Serializer.toJson(customName));
        }
        return super.save(nbt);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        super.load(state, nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        if (nbt.contains("custom_name", Constants.NBT.TAG_STRING))
        {
            customName = ITextComponent.Serializer.fromJson(nbt.getString("custom_name"));
        }
    }



    protected ITextComponent getDefaultName() { return TITLE; }

    @Override
    public final ITextComponent getDisplayName() { return getName(); }

    @Override
    public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
    {
        return new FramedStorageContainer(windowId, inv, this);
    }
}
