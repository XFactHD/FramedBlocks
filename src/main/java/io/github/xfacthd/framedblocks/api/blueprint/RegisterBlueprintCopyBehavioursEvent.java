package io.github.xfacthd.framedblocks.api.blueprint;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.BiConsumer;

/// Event for registering custom [BlueprintCopyBehaviour]s for framed blocks.
///
/// Fired on the mod event bus on both physical sides.
public final class RegisterBlueprintCopyBehavioursEvent extends Event implements IModBusEvent {
    private final BiConsumer<BlueprintCopyBehaviour, Block[]> registrar;

    @ApiStatus.Internal
    public RegisterBlueprintCopyBehavioursEvent(BiConsumer<BlueprintCopyBehaviour, Block[]> registrar) {
        this.registrar = registrar;
    }

    /// Register a custom copy behavior for the given blocks.
    ///
    /// @param behaviour The behavior to register
    /// @param blocks    The blocks to register the behavior for
    public void register(BlueprintCopyBehaviour behaviour, Block... blocks) {
        registrar.accept(behaviour, blocks);
    }

    /// Register a custom copy behavior for the given block.
    ///
    /// @param behaviour The behavior to register
    /// @param block     The block to register the behavior for
    public void register(BlueprintCopyBehaviour behaviour, Holder<Block> block) {
        register(behaviour, block.value());
    }

    /// Register a custom copy behavior for the given blocks.
    ///
    /// @param behaviour The behavior to register
    /// @param blocks    The blocks to register the behavior for
    public void register(BlueprintCopyBehaviour behaviour, List<Holder<Block>> blocks) {
        register(behaviour, blocks.stream().map(Holder::value).toArray(Block[]::new));
    }
}
