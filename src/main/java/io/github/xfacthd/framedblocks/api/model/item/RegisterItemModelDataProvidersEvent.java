package io.github.xfacthd.framedblocks.api.model.item;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

/// Event for registering custom [ItemModelDataProvider]s for block items of framed blocks.
///
/// Fired on the mod event bus only on the physical client.
public final class RegisterItemModelDataProvidersEvent extends Event implements IModBusEvent {
    private final BiConsumer<Identifier, ItemModelDataProvider> registrar;

    @ApiStatus.Internal
    public RegisterItemModelDataProvidersEvent(BiConsumer<Identifier, ItemModelDataProvider> registrar) {
        this.registrar = registrar;
    }

    /// Register the given provider with the given ID.
    ///
    /// @param id           The ID to register the provider under
    /// @param dataProvider The provider to register
    public void register(Identifier id, ItemModelDataProvider dataProvider) {
        registrar.accept(id, dataProvider);
    }
}
