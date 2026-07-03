package io.github.xfacthd.framedblocks.api.render.outline;

import io.github.xfacthd.framedblocks.api.block.IBlockType;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

/// Event for registering [OutlineRenderer]s for framed blocks.
///
/// Fired on the mod event bus only on the physical client.
public final class RegisterOutlineRenderersEvent extends Event implements IModBusEvent {
    private final BiConsumer<IBlockType, OutlineRenderer<?>> registrar;

    @ApiStatus.Internal
    public RegisterOutlineRenderersEvent(BiConsumer<IBlockType, OutlineRenderer<?>> registrar) {
        this.registrar = registrar;
    }

    /// Register an outline renderer for the given block type.
    /// The given type must return `true` from [IBlockType#hasSpecialOutline()].
    ///
    /// @param type     The type to register the renderer for
    /// @param renderer The outline renderer to register
    public void register(IBlockType type, OutlineRenderer<?> renderer) {
        registrar.accept(type, renderer);
    }

    /// Register an outline renderer for the given block which derives the lines from the
    /// block's model. The given block must implement [IFramedBlock] and its block type
    /// must return `true` from [IBlockType#hasSpecialOutline()].
    ///
    /// @param block The block to register the renderer for
    public void registerModelBased(Block block) {
        if (!(block instanceof IFramedBlock framedBlock)) {
            throw new IllegalArgumentException("Provided block must implement IFramedBlock");
        }
        register(framedBlock.getBlockType(), InternalClientAPI.INSTANCE.createModelBasedOutlineRenderer(block));
    }
}
