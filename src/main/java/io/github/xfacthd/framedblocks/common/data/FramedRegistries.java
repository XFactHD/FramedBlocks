package io.github.xfacthd.framedblocks.common.data;

import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Consumer;

public final class FramedRegistries {
    public static final Registry<CamoContainerFactory<?>> CAMO_CONTAINER_FACTORIES = create(
            FramedConstants.Registries.CAMO_CONTAINER_FACTORY_REGISTRY_KEY,
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

    private FramedRegistries() { }
}
