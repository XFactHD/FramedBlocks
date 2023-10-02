package xfacthd.framedblocks.api.util;

import com.google.common.base.Suppliers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.event.TickEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.*;

public final class ClientUtils
{
    public static final ResourceLocation DUMMY_TEXTURE = new ResourceLocation("forge", "white");

    public static BlockEntity getBlockEntitySafe(BlockGetter blockGetter, BlockPos pos)
    {
        if (blockGetter instanceof RenderChunkRegion renderChunk)
        {
            return renderChunk.getBlockEntity(pos);
        }
        return null;
    }

    public static final Supplier<Boolean> OPTIFINE_LOADED = Suppliers.memoize(() ->
    {
        try
        {
            Class.forName("net.optifine.Config");
            return true;
        }
        catch (ClassNotFoundException ignored)
        {
            return false;
        }
    });

    public static void enqueueClientTask(Runnable task)
    {
        Minecraft.getInstance().tell(task);
    }

    public static int getBlockColor(BlockAndTintGetter level, BlockPos pos, BlockState state, int tintIdx)
    {
        return Minecraft.getInstance().getBlockColors().getColor(state, level, pos, tintIdx);
    }

    public static int getFluidColor(BlockAndTintGetter level, BlockPos pos, FluidState fluid)
    {
        return IClientFluidTypeExtensions.of(fluid).getTintColor(fluid, level, pos);
    }

    public static boolean isDummyTexture(BakedQuad quad)
    {
        return isTexture(quad, DUMMY_TEXTURE);
    }

    public static boolean isTexture(BakedQuad quad, ResourceLocation texture)
    {
        return quad.getSprite().contents().name().equals(texture);
    }

    private static final List<ClientTask> tasks = new ArrayList<>();

    public static void enqueueClientTask(long delay, Runnable task)
    {
        //noinspection ConstantConditions
        long time = Minecraft.getInstance().level.getGameTime() + delay;
        tasks.add(new ClientTask(time, task));
    }

    private static ResourceKey<Level> lastDimension = null;

    @ApiStatus.Internal
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END || tasks.isEmpty()) { return; }

        Level level = Minecraft.getInstance().level;
        if (level == null || level.dimension() != lastDimension)
        {
            lastDimension = level != null ? level.dimension() : null;
            tasks.clear(); //Clear remaining tasks from the previous level

            if (level == null)
            {
                return;
            }
        }

        Iterator<ClientTask> it = tasks.iterator();
        while (it.hasNext())
        {
            ClientTask task = it.next();
            if (level.getGameTime() >= task.time)
            {
                task.task.run();
                it.remove();
            }
        }
    }

    private record ClientTask(long time, Runnable task) { }



    private ClientUtils() { }
}