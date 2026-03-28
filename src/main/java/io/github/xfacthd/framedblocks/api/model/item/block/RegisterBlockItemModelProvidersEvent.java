package io.github.xfacthd.framedblocks.api.model.item.block;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

public final class RegisterBlockItemModelProvidersEvent extends Event implements IModBusEvent {
    private final BiConsumer<Identifier, BlockItemModelProvider> registrar;

    @ApiStatus.Internal
    public RegisterBlockItemModelProvidersEvent(BiConsumer<Identifier, BlockItemModelProvider> registrar) {
        this.registrar = registrar;
    }

    public void register(Identifier id, BlockItemModelProvider tintProvider) {
        registrar.accept(id, tintProvider);
    }
}
