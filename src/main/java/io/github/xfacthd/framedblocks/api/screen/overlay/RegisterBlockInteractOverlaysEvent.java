package io.github.xfacthd.framedblocks.api.screen.overlay;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

/// Event for registering custom [BlockInteractOverlay]s for interactions with framed blocks.
///
/// Fired on the mod event bus only on the physical client.
public final class RegisterBlockInteractOverlaysEvent extends Event implements IModBusEvent {
    private final BiConsumer<String, BlockInteractOverlay> registrar;

    @ApiStatus.Internal
    public RegisterBlockInteractOverlaysEvent(BiConsumer<String, BlockInteractOverlay> registrar) {
        this.registrar = registrar;
    }

    /// Register the given overlay under the given name.
    ///
    /// @param name    The name to register the overlay under
    /// @param overlay The overlay to register
    public void register(String name, BlockInteractOverlay overlay) {
        registrar.accept(name, overlay);
    }
}
