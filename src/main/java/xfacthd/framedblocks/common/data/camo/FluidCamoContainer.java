package xfacthd.framedblocks.common.data.camo;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.*;
import xfacthd.framedblocks.api.data.*;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;

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
        return fluidState.getType().getAttributes().getColor(level, pos);
    }

    @Override
    public ItemStack toItemStack(ItemStack stack)
    {
        if (stack.isEmpty())
        {
            return ItemStack.EMPTY;
        }

        LazyOptional<IFluidHandlerItem> cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
        return cap.map(handler ->
        {
            FluidStack fluid = new FluidStack(fluidState.getType(), FluidAttributes.BUCKET_VOLUME);
            if (handler.fill(fluid, IFluidHandler.FluidAction.SIMULATE) == FluidAttributes.BUCKET_VOLUME)
            {
                if (stack.getItem() == Items.BUCKET)
                {
                    return new ItemStack(fluid.getFluid().getBucket());
                }
                else
                {
                    return stack;
                }
            }
            return ItemStack.EMPTY;
        }).orElse(ItemStack.EMPTY);
    }

    @Override
    public Fluid getFluid() { return fluidState.getType(); }

    @Override
    public ContainerType getType() { return ContainerType.FLUID; }

    @Override
    public CamoContainer.Factory getFactory() { return FBContent.factoryFluid.get(); }

    @Override
    public void save(CompoundTag tag)
    {
        tag.put("fluid", NbtUtils.writeFluidState(fluidState));
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
        public CamoContainer fromItem(ItemStack stack)
        {
            LazyOptional<IFluidHandlerItem> cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
            return cap.map(handler ->
            {
                FluidStack fluid = handler.getFluidInTank(0);

                FluidState state = fluid.getFluid().defaultFluidState();
                if (!state.isEmpty())
                {
                    int amount = FluidAttributes.BUCKET_VOLUME;
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
