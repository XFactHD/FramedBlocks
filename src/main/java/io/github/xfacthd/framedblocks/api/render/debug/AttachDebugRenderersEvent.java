package io.github.xfacthd.framedblocks.api.render.debug;

import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

/// Event for attaching [BlockDebugRenderer]s to [BlockEntityType]s.
///
/// This event is fired on the mod event bus only on the physical client and only in a development environment.
///
/// @see DebugRenderers
public final class AttachDebugRenderersEvent extends Event implements IModBusEvent {
    private final BiConsumer<BlockEntityType<? extends BlockEntity>, BlockDebugRenderer<?>> registrar;

    @ApiStatus.Internal
    public AttachDebugRenderersEvent(BiConsumer<BlockEntityType<? extends BlockEntity>, BlockDebugRenderer<?>> registrar) {
        this.registrar = registrar;
    }

    /// Attach the given renderer to the given BE type.
    ///
    /// @param type     The BE type to attach the renderer to
    /// @param renderer The renderer to attach
    public <BT extends BlockEntity & IFramedBlockEntity> void attach(BlockEntityType<BT> type, BlockDebugRenderer<? super BT> renderer) {
        registrar.accept(type, renderer);
    }
}
