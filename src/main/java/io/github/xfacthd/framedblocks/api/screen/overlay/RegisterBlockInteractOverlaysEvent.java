package io.github.xfacthd.framedblocks.api.screen.overlay;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

public final class RegisterBlockInteractOverlaysEvent extends Event implements IModBusEvent {
    private final BiConsumer<String, BlockInteractOverlay> registrar;

    @ApiStatus.Internal
    public RegisterBlockInteractOverlaysEvent(BiConsumer<String, BlockInteractOverlay> registrar) {
        this.registrar = registrar;
    }

    public void register(String name, BlockInteractOverlay overlay) {
        registrar.accept(name, overlay);
    }
}
