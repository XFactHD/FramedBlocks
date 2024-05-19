package xfacthd.framedblocks.api.blueprint;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.BiConsumer;

public final class RegisterBlueprintCopyBehavioursEvent extends Event implements IModBusEvent
{
    private final BiConsumer<BlueprintCopyBehaviour, Block[]> registrar;

    @ApiStatus.Internal
    public RegisterBlueprintCopyBehavioursEvent(BiConsumer<BlueprintCopyBehaviour, Block[]> registrar)
    {
        this.registrar = registrar;
    }

    /**
     * Register a custom {@link BlueprintCopyBehaviour} for the given {@link Block}s
     */
    public void register(BlueprintCopyBehaviour behaviour, Block... blocks)
    {
        registrar.accept(behaviour, blocks);
    }

    /**
     * Register a custom {@link BlueprintCopyBehaviour} for the given {@link Block}
     */
    public void register(BlueprintCopyBehaviour behaviour, Holder<Block> block)
    {
        register(behaviour, block.value());
    }

    /**
     * Register a custom {@link BlueprintCopyBehaviour} for the given {@link Block}s
     */
    public void register(BlueprintCopyBehaviour behaviour, List<Holder<Block>> blocks)
    {
        register(behaviour, blocks.stream().map(Holder::value).toArray(Block[]::new));
    }
}
