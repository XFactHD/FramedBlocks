package io.github.xfacthd.framedblocks.common.blockentity.special;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.NetworkValueInput;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.capability.fluid.TankFluidResourceHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;

public class FramedTankBlockEntity extends FramedBlockEntity
{
    private final TankFluidResourceHandler fluidHandler = new TankFluidResourceHandler(this);

    public FramedTankBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_TANK.value(), pos, state);
    }

    public InteractionResult handleTankInteraction(Player player, InteractionHand hand)
    {
        if (FluidUtil.interactWithFluidHandler(player, hand, worldPosition, fluidHandler))
        {
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    public FluidStack getContents()
    {
        return fluidHandler.getResource(0).toStack(fluidHandler.getAmountAsInt(0));
    }

    public ResourceHandler<FluidResource> getFluidHandler()
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
        return ResourceHandlerUtil.getRedstoneSignalFromResourceHandler(fluidHandler);
    }

    @Override
    protected void writeToDataPacket(ValueOutput valueOutput)
    {
        super.writeToDataPacket(valueOutput);
        fluidHandler.serialize(valueOutput);
    }

    @Override
    protected void readFromDataPacket(NetworkValueInput input)
    {
        super.readFromDataPacket(input);
        fluidHandler.deserialize(input);
    }

    @Override
    public void removeComponentsFromTag(ValueOutput valueOutput)
    {
        super.removeComponentsFromTag(valueOutput);
        valueOutput.discard(TankFluidResourceHandler.FLUID_NBT_KEY);
    }

    @Override
    protected void applyMiscComponents(DataComponentGetter input)
    {
        SimpleFluidContent content = input.getOrDefault(FBContent.DC_TYPE_TANK_CONTENTS, SimpleFluidContent.EMPTY);
        if (!content.isEmpty())
        {
            fluidHandler.setContent(content);
        }
    }

    @Override
    protected void collectMiscComponents(DataComponentMap.Builder builder)
    {
        FluidResource contents = fluidHandler.getResource(0);
        if (!contents.isEmpty())
        {
            int amount = fluidHandler.getAmountAsInt(0);
            builder.set(FBContent.DC_TYPE_TANK_CONTENTS, SimpleFluidContent.copyOf(contents.toStack(amount)));
        }
    }

    @Override
    public void loadAdditional(ValueInput valueInput)
    {
        super.loadAdditional(valueInput);
        fluidHandler.deserialize(valueInput);
    }

    @Override
    public void saveAdditional(ValueOutput valueOutput)
    {
        super.saveAdditional(valueOutput);
        fluidHandler.serialize(valueOutput);
    }
}
