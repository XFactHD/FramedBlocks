package xfacthd.framedblocks.common.util;

import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;

public final class FramedUtils
{
    public static boolean isFramedRailSlope(BlockState state)
    {
        Block block = state.getBlock();
        if (block instanceof BaseRailBlock && block instanceof IFramedBlock)
        {
            return state.hasProperty(PropertyHolder.ASCENDING_RAIL_SHAPE);
        }
        return false;
    }

    public static boolean isRailItem(Item item)
    {
        return item == Items.RAIL ||
               item == Items.POWERED_RAIL ||
               item == Items.DETECTOR_RAIL ||
               item == Items.ACTIVATOR_RAIL ||
               item == FBContent.blockFramedFancyRail.get().asItem() ||
               item == FBContent.blockFramedFancyPoweredRail.get().asItem() ||
               item == FBContent.blockFramedFancyDetectorRail.get().asItem() ||
               item == FBContent.blockFramedFancyActivatorRail.get().asItem();
    }

    public static Block getRailSlopeBlock(Item item)
    {
        if (item == Items.RAIL)
        {
            return FBContent.blockFramedRailSlope.get();
        }
        if (item == Items.POWERED_RAIL)
        {
            return FBContent.blockFramedPoweredRailSlope.get();
        }
        if (item == Items.DETECTOR_RAIL)
        {
            return FBContent.blockFramedDetectorRailSlope.get();
        }
        if (item == Items.ACTIVATOR_RAIL)
        {
            return FBContent.blockFramedActivatorRailSlope.get();
        }
        if (item == FBContent.blockFramedFancyRail.get().asItem())
        {
            return FBContent.blockFramedFancyRailSlope.get();
        }
        if (item == FBContent.blockFramedFancyPoweredRail.get().asItem())
        {
            return FBContent.blockFramedFancyPoweredRailSlope.get();
        }
        if (item == FBContent.blockFramedFancyDetectorRail.get().asItem())
        {
            return FBContent.blockFramedFancyDetectorRailSlope.get();
        }
        if (item == FBContent.blockFramedFancyActivatorRail.get().asItem())
        {
            return FBContent.blockFramedFancyActivatorRailSlope.get();
        }
        throw new IllegalStateException("Invalid rail item: " + item);
    }

    public static Direction getSlopeBlockFacing(BlockState state)
    {
        if (isFramedRailSlope(state))
        {
            return getDirectionFromAscendingRailShape(state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE));
        }
        return state.getValue(FramedProperties.FACING_HOR);
    }

    public static SlopeType getSlopeType(BlockState state)
    {
        if (isFramedRailSlope(state))
        {
            return SlopeType.BOTTOM;
        }
        return state.getValue(PropertyHolder.SLOPE_TYPE);
    }

    public static RailShape getAscendingRailShapeFromDirection(Direction dir)
    {
        return switch (dir)
        {
            case NORTH -> RailShape.ASCENDING_NORTH;
            case EAST -> RailShape.ASCENDING_EAST;
            case SOUTH -> RailShape.ASCENDING_SOUTH;
            case WEST -> RailShape.ASCENDING_WEST;
            default -> throw new IllegalArgumentException("Invalid facing " + dir);
        };
    }

    public static Direction getDirectionFromAscendingRailShape(RailShape shape)
    {
        return switch (shape)
        {
            case ASCENDING_NORTH -> Direction.NORTH;
            case ASCENDING_EAST -> Direction.EAST;
            case ASCENDING_SOUTH -> Direction.SOUTH;
            case ASCENDING_WEST -> Direction.WEST;
            default -> throw new IllegalArgumentException("Invalid shape " + shape);
        };
    }

    public static void enqueueImmediateTask(LevelAccessor level, Runnable task, boolean allowClient)
    {
        if (level.isClientSide() && allowClient)
        {
            task.run();
        }
        else
        {
            enqueueTask(level, task, 0);
        }
    }

    public static void enqueueTask(LevelAccessor level, Runnable task, int delay)
    {
        if (!(level instanceof ServerLevel slevel))
        {
            throw new IllegalArgumentException("Utils#enqueueTask() called with a non-ServerWorld");
        }

        MinecraftServer server = slevel.getServer();
        server.tell(new TickTask(server.getTickCount() + delay, task));
    }



    private FramedUtils() { }
}