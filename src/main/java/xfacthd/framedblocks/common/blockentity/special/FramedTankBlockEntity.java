package xfacthd.framedblocks.common.blockentity.special;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.*;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.capability.TankFluidHandler;

public class FramedTankBlockEntity extends FramedBlockEntity
{
    private final TankFluidHandler fluidHandler = new TankFluidHandler(this);

    public FramedTankBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_TANK.value(), pos, state);
    }

    public ItemInteractionResult handleTankInteraction(Player player, InteractionHand hand)
    {
        if (FluidUtil.interactWithFluidHandler(player, hand, fluidHandler))
        {
            return ItemInteractionResult.sidedSuccess(level().isClientSide());
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public FluidStack getContents()
    {
        return fluidHandler.getFluid();
    }

    public IFluidHandler getFluidHandler()
    {
        return fluidHandler;
    }

    public void onTankContentsChanged()
    {
        if (level == null || level.isClientSide()) return;

        setChanged();
        if (!getBlockState().getValue(FramedProperties.SOLID))
        {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public int getAnalogSignal()
    {
        return fluidHandler.getFluid().getAmount() * 15 / TankFluidHandler.CAPACITY;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider)
    {
        CompoundTag tag = super.getUpdateTag(provider);
        fluidHandler.save(tag, provider);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.handleUpdateTag(nbt, provider);
        fluidHandler.load(nbt, provider);
    }

    @Override
    protected void writeToDataPacket(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.writeToDataPacket(nbt, provider);
        fluidHandler.save(nbt, provider);
    }

    @Override
    protected boolean readFromDataPacket(CompoundTag nbt, HolderLookup.Provider provider)
    {
        fluidHandler.load(nbt, provider);
        return super.readFromDataPacket(nbt, provider);
    }

    @Override
    protected void applyMiscComponents(DataComponentInput input)
    {
        FluidStack contents = input.getOrDefault(FBContent.DC_TYPE_TANK_CONTENTS, SimpleFluidContent.EMPTY).copy();
        if (!contents.isEmpty())
        {
            fluidHandler.setFluid(contents);
        }
    }

    @Override
    protected void collectMiscComponents(DataComponentMap.Builder builder)
    {
        FluidStack contents = fluidHandler.getFluid();
        if (!contents.isEmpty())
        {
            builder.set(FBContent.DC_TYPE_TANK_CONTENTS, SimpleFluidContent.copyOf(contents));
        }
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.loadAdditional(nbt, provider);
        fluidHandler.load(nbt, provider);
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.saveAdditional(nbt, provider);
        fluidHandler.save(nbt, provider);
    }
}
