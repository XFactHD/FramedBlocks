package io.github.xfacthd.framedblocks.api.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;

final class Lookups {
    static final Direction.@Nullable Axis[] PERP_AXIS = buildPerpAxisMapping();
    static final Set<Direction.Axis>[] PERP_AXES = buildPerpAxesMapping();
    static final @Nullable Direction[] NORMALS = makeNormalMapping();
    static final Set<Direction>[] AXIS_TUBE_FACES = makeAxisTubeFaceMapping();
    static final Set<Direction>[] AXIS_CAP_FACES = makeAxisCapFaceMapping();
    static final int[] DIR_2D_VALUE_AROUND_AXIS = build2dValueMapping();
    static final Rotation[] ROTATIONS = makeDirToDirRotationMapping();

    static int makePerpAxisIndex(Direction.Axis axis1, Direction.Axis axis2) {
        return axis1.ordinal() | (axis2.ordinal() << 2);
    }

    static int makeNormalIndex(int x, int y, int z) {
        x = Math.clamp(x, -1, 1);
        y = Math.clamp(y, -1, 1);
        z = Math.clamp(z, -1, 1);
        return ((x & 0b11) | (y & 0b11) << 2 | (z & 0b11) << 4);
    }

    static int make2dValueIndex(Direction.Axis axis, Direction dir) {
        return axis.ordinal() << 3 | dir.ordinal();
    }

    static int makeDirToDirRotationIndex(Direction dirOne, Direction dirTwo) {
        return dirOne.get2DDataValue() << 2 | dirTwo.get2DDataValue();
    }

    private static Direction.@Nullable Axis[] buildPerpAxisMapping() {
        Direction.Axis[] mapping = new Direction.Axis[11];
        mapping[makePerpAxisIndex(Direction.Axis.X, Direction.Axis.Y)] = mapping[makePerpAxisIndex(Direction.Axis.Y, Direction.Axis.X)] = Direction.Axis.Z;
        mapping[makePerpAxisIndex(Direction.Axis.X, Direction.Axis.Z)] = mapping[makePerpAxisIndex(Direction.Axis.Z, Direction.Axis.X)] = Direction.Axis.Y;
        mapping[makePerpAxisIndex(Direction.Axis.Y, Direction.Axis.Z)] = mapping[makePerpAxisIndex(Direction.Axis.Z, Direction.Axis.Y)] = Direction.Axis.X;
        return mapping;
    }

    @SuppressWarnings("unchecked")
    private static Set<Direction.Axis>[] buildPerpAxesMapping() {
        Set<Direction.Axis>[] mapping = new Set[3];
        mapping[Direction.Axis.X.ordinal()] = Set.copyOf(EnumSet.complementOf(EnumSet.of(Direction.Axis.X)));
        mapping[Direction.Axis.Y.ordinal()] = Set.copyOf(EnumSet.complementOf(EnumSet.of(Direction.Axis.Y)));
        mapping[Direction.Axis.Z.ordinal()] = Set.copyOf(EnumSet.complementOf(EnumSet.of(Direction.Axis.Z)));
        return mapping;
    }

    private static @Nullable Direction[] makeNormalMapping() {
        Direction[] mapping = new Direction[64];
        for (Direction dir : DirUtils.DIRECTIONS) {
            mapping[makeNormalIndex(dir.getStepX(), dir.getStepY(), dir.getStepZ())] = dir;
        }
        return mapping;
    }

    @SuppressWarnings("unchecked")
    private static Set<Direction>[] makeAxisTubeFaceMapping() {
        Set<Direction>[] mapping = new Set[3];
        for (Direction.Axis axis : Direction.Axis.values()) {
            mapping[axis.ordinal()] = Set.copyOf(Direction.stream().filter(dir -> dir.getAxis() != axis).toList());
        }
        return mapping;
    }

    @SuppressWarnings("unchecked")
    private static Set<Direction>[] makeAxisCapFaceMapping() {
        Set<Direction>[] mapping = new Set[3];
        for (Direction.Axis axis : Direction.Axis.values()) {
            mapping[axis.ordinal()] = Set.of(axis.getDirections());
        }
        return mapping;
    }

    private static int[] build2dValueMapping() {
        int[] mapping = new int[24];
        for (Direction dir : DirUtils.DIRECTIONS) {
            mapping[make2dValueIndex(Direction.Axis.Y, dir)] = dir.get2DDataValue();
            mapping[make2dValueIndex(Direction.Axis.X, dir)] = switch (dir) {
                case SOUTH -> 0;
                case DOWN -> 1;
                case NORTH -> 2;
                case UP -> 3;
                default -> -1;
            };
            mapping[make2dValueIndex(Direction.Axis.Z, dir)] = switch (dir) {
                case EAST -> 0;
                case DOWN -> 1;
                case WEST -> 2;
                case UP -> 3;
                default -> -1;
            };
        }
        return mapping;
    }

    private static Rotation[] makeDirToDirRotationMapping() {
        Rotation[] mapping = new Rotation[16];
        for (Rotation rotation : Rotation.values()) {
            for (Direction dirOne : DirUtils.HORIZONTAL_DIRECTIONS) {
                Direction dirTwo = rotation.rotate(dirOne);
                mapping[makeDirToDirRotationIndex(dirOne, dirTwo)] = rotation;
            }
        }
        return mapping;
    }

    private Lookups() { }
}
