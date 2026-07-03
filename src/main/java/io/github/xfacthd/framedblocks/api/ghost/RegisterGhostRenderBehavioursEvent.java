package io.github.xfacthd.framedblocks.api.ghost;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.BiConsumer;

/// Event for registering custom [GhostRenderBehaviour]s for framed blocks.
///
/// Fired on the mod event bus only on the physical client.
public final class RegisterGhostRenderBehavioursEvent extends Event implements IModBusEvent {
    private final BiConsumer<GhostRenderBehaviour, Block[]> blockRegistrar;
    private final BiConsumer<GhostRenderBehaviour, Item[]> itemRegistrar;

    @ApiStatus.Internal
    public RegisterGhostRenderBehavioursEvent(
            BiConsumer<GhostRenderBehaviour, Block[]> blockRegistrar,
            BiConsumer<GhostRenderBehaviour, Item[]> itemRegistrar
    ) {
        this.blockRegistrar = blockRegistrar;
        this.itemRegistrar = itemRegistrar;
    }

    /// Register a custom behavior for the given blocks.
    ///
    /// @param behavior The behavior to register
    /// @param blocks   The blocks to register the behavior for
    public void registerBlocks(GhostRenderBehaviour behavior, Block... blocks) {
        blockRegistrar.accept(behavior, blocks);
    }

    /// Register a custom behavior for the given block.
    ///
    /// @param behavior The behavior to register
    /// @param block    The block to register the behavior for
    public void registerBlock(GhostRenderBehaviour behavior, Holder<Block> block) {
        registerBlocks(behavior, block.value());
    }

    /// Register a custom behavior for the given blocks.
    ///
    /// @param behavior The behavior to register
    /// @param blocks   The blocks to register the behavior for
    public void registerBlocks(GhostRenderBehaviour behavior, List<Holder<Block>> blocks) {
        registerBlocks(behavior, blocks.stream().map(Holder::value).toArray(Block[]::new));
    }

    /// Register a custom behavior for the given items.
    ///
    /// @param behavior The behavior to register
    /// @param items    The items to register the behavior for
    public void registerItems(GhostRenderBehaviour behavior, Item... items) {
        itemRegistrar.accept(behavior, items);
    }

    /// Register a custom behavior for the given item.
    ///
    /// @param behavior The behavior to register
    /// @param item     The item to register the behavior for
    public void registerItem(GhostRenderBehaviour behavior, Holder<Item> item) {
        registerItems(behavior, item.value());
    }

    /// Register a custom behavior for the given items.
    ///
    /// @param behavior The behavior to register
    /// @param items    The items to register the behavior for
    public void registerItems(GhostRenderBehaviour behavior, List<Holder<Item>> items) {
        registerItems(behavior, items.stream().map(Holder::value).toArray(Item[]::new));
    }
}
