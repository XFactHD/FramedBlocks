package xfacthd.framedblocks.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public final class ClientTaskQueue
{
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



    private ClientTaskQueue() { }
}
