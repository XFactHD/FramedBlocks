package io.github.xfacthd.framedblocks.common.util;

import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class FramedUtils {
    private static final Lazy<Set<Item>> RAIL_ITEMS = Lazy.of(() -> {
        Set<Item> items = Collections.newSetFromMap(new IdentityHashMap<>());
        items.addAll(Set.of(
                Items.RAIL,
                Items.POWERED_RAIL,
                Items.DETECTOR_RAIL,
                Items.ACTIVATOR_RAIL,
                FBContent.BLOCK_FRAMED_FANCY_RAIL.value().asItem(),
                FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL.value().asItem(),
                FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL.value().asItem(),
                FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL.value().asItem()
        ));
        return items;
    });
    private static final Lazy<Map<Item, Block>> RAIL_SLOPE_BLOCKS = Lazy.of(() -> new IdentityHashMap<>(Map.of(
            Items.RAIL, FBContent.BLOCK_FRAMED_RAIL_SLOPE.value(),
            Items.POWERED_RAIL, FBContent.BLOCK_FRAMED_POWERED_RAIL_SLOPE.value(),
            Items.DETECTOR_RAIL, FBContent.BLOCK_FRAMED_DETECTOR_RAIL_SLOPE.value(),
            Items.ACTIVATOR_RAIL, FBContent.BLOCK_FRAMED_ACTIVATOR_RAIL_SLOPE.value(),
            FBContent.BLOCK_FRAMED_FANCY_RAIL.value().asItem(), FBContent.BLOCK_FRAMED_FANCY_RAIL_SLOPE.value(),
            FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL.value().asItem(), FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL_SLOPE.value(),
            FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL.value().asItem(), FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL_SLOPE.value(),
            FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL.value().asItem(), FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE.value()
    )));

    public static boolean isRailItem(Item item) {
        return RAIL_ITEMS.get().contains(item);
    }

    public static Block getRailSlopeBlock(Item item) {
        Block railSlope = RAIL_SLOPE_BLOCKS.get().get(item);
        if (railSlope == null) {
            throw new IllegalStateException("Invalid rail item: " + item);
        }
        return railSlope;
    }

    public static RailShape getAscendingRailShapeFromDirection(Direction dir) {
        return switch (dir) {
            case NORTH -> RailShape.ASCENDING_NORTH;
            case EAST -> RailShape.ASCENDING_EAST;
            case SOUTH -> RailShape.ASCENDING_SOUTH;
            case WEST -> RailShape.ASCENDING_WEST;
            default -> throw new IllegalArgumentException("Invalid facing " + dir);
        };
    }

    public static Direction getDirectionFromAscendingRailShape(RailShape shape) {
        return switch (shape) {
            case ASCENDING_NORTH -> Direction.NORTH;
            case ASCENDING_EAST -> Direction.EAST;
            case ASCENDING_SOUTH -> Direction.SOUTH;
            case ASCENDING_WEST -> Direction.WEST;
            default -> throw new IllegalArgumentException("Invalid shape " + shape);
        };
    }

    public static Direction getDirectionFromStraightRailShape(RailShape shape) {
        return switch (shape) {
            case NORTH_SOUTH, ASCENDING_NORTH, NORTH_WEST, NORTH_EAST -> Direction.NORTH;
            case EAST_WEST, ASCENDING_WEST -> Direction.WEST;
            case ASCENDING_EAST -> Direction.EAST;
            case ASCENDING_SOUTH, SOUTH_EAST, SOUTH_WEST -> Direction.SOUTH;
        };
    }

    public static void enqueueImmediateTask(LevelAccessor level, Runnable task, boolean allowClient) {
        if (level.isClientSide() && allowClient) {
            task.run();
        } else {
            enqueueTask(level, task, 0);
        }
    }

    public static void enqueueTask(LevelAccessor level, Runnable task, int delay) {
        if (!(level instanceof ServerLevel slevel)) {
            throw new IllegalArgumentException("Utils#enqueueTask() called with a non-ServerWorld");
        }

        MinecraftServer server = slevel.getServer();
        server.schedule(new TickTask(server.getTickCount() + delay, task));
    }

    public static void addPlayerInvSlots(Consumer<Slot> slotConsumer, Inventory playerInv, int x, int y) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                slotConsumer.accept(new Slot(playerInv, col + row * 9 + 9, x + col * 18, y));
            }
            y += 18;
        }

        for (int col = 0; col < 9; ++col) {
            slotConsumer.accept(new Slot(playerInv, col, x + col * 18, y + 4));
        }
    }

    private FramedUtils() { }
}
