package io.github.xfacthd.framedblocks.api.util;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public final class DirUtils {
    static final Direction[] DIRECTIONS = Direction.values();
    static final Direction[] HORIZONTAL_DIRECTIONS = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);

    public static boolean isPositive(Direction dir) {
        return dir.getAxisDirection() == Direction.AxisDirection.POSITIVE;
    }

    public static boolean isX(Direction dir) {
        return dir.getAxis() == Direction.Axis.X;
    }

    public static boolean isY(Direction dir) {
        return dir.getAxis() == Direction.Axis.Y;
    }

    public static boolean isZ(Direction dir) {
        return dir.getAxis() == Direction.Axis.Z;
    }

    public static @Nullable Direction dirByNormal(int x, int y, int z) {
        return Lookups.NORMALS[Lookups.makeNormalIndex(x, y, z)];
    }

    public static @Nullable Direction dirByNormal(BlockPos from, BlockPos to) {
        int nx = to.getX() - from.getX();
        int ny = to.getY() - from.getY();
        int nz = to.getZ() - from.getZ();
        return dirByNormal(nx, ny, nz);
    }

    public static Direction getDirByCross(Direction face, Vec3 hitVec) {
        hitVec = MathUtils.fraction(hitVec);

        if (DirUtils.isY(face)) {
            double x = hitVec.x() - .5;
            double z = hitVec.z() - .5;
            if (Math.max(Math.abs(x), Math.abs(z)) == Math.abs(x)) {
                return x > 0 ? Direction.EAST : Direction.WEST;
            } else {
                return z > 0 ? Direction.SOUTH : Direction.NORTH;
            }
        } else {
            double xz = (DirUtils.isX(face) ? hitVec.z() : hitVec.x()) - .5;
            double y = hitVec.y() - .5;

            if (Math.max(Math.abs(xz), Math.abs(y)) == Math.abs(xz)) {
                if (DirUtils.isX(face)) {
                    return xz < 0 ? Direction.NORTH : Direction.SOUTH;
                } else {
                    return (xz < 0) ? Direction.WEST : Direction.EAST;
                }
            } else {
                return y < 0 ? Direction.DOWN : Direction.UP;
            }
        }
    }

    public static Direction getDirByCorner(Direction face, Vec3 hitVec) {
        hitVec = MathUtils.fraction(hitVec);

        if (DirUtils.isY(face)) {
            double x = hitVec.x() - .5;
            double z = hitVec.z() - .5;
            if (z < 0) {
                return x > 0 ? Direction.EAST : Direction.NORTH;
            } else {
                return x > 0 ? Direction.SOUTH : Direction.WEST;
            }
        } else {
            double xz = MathUtils.fractionInDir(hitVec, face.getClockWise()) - .5;
            double y = hitVec.y() - .5;
            if (y > 0) {
                if (xz > 0) {
                    return face.getClockWise();
                } else {
                    return Direction.UP;
                }
            } else {
                if (xz > 0) {
                    return Direction.DOWN;
                } else {
                    return face.getCounterClockWise();
                }
            }
        }
    }

    /**
     * Returns the axis perpendicular to both provided axis which must themselves be perpendicular to each other
     */
    public static Direction.Axis getPerpendicularAxis(Direction.Axis axisOne, Direction.Axis axisTwo) {
        Preconditions.checkArgument(axisOne != axisTwo, "Provided axis must be perpendicular");
        int idx = Lookups.makePerpAxisIndex(axisOne, axisTwo);
        return Objects.requireNonNull(Lookups.PERP_AXIS[idx]);
    }

    public static Set<Direction> getAxisTubeFaces(Direction.Axis axis) {
        return Lookups.AXIS_TUBE_FACES[axis.ordinal()];
    }

    public static Set<Direction> getAxisCapFaces(Direction.Axis axis) {
        return Lookups.AXIS_CAP_FACES[axis.ordinal()];
    }

    public static int get2dValueAround(Direction.Axis axis, Direction dir) {
        Preconditions.checkArgument(axis != dir.getAxis(), "Direction must be perpendicular to axis");
        return Lookups.DIR_2D_VALUE_AROUND_AXIS[Lookups.make2dValueIndex(axis, dir)];
    }

    public static boolean isNinetyDegree(Rotation rotation) {
        return rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90;
    }

    public static Rotation getOppositeRotation(Rotation rotation) {
        return switch (rotation) {
            case NONE -> Rotation.NONE;
            case CLOCKWISE_90 -> Rotation.COUNTERCLOCKWISE_90;
            case CLOCKWISE_180 -> Rotation.CLOCKWISE_180;
            case COUNTERCLOCKWISE_90 -> Rotation.CLOCKWISE_90;
        };
    }

    public static Rotation getRotationBetween(Direction dirOne, Direction dirTwo) {
        return Lookups.ROTATIONS[Lookups.makeDirToDirRotationIndex(dirOne, dirTwo)];
    }

    public static void forAllDirectionsAndNull(Consumer<@Nullable Direction> consumer) {
        consumer.accept(null);
        //noinspection NullableProblems
        forAllDirections(consumer);
    }

    public static void forAllDirections(Consumer<Direction> consumer) {
        for (Direction dir : DIRECTIONS) {
            consumer.accept(dir);
        }
    }

    public static void forHorizontalDirections(Consumer<Direction> consumer) {
        for (Direction dir : HORIZONTAL_DIRECTIONS) {
            consumer.accept(dir);
        }
    }

    public static int maskNullDirection(@Nullable Direction dir) {
        return dir == null ? DIRECTIONS.length : dir.ordinal();
    }

    public static Direction.Axis getMirrorAxis(Mirror mirror) {
        return switch (mirror) {
            case LEFT_RIGHT -> Direction.Axis.Z;
            case FRONT_BACK -> Direction.Axis.X;
            case NONE -> throw new IllegalArgumentException("Cannot get mirror axis of Mirror.NONE");
        };
    }

    public static boolean isDifferentRot16Quadrant(BlockState oldState, BlockState newState) {
        int oldRot = oldState.getValue(BlockStateProperties.ROTATION_16);
        int newRot = newState.getValue(BlockStateProperties.ROTATION_16);
        return  (oldRot / 4) != (newRot / 4);
    }

    private DirUtils() { }
}
