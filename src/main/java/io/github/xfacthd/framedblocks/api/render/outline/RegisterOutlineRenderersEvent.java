package io.github.xfacthd.framedblocks.api.render.outline;

import io.github.xfacthd.framedblocks.api.block.IBlockType;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

public final class RegisterOutlineRenderersEvent extends Event implements IModBusEvent
{
    private final BiConsumer<IBlockType, OutlineRenderer<?>> registrar;

    @ApiStatus.Internal
    public RegisterOutlineRenderersEvent(BiConsumer<IBlockType, OutlineRenderer<?>> registrar)
    {
        this.registrar = registrar;
    }

    /**
     * Register an {@link OutlineRenderer} for the given {@link IBlockType}
     *
     * @param type     The {@link IBlockType}, must return true for {@link IBlockType#hasSpecialOutline()}
     * @param renderer The {@link OutlineRenderer} to register
     */
    public void register(IBlockType type, OutlineRenderer<?> renderer)
    {
        registrar.accept(type, renderer);
    }

    /**
     * Register an {@link OutlineRenderer} for the given {@link IFramedBlock} which derives the lines from the
     * block's model.
     *
     * @param block The {@link IFramedBlock} to register the renderer for, its {@link IBlockType} must return true
     *              from {@link IBlockType#hasSpecialOutline()}
     */
    public void registerModelBased(Block block)
    {
        if (!(block instanceof IFramedBlock framedBlock))
        {
            throw new IllegalArgumentException("Provided block must implement IFramedBlock");
        }
        register(framedBlock.getBlockType(), InternalClientAPI.INSTANCE.createModelBasedOutlineRenderer(block));
    }
}
