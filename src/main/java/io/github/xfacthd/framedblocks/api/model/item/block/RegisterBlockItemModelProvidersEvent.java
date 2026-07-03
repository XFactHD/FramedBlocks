package io.github.xfacthd.framedblocks.api.model.item.block;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

/// Event for registering custom [BlockItemModelProvider]s for block items of framed blocks.
///
/// Fired on the mod event bus only on the physical client.
public final class RegisterBlockItemModelProvidersEvent extends Event implements IModBusEvent {
    private final BiConsumer<Identifier, BlockItemModelProvider> registrar;

    @ApiStatus.Internal
    public RegisterBlockItemModelProvidersEvent(BiConsumer<Identifier, BlockItemModelProvider> registrar) {
        this.registrar = registrar;
    }

    /// Register the given provider with the given ID.
    ///
    /// @param id            The ID to register the provider under
    /// @param modelProvider The provider to register
    public void register(Identifier id, BlockItemModelProvider modelProvider) {
        registrar.accept(id, modelProvider);
    }
}
