package io.github.xfacthd.framedblocks.common.data.camo.fluid;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.api.camo.TriggerRegistrar;
import io.github.xfacthd.framedblocks.api.camo.resource.ResourceCamoContainerFactory;
import io.github.xfacthd.framedblocks.api.util.CamoMessageVerbosity;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jspecify.annotations.Nullable;

public final class FluidCamoContainerFactory extends ResourceCamoContainerFactory<FluidResource, FluidCamoContent, FluidCamoContainer> {
    private static final MapCodec<FluidCamoContainer> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").xmap(FluidResource::of, FluidResource::getFluid).forGetter(FluidCamoContainer::getResource),
            Direction.CODEC.optionalFieldOf("flow_dir", Direction.DOWN).forGetter(FluidCamoContainer::getFlowDirection)
    ).apply(inst, FluidCamoContainer::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidCamoContainer> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.FLUID).map(FluidResource::of, FluidResource::getFluid),
            FluidCamoContainer::getResource,
            Direction.STREAM_CODEC,
            FluidCamoContainer::getFlowDirection,
            FluidCamoContainer::new
    );

    public FluidCamoContainerFactory() {
        super(Capabilities.Fluid.ITEM, FluidType.BUCKET_VOLUME, FramedConstants.Tags.CRAFTING_BLOCKED_FLUID_CONTAINERS);
    }

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
        return new FluidCamoContainer(FluidResource.of(fluid), facing);
    }

    @Override
    protected FluidCamoContainer createContainer(FluidResource resource) {
        return new FluidCamoContainer(resource, Direction.DOWN);
    }

    @Override
    protected boolean isValidResource(FluidResource resource, @Nullable Player player) {
        if (resource.isEmpty() || !resource.isComponentsPatchEmpty()) {
            return false;
        }
        if (resource.typeHolder().is(FramedConstants.Tags.FLUID_BLACKLIST)) {
            displayValidationMessage(player, MSG_BLACKLISTED, CamoMessageVerbosity.DEFAULT);
            return false;
        }
        return true;
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
