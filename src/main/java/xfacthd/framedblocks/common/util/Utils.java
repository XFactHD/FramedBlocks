package xfacthd.framedblocks.common.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.*;
import net.minecraft.util.Direction;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import xfacthd.framedblocks.FramedBlocks;

public class Utils
{
    public static final ITag.INamedTag<Block> FRAMEABLE = BlockTags.makeWrapperTag(FramedBlocks.MODID + ":frameable");
    public static final ITag.INamedTag<Block> BLACKLIST = BlockTags.makeWrapperTag(FramedBlocks.MODID + ":blacklisted");
    public static final ITag.INamedTag<Item> WRENCH = ItemTags.makeWrapperTag("forge:tools/wrench");

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape)
    {
        if (from.getAxis() == Direction.Axis.Y || to.getAxis() == Direction.Axis.Y) { throw new IllegalArgumentException("Invalid Direction!"); }
        if (from == to) { return shape; }

        VoxelShape[] buffer = new VoxelShape[] { shape, VoxelShapes.empty() };

        int times = (to.getHorizontalIndex() - from.getHorizontalIndex() + 4) % 4;
        for (int i = 0; i < times; i++)
        {
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.or(
                    buffer[1],
                    VoxelShapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)
            ));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }

        return buffer[0];
    }

    public static Vector3d fraction(Vector3d vec)
    {
        return new Vector3d(
                vec.getX() - Math.floor(vec.getX()),
                vec.getY() - Math.floor(vec.getY()),
                vec.getZ() - Math.floor(vec.getZ())
        );
    }

    public static void enqueueImmediateTask(IWorld world, Runnable task, boolean allowClient)
    {
        if (world.isRemote() && allowClient)
        {
            task.run();
        }
        else
        {
            enqueueTask(world, task, 0);
        }
    }

    public static void enqueueTask(IWorld world, Runnable task, int delay)
    {
        if (!(world instanceof ServerWorld))
        {
            throw new IllegalArgumentException("Utils#enqueueTask() called with a non-ServerWorld");
        }

        MinecraftServer server = ((ServerWorld) world).getServer();
        server.enqueue(new TickDelayedTask(server.getTickCounter() + delay, task));
    }
}