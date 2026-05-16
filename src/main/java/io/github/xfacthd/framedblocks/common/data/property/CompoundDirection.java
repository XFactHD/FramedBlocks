package io.github.xfacthd.framedblocks.common.data.property;

import io.github.xfacthd.framedblocks.api.block.item.placement.PropertyLabels;
import io.github.xfacthd.framedblocks.api.block.item.placement.PropertyPrinter;
import io.github.xfacthd.framedblocks.api.block.item.placement.ValueOrders;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.api.util.text.ValuePrinters;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public enum CompoundDirection implements StringRepresentable {
    DOWN_NORTH  (Direction.DOWN, Direction.NORTH),
    DOWN_SOUTH  (Direction.DOWN, Direction.SOUTH),
    DOWN_WEST   (Direction.DOWN, Direction.WEST),
    DOWN_EAST   (Direction.DOWN, Direction.EAST),

    UP_NORTH    (Direction.UP, Direction.NORTH),
    UP_SOUTH    (Direction.UP, Direction.SOUTH),
    UP_WEST     (Direction.UP, Direction.WEST),
    UP_EAST     (Direction.UP, Direction.EAST),

    NORTH_DOWN  (Direction.NORTH, Direction.DOWN),
    NORTH_UP    (Direction.NORTH, Direction.UP),
    NORTH_WEST  (Direction.NORTH, Direction.WEST),
    NORTH_EAST  (Direction.NORTH, Direction.EAST),

    SOUTH_DOWN  (Direction.SOUTH, Direction.DOWN),
    SOUTH_UP    (Direction.SOUTH, Direction.UP),
    SOUTH_WEST  (Direction.SOUTH, Direction.WEST),
    SOUTH_EAST  (Direction.SOUTH, Direction.EAST),

    WEST_DOWN   (Direction.WEST, Direction.DOWN),
    WEST_UP     (Direction.WEST, Direction.UP),
    WEST_NORTH  (Direction.WEST, Direction.NORTH),
    WEST_SOUTH  (Direction.WEST, Direction.SOUTH),

    EAST_DOWN   (Direction.EAST, Direction.DOWN),
    EAST_UP     (Direction.EAST, Direction.UP),
    EAST_NORTH  (Direction.EAST, Direction.NORTH),
    EAST_SOUTH  (Direction.EAST, Direction.SOUTH),
    ;

    private static final CompoundDirection[][] FROM_DIRS = makeDirTable();
    public static final int COUNT = values().length;
    private static final List<Direction> ORIENTATION_CYCLE_ORDER = List.of(
            Direction.UP,
            Direction.NORTH,
            Direction.WEST,
            Direction.DOWN,
            Direction.SOUTH,
            Direction.EAST
    );
    public static final List<CompoundDirection> CYCLE_ORDER = Util.make(() -> {
        List<CompoundDirection> values = new ArrayList<>(List.of(values()));
        values.sort(Comparator.comparingInt(cmpDir -> {
            int directionIdx = ValueOrders.FACING.indexOf(cmpDir.direction);
            int orientationIdx;
            if (DirUtils.isY(cmpDir.direction)) {
                orientationIdx = ValueOrders.FACING.indexOf(cmpDir.orientation);
            } else if (DirUtils.isPositive(cmpDir.direction.getClockWise())) {
                orientationIdx = ORIENTATION_CYCLE_ORDER.indexOf(cmpDir.orientation);
            } else {
                Direction orientation = DirUtils.isY(cmpDir.orientation) ? cmpDir.orientation : cmpDir.orientation.getOpposite();
                orientationIdx = ORIENTATION_CYCLE_ORDER.indexOf(orientation);
            }
            return directionIdx * 6 + orientationIdx;
        }));
        return values;
    });
    public static final PropertyPrinter<CompoundDirection> PRINTER = (value, out, defaultValueColor) -> {
        out.accept(PropertyLabels.FACING, ValuePrinters.DIRECTION.printStyled(value.direction(), defaultValueColor));
        out.accept(PropertyLabels.ORIENTATION, ValuePrinters.DIRECTION.printStyled(value.orientation(), defaultValueColor));
    };

    private final String name = toString().toLowerCase(Locale.ROOT);
    private final Direction direction;
    private final Direction orientation;

    CompoundDirection(Direction direction, Direction orientation) {
        this.direction = direction;
        this.orientation = orientation;
    }

    public Direction direction() {
        return direction;
    }

    public Direction orientation() {
        return orientation;
    }

    public CompoundDirection rotate(Rotation rot) {
        if (rot == Rotation.NONE) {
            return this;
        }

        if (DirUtils.isY(direction)) {
            return of(direction, rot.rotate(orientation));
        }

        Direction newOrientation = orientation;
        if (orientation.getAxis() != Direction.Axis.Y) {
            newOrientation = rot.rotate(orientation);
        }
        return of(rot.rotate(direction), newOrientation);
    }

    public CompoundDirection mirror(Mirror mirror) {
        if (mirror == Mirror.NONE) {
            return this;
        }

        Direction.Axis mirrorAxis = DirUtils.getMirrorAxis(mirror);
        return of(
                direction.getAxis() == mirrorAxis ? direction.getOpposite() : direction,
                orientation.getAxis() == mirrorAxis ? orientation.getOpposite() : orientation
        );
    }

    public CompoundDirection rotateOrientation(RotationDirection dir) {
        if (!DirUtils.isY(direction) && !DirUtils.isPositive(direction)) {
            dir = dir.getOpposite();
        }
        return of(direction, switch (dir) {
            case CLOCKWISE -> orientation.getClockWise(direction.getAxis());
            case COUNTERCLOCKWISE -> orientation.getCounterClockWise(direction.getAxis());
        });
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static CompoundDirection of(Direction direction, Direction orientation) {
        CompoundDirection dirAxis = FROM_DIRS[direction.ordinal()][orientation.ordinal()];
        if (dirAxis == null) {
            throw new IllegalArgumentException(
                    "Invalid direction pair! Direction: " + direction + ", Orientation: " + orientation
            );
        }
        return dirAxis;
    }

    private static CompoundDirection[][] makeDirTable() {
        CompoundDirection[][] table = new CompoundDirection[6][6];
        for (CompoundDirection cmpDir : values()) {
            Direction direction = cmpDir.direction;
            Direction orientation = cmpDir.orientation;
            table[direction.ordinal()][orientation.ordinal()] = cmpDir;
        }
        return table;
    }
}
