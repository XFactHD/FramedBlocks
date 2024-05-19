package xfacthd.framedblocks.api.ghost;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.BiConsumer;

public final class RegisterGhostRenderBehavioursEvent extends Event implements IModBusEvent
{
    private final BiConsumer<GhostRenderBehaviour, Block[]> blockRegistrar;
    private final BiConsumer<GhostRenderBehaviour, Item[]> itemRegistrar;

    @ApiStatus.Internal
    public RegisterGhostRenderBehavioursEvent(
            BiConsumer<GhostRenderBehaviour, Block[]> blockRegistrar,
            BiConsumer<GhostRenderBehaviour, Item[]> itemRegistrar
    )
    {
        this.blockRegistrar = blockRegistrar;
        this.itemRegistrar = itemRegistrar;
    }

    /**
     * Register a custom {@link GhostRenderBehaviour} for the given {@link Block}s
     */
    public void registerBlocks(GhostRenderBehaviour behaviour, Block... blocks)
    {
        blockRegistrar.accept(behaviour, blocks);
    }

    /**
     * Register a custom {@link GhostRenderBehaviour} for the given {@link Block}
     */
    public void registerBlock(GhostRenderBehaviour behaviour, Holder<Block> block)
    {
        registerBlocks(behaviour, block.value());
    }

    /**
     * Register a custom {@link GhostRenderBehaviour} for the given {@link Block}s
     */
    public void registerBlocks(GhostRenderBehaviour behaviour, List<Holder<Block>> blocks)
    {
        registerBlocks(behaviour, blocks.stream().map(Holder::value).toArray(Block[]::new));
    }

    /**
     * Register a custom {@link GhostRenderBehaviour} for the given {@link Item}s
     */
    public void registerItems(GhostRenderBehaviour behaviour, Item... items)
    {
        itemRegistrar.accept(behaviour, items);
    }

    /**
     * Register a custom {@link GhostRenderBehaviour} for the given {@link Item}
     */
    public void registerItem(GhostRenderBehaviour behaviour, Holder<Item> item)
    {
        registerItems(behaviour, item.value());
    }

    /**
     * Register a custom {@link GhostRenderBehaviour} for the given {@link Item}s
     */
    public void registerItems(GhostRenderBehaviour behaviour, List<Holder<Item>> items)
    {
        registerItems(behaviour, items.stream().map(Holder::value).toArray(Item[]::new));
    }
}
