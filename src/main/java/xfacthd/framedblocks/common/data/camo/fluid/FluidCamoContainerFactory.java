package xfacthd.framedblocks.common.data.camo.fluid;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.api.camo.TriggerRegistrar;
import xfacthd.framedblocks.api.util.CamoMessageVerbosity;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.config.ServerConfig;

public final class FluidCamoContainerFactory extends CamoContainerFactory<FluidCamoContainer>
{
    private static final Codec<FluidCamoContainer> CODEC = BuiltInRegistries.FLUID.byNameCodec()
            .xmap(FluidCamoContainer::new, FluidCamoContainer::getFluid);

    @Override // TODO 1.20.5: write raw fluid instead of fluidstate
    protected void writeToDisk(CompoundTag tag, FluidCamoContainer container)
    {
        FluidState fluidState = container.getFluid().defaultFluidState();
        tag.put("fluid", NbtUtils.writeFluidState(fluidState));
    }

    @Override // TODO 1.20.5: read raw fluid instead of fluidstate
    protected FluidCamoContainer readFromDisk(CompoundTag tag)
    {
        FluidState fluidState = Utils.readFluidStateFromNbt(tag.getCompound("fluid"));
        return new FluidCamoContainer(fluidState.getType());
    }

    @Override
    protected void writeToNetwork(CompoundTag tag, FluidCamoContainer container)
    {
        Fluid fluid = container.getFluid();
        tag.putInt("fluid", BuiltInRegistries.FLUID.getId(fluid));
    }

    @Override
    protected FluidCamoContainer readFromNetwork(CompoundTag tag)
    {
        Fluid fluid = BuiltInRegistries.FLUID.byId(tag.getInt("fluid"));
        return new FluidCamoContainer(fluid);
    }

    @Override
    @Nullable
    public FluidCamoContainer applyCamo(Level level, BlockPos pos, Player player, ItemStack stack)
    {
        IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (handler == null || handler.getTanks() <= 0)
        {
            return null;
        }

        FluidStack fluid = handler.getFluidInTank(0);
        if (!isValidFluid(fluid.getFluid(), player))
        {
            return null;
        }

        if (!player.isCreative() && ServerConfig.VIEW.shouldConsumeCamoItem())
        {
            fluid = fluid.copyWithAmount(FluidType.BUCKET_VOLUME);
            if (handler.drain(fluid, IFluidHandler.FluidAction.SIMULATE).getAmount() != fluid.getAmount())
            {
                return null;
            }

            if (!level.isClientSide())
            {
                handler.drain(fluid, IFluidHandler.FluidAction.EXECUTE);
                ItemStack result = handler.getContainer();
                if (result != stack) // Container holds fluid by type (i.e. bucket) -> got a new stack
                {
                    stack.shrink(1);
                    if (!result.isEmpty() && !player.getInventory().add(result))
                    {
                        player.drop(result, false);
                    }
                    player.getInventory().setChanged();
                }
            }
        }

        return new FluidCamoContainer(fluid.getFluid());
    }

    @Override
    public boolean removeCamo(Level level, BlockPos pos, Player player, ItemStack stack, FluidCamoContainer container)
    {
        if (stack.isEmpty())
        {
            return false;
        }

        IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (handler == null)
        {
            return false;
        }

        FluidStack fluid = new FluidStack(container.getFluid(), FluidType.BUCKET_VOLUME);
        if (handler.fill(fluid, IFluidHandler.FluidAction.SIMULATE) == FluidType.BUCKET_VOLUME)
        {
            if (!player.isCreative() && ServerConfig.VIEW.shouldConsumeCamoItem())
            {
                handler.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                ItemStack result = handler.getContainer();
                if (result != stack) // Container holds fluid by type (i.e. bucket) -> got a new stack
                {
                    stack.shrink(1);
                    if (!result.isEmpty() && !player.getInventory().add(result))
                    {
                        player.drop(result, false);
                    }
                    player.getInventory().setChanged();
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean canTriviallyConvertToItemStack()
    {
        return false;
    }

    @Override
    public ItemStack dropCamo(FluidCamoContainer container)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean validateCamo(FluidCamoContainer container)
    {
        return isValidFluid(container.getFluid(), null);
    }

    private static boolean isValidFluid(Fluid fluid, @Nullable Player player)
    {
        if (BuiltInRegistries.FLUID.wrapAsHolder(fluid).is(Utils.FLUID_BLACKLIST))
        {
            displayValidationMessage(player, MSG_BLACKLISTED, CamoMessageVerbosity.DEFAULT);
            return false;
        }
        return true;
    }

    @Override
    public Codec<FluidCamoContainer> codec()
    {
        return CODEC;
    }

    @Override
    public void registerTriggerItems(TriggerRegistrar registrar) { }
}
