package io.github.xfacthd.framedblocks.common.data.property;

import io.github.xfacthd.framedblocks.api.block.item.placement.PropertyLabels;
import io.github.xfacthd.framedblocks.api.block.item.placement.PropertyPrinter;
import io.github.xfacthd.framedblocks.api.block.item.placement.ValueOrders;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
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

public enum DirectionAxis implements StringRepresentable {
    DOWN_X  (Direction.DOWN, Direction.Axis.X),
    DOWN_Z  (Direction.DOWN, Direction.Axis.Z),

    UP_X    (Direction.UP, Direction.Axis.X),
    UP_Z    (Direction.UP, Direction.Axis.Z),

    NORTH_X (Direction.NORTH, Direction.Axis.X),
    NORTH_Y (Direction.NORTH, Direction.Axis.Y),

    SOUTH_X (Direction.SOUTH, Direction.Axis.X),
    SOUTH_Y (Direction.SOUTH, Direction.Axis.Y),

    WEST_Y  (Direction.WEST, Direction.Axis.Y),
    WEST_Z  (Direction.WEST, Direction.Axis.Z),

    EAST_Y  (Direction.EAST, Direction.Axis.Y),
    EAST_Z  (Direction.EAST, Direction.Axis.Z);

    private static final DirectionAxis[][] FROM_DIR_AXIS = makeDirTable();
    public static final int COUNT = values().length;
    public static final List<DirectionAxis> CYCLE_ORDER = Util.make(() -> {
        List<DirectionAxis> values = new ArrayList<>(List.of(values()));
        values.sort(Comparator.comparingInt(dirAxis -> {
            int dirIdx = ValueOrders.FACING.indexOf(dirAxis.dir);
            int axisIdx;
            if (!DirUtils.isY(dirAxis.dir)) {
                axisIdx = dirAxis.axis == Direction.Axis.Y ? 1 : 0;
            } else {
                axisIdx = dirAxis.axis.ordinal();
            }
            return dirIdx * 3 + axisIdx;
        }));
        return values;
    });
    public static final PropertyPrinter<DirectionAxis> PRINTER = (dirAxis, out, defaultValueColor) -> {
        out.accept(PropertyLabels.FACING, ValuePrinters.DIRECTION.printStyled(dirAxis.dir, defaultValueColor));
        out.accept(PropertyLabels.AXIS, ValuePrinters.AXIS.printStyled(dirAxis.axis, defaultValueColor));
    };

    private final String name = toString().toLowerCase(Locale.ROOT);
    private final Direction dir;
    private final Direction.Axis axis;

    DirectionAxis(Direction dir, Direction.Axis axis) {
        this.dir = dir;
        this.axis = axis;
    }

    public Direction direction() {
        return dir;
    }

    public Direction.Axis axis() {
        return axis;
    }

    public DirectionAxis rotate(Rotation rot) {
        if (rot == Rotation.NONE) {
            return this;
        }

        if (DirUtils.isY(dir)) {
            if (rot == Rotation.CLOCKWISE_180) {
                return this;
            }

            return of(dir, DirUtils.getPerpendicularAxis(axis, dir.getAxis()));
        }

        Direction.Axis newAxis = axis;
        if (axis != Direction.Axis.Y && rot != Rotation.CLOCKWISE_180) {
            newAxis = DirUtils.getPerpendicularAxis(axis, Direction.Axis.Y);
        }
        return of(rot.rotate(dir), newAxis);
    }

    public DirectionAxis mirror(Mirror mirror) {
        return switch (mirror) {
            case NONE -> this;
            case FRONT_BACK -> DirUtils.isX(dir) ? of(dir.getOpposite(), axis) : this;
            case LEFT_RIGHT -> DirUtils.isZ(dir) ? of(dir.getOpposite(), axis) : this;
        };
    }

    public DirectionAxis rotateAxis() {
        return of(dir, DirUtils.getPerpendicularAxis(axis, dir.getAxis()));
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static DirectionAxis of(Direction dir, Direction.Axis axis) {
        DirectionAxis dirAxis = FROM_DIR_AXIS[dir.ordinal()][axis.ordinal()];
        if (dirAxis == null) {
            throw new IllegalArgumentException("Invalid dir/axis pair! Direction: " + dir + ", Axis: " + axis);
        }
        return dirAxis;
    }

    private static DirectionAxis[][] makeDirTable() {
        DirectionAxis[][] table = new DirectionAxis[6][3];
        for (DirectionAxis dirAxis : values()) {
            Direction dir = dirAxis.dir;
            Direction.Axis axis = dirAxis.axis;
            table[dir.ordinal()][axis.ordinal()] = dirAxis;
        }
        return table;
    }
}
