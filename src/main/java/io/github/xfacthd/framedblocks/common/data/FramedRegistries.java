package io.github.xfacthd.framedblocks.common.data;

import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.common.block.interactive.FramedFlowerPotBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.ModifyRegistriesEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.registries.callback.BakeCallback;

import java.util.function.Consumer;

public final class FramedRegistries {
    public static final Registry<CamoContainerFactory<?>> CAMO_CONTAINER_FACTORIES = create(
            FramedConstants.CAMO_CONTAINER_FACTORY_REGISTRY_KEY,
            builder -> builder.sync(true)
    );

    private static <T> Registry<T> create(ResourceKey<Registry<T>> key, Consumer<RegistryBuilder<T>> consumer) {
        RegistryBuilder<T> builder = new RegistryBuilder<>(key);
        consumer.accept(builder);
        return builder.create();
    }

    public static void onRegisterNewRegistries(NewRegistryEvent event) {
        event.register(CAMO_CONTAINER_FACTORIES);
    }

    public static void onModifyRegistries(ModifyRegistriesEvent event) {
        // TODO: move to common setup once NeoForge#3003 is merged
        event.getRegistry(Registries.BLOCK).addCallback((BakeCallback<Block>) _ -> FramedFlowerPotBlock.initPotMapping());
    }

    private FramedRegistries() { }
}
