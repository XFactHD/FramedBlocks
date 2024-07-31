package xfacthd.framedblocks.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.*;

public final class ClientTaskQueue
{
    private static final List<ClientTask> tasks = new ArrayList<>();
    private static ResourceKey<Level> lastDimension = null;

    public static void enqueueClientTask(long delay, Runnable task)
    {
        if (delay == 0)
        {
            Minecraft.getInstance().tell(task);
            return;
        }

        //noinspection ConstantConditions
        long time = Minecraft.getInstance().level.getGameTime() + delay;
        tasks.add(new ClientTask(time, task));
    }

    public static void onClientTick(@SuppressWarnings("unused") ClientTickEvent.Post event)
    {
        if (tasks.isEmpty()) { return; }

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
