package io.github.xfacthd.framedblocks.api.block.item.placement;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Half;

import java.util.List;

/// Holds sensible value cycling orders for various blockstate properties
public final class ValueOrders {
    public static final List<Direction> FACING = List.of(Direction.DOWN, Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    public static final List<Direction> FACING_HOR = List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    public static final List<Half> HALF = List.of(Half.BOTTOM, Half.TOP);

    private ValueOrders() { }
}
