package io.github.xfacthd.framedblocks.common.data.camo.fluid;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.camo.CamoCraftingHandler;
import io.github.xfacthd.framedblocks.api.camo.TriggerRegistrar;
import io.github.xfacthd.framedblocks.api.util.CamoMessageVerbosity;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.common.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

public final class FluidCamoContainerFactory extends CamoContainerFactory<FluidCamoContainer> {
    private static final MapCodec<FluidCamoContainer> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidCamoContainer::getFluid),
            Direction.CODEC.optionalFieldOf("flow_dir", Direction.DOWN).forGetter(FluidCamoContainer::getFlowDirection)
    ).apply(inst, FluidCamoContainer::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidCamoContainer> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.FLUID),
            FluidCamoContainer::getFluid,
            Direction.STREAM_CODEC,
            FluidCamoContainer::getFlowDirection,
            FluidCamoContainer::new
    );

    @Override
    protected void writeToNetwork(ValueOutput valueOutput, FluidCamoContainer container) {
        Fluid fluid = container.getFluid();
        valueOutput.putInt("fluid", BuiltInRegistries.FLUID.getId(fluid));
        valueOutput.store("flow_dir", Direction.CODEC, container.getFlowDirection());
    }

    @Override
    protected FluidCamoContainer readFromNetwork(ValueInput valueInput) {
        Fluid fluid = BuiltInRegistries.FLUID.byId(valueInput.getIntOr("fluid", -1));
        Direction facing = valueInput.read("flow_dir", Direction.CODEC).orElse(Direction.DOWN);
        return new FluidCamoContainer(fluid, facing);
    }

    @Override
    public @Nullable FluidCamoContainer applyCamo(Level level, BlockPos pos, Player player, ItemAccess itemAccess) {
        return applyCamo(itemAccess, player, !player.isCreative(), !player.level().isClientSide());
    }

    static @Nullable FluidCamoContainer applyCamo(ItemAccess itemAccess, @Nullable Player player, boolean consume, boolean commit) {
        ResourceHandler<FluidResource> handler = itemAccess.getCapability(Capabilities.Fluid.ITEM);
        if (handler == null || handler.size() <= 0) {
            return null;
        }

        for (int tank = 0; tank < handler.size(); tank++) {
            FluidResource resource = handler.getResource(tank);
            if (!isValidFluid(resource.getFluid(), player) || !resource.isComponentsPatchEmpty()) {
                continue;
            }

            if (consume && ServerConfig.VIEW.shouldConsumeCamoItem()) {
                try (Transaction tx = Transaction.open(null)) {
                    if (handler.extract(tank, resource, FluidType.BUCKET_VOLUME, tx) != FluidType.BUCKET_VOLUME) {
                        continue;
                    }
                    if (commit) {
                        tx.commit();
                    }
                }
            }

            return new FluidCamoContainer(resource.getFluid(), Direction.DOWN);
        }
        return null;
    }

    @Override
    public boolean removeCamo(Level level, BlockPos pos, Player player, ItemAccess itemAccess, FluidCamoContainer container) {
        if (itemAccess.getResource().isEmpty()) {
            return false;
        }

        ResourceHandler<FluidResource> handler = itemAccess.getCapability(Capabilities.Fluid.ITEM);
        if (handler == null) {
            return false;
        }

        FluidResource fluid = FluidResource.of(container.getFluid());
        if (!isValidForHandler(handler, fluid)) {
            return false;
        }
        if (!player.isCreative() && ServerConfig.VIEW.shouldConsumeCamoItem()) {
            try (Transaction tx = Transaction.open(null)) {
                if (handler.insert(fluid, FluidType.BUCKET_VOLUME, tx) != FluidType.BUCKET_VOLUME) {
                    return false;
                }
                if (!level.isClientSide()) {
                    tx.commit();
                }
            }
        }
        return true;
    }

    private static boolean isValidForHandler(ResourceHandler<FluidResource> handler, FluidResource fluid) {
        for (int tank = 0; tank < handler.size(); tank++) {
            if (!handler.isValid(tank, fluid)) {
                continue;
            }

            FluidResource inTank = handler.getResource(tank);
            if (inTank.isEmpty() || inTank.equals(fluid)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canTriviallyConvertToItemStack() {
        return false;
    }

    @Override
    public ItemStack dropCamo(FluidCamoContainer container) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean validateCamo(FluidCamoContainer container) {
        return isValidFluid(container.getFluid(), null);
    }

    private static boolean isValidFluid(Fluid fluid, @Nullable Player player) {
        if (fluid == Fluids.EMPTY) {
            return false;
        }
        if (BuiltInRegistries.FLUID.wrapAsHolder(fluid).is(FramedConstants.Tags.FLUID_BLACKLIST)) {
            displayValidationMessage(player, MSG_BLACKLISTED, CamoMessageVerbosity.DEFAULT);
            return false;
        }
        return true;
    }

    @Override
    public CamoCraftingHandler<FluidCamoContainer> getCraftingHandler() {
        return FluidCamoCraftingHandler.INSTANCE;
    }

    @Override
    public MapCodec<FluidCamoContainer> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, FluidCamoContainer> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public void registerTriggerItems(TriggerRegistrar registrar) { }
}
