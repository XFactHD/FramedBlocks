package xfacthd.framedblocks.api.render.debug;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;

import java.util.function.BiConsumer;

/**
 * Fired for attaching {@link BlockDebugRenderer}s to {@link BlockEntityType}s.
 * <p>
 * This event is only fired in a development environment. Any {@link BlockEntityType} with at least one
 * {@linkplain BlockDebugRenderer#isEnabled() enabled} {@link BlockDebugRenderer} attached to it will have a
 * {@link BlockEntityRenderer} assigned to it which will override any BER previously attached to this type
 */
public final class AttachDebugRenderersEvent extends Event implements IModBusEvent
{
    private final BiConsumer<BlockEntityType<? extends FramedBlockEntity>, BlockDebugRenderer<?>> registrar;

    @ApiStatus.Internal
    public AttachDebugRenderersEvent(BiConsumer<BlockEntityType<? extends FramedBlockEntity>, BlockDebugRenderer<?>> registrar)
    {
        this.registrar = registrar;
    }

    public <RT extends FramedBlockEntity, BT extends RT> void attach(BlockEntityType<BT> type, BlockDebugRenderer<RT> renderer)
    {
        registrar.accept(type, renderer);
    }
}
