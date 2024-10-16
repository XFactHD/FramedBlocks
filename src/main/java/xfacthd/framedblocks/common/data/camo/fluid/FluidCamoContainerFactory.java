package xfacthd.framedblocks.common.data.camo.fluid;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
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
    private static final MapCodec<FluidCamoContainer> CODEC = BuiltInRegistries.FLUID.byNameCodec()
            .xmap(FluidCamoContainer::new, FluidCamoContainer::getFluid).fieldOf("fluid");
    private static final StreamCodec<RegistryFriendlyByteBuf, FluidCamoContainer> STREAM_CODEC = ByteBufCodecs.registry(Registries.FLUID)
            .map(FluidCamoContainer::new, FluidCamoContainer::getFluid);

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

        for (int tank = 0; tank < handler.getTanks(); tank++)
        {
            FluidStack fluid = handler.getFluidInTank(tank);
            if (!isValidFluid(fluid.getFluid(), player))
            {
                continue;
            }

            if (!player.isCreative() && ServerConfig.VIEW.shouldConsumeCamoItem())
            {
                fluid = fluid.copyWithAmount(FluidType.BUCKET_VOLUME);
                if (handler.drain(fluid, IFluidHandler.FluidAction.SIMULATE).getAmount() != fluid.getAmount())
                {
                    continue;
                }

                if (!level.isClientSide())
                {
                    handler.drain(fluid, IFluidHandler.FluidAction.EXECUTE);
                    ItemStack result = handler.getContainer();
                    if (result != stack) // Container holds fluid by type (i.e. bucket) -> got a new stack
                    {
                        stack.shrink(1);
                        Utils.giveToPlayer(player, result, true);
                    }
                }
            }

            return new FluidCamoContainer(fluid.getFluid());
        }
        return null;
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
        if (!isValidForHandler(handler, fluid))
        {
            return false;
        }
        if (!player.isCreative() && ServerConfig.VIEW.shouldConsumeCamoItem())
        {
            if (handler.fill(fluid, IFluidHandler.FluidAction.SIMULATE) != FluidType.BUCKET_VOLUME)
            {
                return false;
            }
            if (!level.isClientSide())
            {
                handler.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                ItemStack result = handler.getContainer();
                if (result != stack) // Container holds fluid by type (i.e. bucket) -> got a new stack
                {
                    stack.shrink(1);
                    Utils.giveToPlayer(player, result, true);
                }
            }
        }
        return true;
    }

    private static boolean isValidForHandler(IFluidHandlerItem handler, FluidStack fluid)
    {
        for (int tank = 0; tank < handler.getTanks(); tank++)
        {
            if (!handler.isFluidValid(tank, fluid))
            {
                continue;
            }

            FluidStack inTank = handler.getFluidInTank(tank);
            if (inTank.isEmpty() || FluidStack.isSameFluidSameComponents(inTank, fluid))
            {
                return true;
            }
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
        if (fluid == Fluids.EMPTY)
        {
            return false;
        }
        if (BuiltInRegistries.FLUID.wrapAsHolder(fluid).is(Utils.FLUID_BLACKLIST))
        {
            displayValidationMessage(player, MSG_BLACKLISTED, CamoMessageVerbosity.DEFAULT);
            return false;
        }
        return true;
    }

    @Override
    public MapCodec<FluidCamoContainer> codec()
    {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, FluidCamoContainer> streamCodec()
    {
        return STREAM_CODEC;
    }

    @Override
    public void registerTriggerItems(TriggerRegistrar registrar) { }
}
