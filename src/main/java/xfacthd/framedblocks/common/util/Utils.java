package xfacthd.framedblocks.common.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.loading.FMLEnvironment;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.util.ClientUtils;
import xfacthd.framedblocks.common.block.FramedRailSlopeBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.SlopeType;

public class Utils
{
    public static final ITag.INamedTag<Block> FRAMEABLE = BlockTags.bind(FramedBlocks.MODID + ":frameable");
    public static final ITag.INamedTag<Block> BLACKLIST = BlockTags.bind(FramedBlocks.MODID + ":blacklisted");
    public static final ITag.INamedTag<Item> WRENCH = ItemTags.bind("forge:tools/wrench");

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape)
    {
        if (from.getAxis() == Direction.Axis.Y || to.getAxis() == Direction.Axis.Y) { throw new IllegalArgumentException("Invalid Direction!"); }
        if (from == to) { return shape; }

        VoxelShape[] buffer = new VoxelShape[] { shape, VoxelShapes.empty() };

        int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++)
        {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.or(
                    buffer[1],
                    VoxelShapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)
            ));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }

        return buffer[0];
    }

    public static Vector3d fraction(Vector3d vec)
    {
        return new Vector3d(
                vec.x() - Math.floor(vec.x()),
                vec.y() - Math.floor(vec.y()),
                vec.z() - Math.floor(vec.z())
        );
    }

    public static void enqueueImmediateTask(IWorld world, Runnable task, boolean allowClient)
    {
        if (world.isClientSide() && allowClient)
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
        server.tell(new TickDelayedTask(server.getTickCount() + delay, task));
    }

    public static Direction getBlockFacing(BlockState state)
    {
        if (state.getBlock() instanceof FramedRailSlopeBlock)
        {
            return FramedRailSlopeBlock.directionFromShape(state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE));
        }
        return state.getValue(PropertyHolder.FACING_HOR);
    }

    public static SlopeType getSlopeType(BlockState state)
    {
        if (state.getBlock() instanceof FramedRailSlopeBlock)
        {
            return SlopeType.BOTTOM;
        }
        return state.getValue(PropertyHolder.SLOPE_TYPE);
    }

    public static TileEntity getTileEntitySafe(IBlockReader blockGetter, BlockPos pos)
    {
        if (blockGetter instanceof World)
        {
            return ((World) blockGetter).getChunkAt(pos).getBlockEntity(pos, Chunk.CreateEntityType.CHECK);
        }
        else if (blockGetter instanceof Chunk)
        {
            return ((Chunk) blockGetter).getBlockEntity(pos, Chunk.CreateEntityType.CHECK);
        }
        else if (FMLEnvironment.dist.isClient())
        {
            return ClientUtils.getTileEntitySafe(blockGetter, pos);
        }
        return null;
    }
}