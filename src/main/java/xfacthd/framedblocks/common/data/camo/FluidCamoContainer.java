package xfacthd.framedblocks.common.data.camo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.fml.loading.FMLEnvironment;
import xfacthd.framedblocks.api.camo.*;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.common.FBContent;

import java.util.function.Consumer;

public class FluidCamoContainer extends CamoContainer
{
    private final FluidState fluidState;

    private FluidCamoContainer(FluidState fluidState)
    {
        super(fluidState.createLegacyBlock().setValue(BlockStateProperties.LEVEL, 8));
        this.fluidState = fluidState;
    }

    @Override
    public int getColor(BlockAndTintGetter level, BlockPos pos, int tintIdx)
    {
        if (FMLEnvironment.dist.isClient())
        {
            return ClientUtils.getFluidColor(level, pos, fluidState);
        }
        throw new UnsupportedOperationException("Block color is not available on the server!");
    }

    @Override
    public ItemStack toItemStack(ItemStack stack)
    {
        if (stack.isEmpty())
        {
            return ItemStack.EMPTY;
        }

        IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (handler == null)
        {
            return ItemStack.EMPTY;
        }

        FluidStack fluid = new FluidStack(fluidState.getType(), FluidType.BUCKET_VOLUME);
        if (handler.fill(fluid, IFluidHandler.FluidAction.SIMULATE) == FluidType.BUCKET_VOLUME)
        {
            handler.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
            return handler.getContainer();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public Fluid getFluid()
    {
        return fluidState.getType();
    }

    @Override
    public boolean canRotateCamo()
    {
        return false;
    }

    @Override
    public boolean rotateCamo()
    {
        return false;
    }

    @Override
    public SoundType getSoundType()
    {
        //TODO: build custom sound type suitable for the contained fluid
        return super.getSoundType();
    }

    @Override
    public boolean equals(Object o)
    {
        return this == o || (o != null && getClass() == o.getClass() && state == ((CamoContainer) o).getState());
    }

    @Override
    public int hashCode()
    {
        return state.hashCode();
    }

    @Override
    public CamoContainerType getType()
    {
        return CamoContainerType.FLUID;
    }

    @Override
    public CamoContainerFactory getFactory()
    {
        return FBContent.FACTORY_FLUID.value();
    }

    @Override
    public void save(CompoundTag tag)
    {
        tag.put("fluid", NbtUtils.writeFluidState(fluidState));
    }

    @Override
    public void toNetwork(CompoundTag tag)
    {
        tag.putInt("fluid", BuiltInRegistries.FLUID.getId(fluidState.getType()));
    }



    public static final class Factory extends CamoContainerFactory
    {
        @Override
        public CamoContainer fromNbt(CompoundTag tag)
        {
            FluidState fluidState = Utils.readFluidStateFromNbt(tag.getCompound("fluid"));
            return new FluidCamoContainer(fluidState);
        }

        @Override
        public CamoContainer fromNetwork(CompoundTag tag)
        {
            FluidState fluidState = BuiltInRegistries.FLUID.byId(tag.getInt("fluid")).defaultFluidState();
            return new FluidCamoContainer(fluidState);
        }

        @Override
        public CamoContainer fromItem(ItemStack stack)
        {
            IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if (handler == null)
            {
                return EmptyCamoContainer.EMPTY;
            }

            FluidStack fluid = handler.getFluidInTank(0);

            FluidState state = fluid.getFluid().defaultFluidState();
            if (!state.isEmpty())
            {
                int amount = FluidType.BUCKET_VOLUME;
                if (fluid.getAmount() >= amount && handler.drain(amount, IFluidHandler.FluidAction.SIMULATE).getAmount() == amount)
                {
                    return new FluidCamoContainer(state);
                }
            }
            return EmptyCamoContainer.EMPTY;
        }

        @Override
        public void registerTriggerItems(Consumer<Item> registrar) { }
    }
}
