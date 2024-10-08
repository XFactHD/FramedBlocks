package xfacthd.framedblocks.common.data.camo;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.framedblocks.api.camo.*;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.common.FBContent;

public class FluidCamoContainer extends CamoContainer
{
    private final FluidState fluidState;

    private FluidCamoContainer(FluidState fluidState)
    {
        super(fluidState.createLegacyBlock());
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

        LazyOptional<IFluidHandlerItem> cap = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        return cap.map(handler ->
        {
            FluidStack fluid = new FluidStack(fluidState.getType(), FluidType.BUCKET_VOLUME);
            if (handler.fill(fluid, IFluidHandler.FluidAction.SIMULATE) == FluidType.BUCKET_VOLUME)
            {
                handler.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                return handler.getContainer();
            }
            return ItemStack.EMPTY;
        }).orElse(ItemStack.EMPTY);
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
        return SoundType.WET_GRASS;
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
    public ContainerType getType()
    {
        return ContainerType.FLUID;
    }

    @Override
    public CamoContainer.Factory getFactory()
    {
        return FBContent.FACTORY_FLUID.get();
    }

    @Override
    public void save(CompoundTag tag)
    {
        tag.put("fluid", NbtUtils.writeFluidState(fluidState));
    }

    @Override
    public void toNetwork(CompoundTag tag)
    {
        tag.putInt("fluid", Utils.getId(ForgeRegistries.FLUIDS, fluidState.getType()));
    }



    public static final class Factory extends CamoContainer.Factory
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
            FluidState fluidState = Utils.getValue(ForgeRegistries.FLUIDS, tag.getInt("fluid")).defaultFluidState();
            return new FluidCamoContainer(fluidState);
        }

        @Override
        public CamoContainer fromItem(ItemStack stack)
        {
            LazyOptional<IFluidHandlerItem> cap = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
            return cap.map(handler ->
            {
                FluidStack fluid = handler.getFluidInTank(0);

                FluidState state = fluid.getFluid().defaultFluidState();
                if (!state.isEmpty() && !state.createLegacyBlock().isAir())
                {
                    int amount = FluidType.BUCKET_VOLUME;
                    if (fluid.getAmount() >= amount && handler.drain(amount, IFluidHandler.FluidAction.SIMULATE).getAmount() == amount)
                    {
                        return new FluidCamoContainer(state);
                    }
                }
                return EmptyCamoContainer.EMPTY;
            }).orElse(EmptyCamoContainer.EMPTY);
        }
    }
}
