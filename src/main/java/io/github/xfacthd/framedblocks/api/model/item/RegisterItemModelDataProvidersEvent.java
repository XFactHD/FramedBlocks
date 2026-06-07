package io.github.xfacthd.framedblocks.api.model.item;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

public final class RegisterItemModelDataProvidersEvent extends Event implements IModBusEvent {
    private final BiConsumer<Identifier, ItemModelDataProvider> registrar;

    @ApiStatus.Internal
    public RegisterItemModelDataProvidersEvent(BiConsumer<Identifier, ItemModelDataProvider> registrar) {
        this.registrar = registrar;
    }

    public void register(Identifier id, ItemModelDataProvider dataProvider) {
        registrar.accept(id, dataProvider);
    }
}
