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

/// Provides various helpers for working with directions.
public final class DirUtils {
    static final Direction[] DIRECTIONS = Direction.values();
    static final Direction[] HORIZONTAL_DIRECTIONS = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);

    /// {@return whether the given direction is positive}
    ///
    /// @param dir The direction to check
    public static boolean isPositive(Direction dir) {
        return dir.getAxisDirection() == Direction.AxisDirection.POSITIVE;
    }

    /// {@return whether the given direction is on the X axis}
    ///
    /// @param dir The direction to check
    public static boolean isX(Direction dir) {
        return dir.getAxis() == Direction.Axis.X;
    }

    /// {@return whether the given direction is on the Y axis}
    ///
    /// @param dir The direction to check
    public static boolean isY(Direction dir) {
        return dir.getAxis() == Direction.Axis.Y;
    }

    /// {@return whether the given direction is on the Z axis}
    ///
    /// @param dir The direction to check
    public static boolean isZ(Direction dir) {
        return dir.getAxis() == Direction.Axis.Z;
    }

    /// {@return the direction represented by the given normal vector or `null` if the normal is not axis-aligned}
    ///
    /// @param x The X component, must be -1<x<1
    /// @param y The Y component, must be -1<x<1
    /// @param z The Z component, must be -1<x<1
    public static @Nullable Direction dirByNormal(int x, int y, int z) {
        return Lookups.NORMALS[Lookups.makeNormalIndex(x, y, z)];
    }

    /// {@return the direction between the two adjacent positions or `null` if the line between the positions is not axis-aligned}
    ///
    /// @param from The origin position
    /// @param to   The target position
    public static @Nullable Direction dirByNormal(BlockPos from, BlockPos to) {
        int nx = to.getX() - from.getX();
        int ny = to.getY() - from.getY();
        int nz = to.getZ() - from.getZ();
        return dirByNormal(nx, ny, nz);
    }

    /// Splits the face into four triangles as shown and computes the edge direction
    /// corresponding to the triangle targetted by the given hit position.
    ///
    /// Example of the UP face split into triangles for determining the target edge:
    /// ```
    /// +-------+
    /// | \ N / |
    /// |  \ /  |
    /// | W X E |
    /// |  / \  |
    /// | / S \ |
    /// +-------+
    /// ```
    ///
    /// @param face   The face the player is looking at
    /// @param hitVec The exact hit position on the face
    /// @return the edge corresponding to the triangle the player is looking at
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

    /// Splits the face into four quadrants as shown and computes the direction of
    /// the outer edge of the targetted quadrant which, if rotated CCW, points at
    /// the second outer edge of said quadrant.
    ///
    /// Example of the UP face split into quadrants for determining the target edge with
    /// the edge directions and the resulting directions for the respective quadrant:
    /// ```
    ///       N
    ///   +---+---+
    ///   | N | E |
    /// W +-------+ E
    ///   | W | S |
    ///   +---+---+
    ///       S
    /// ```
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

    /// {@return the axis perpendicular to both provided axis}
    /// The given axis must be perpendicular to each other.
    ///
    /// @param axisOne The first axis
    /// @param axisTwo The second axis
    public static Direction.Axis getPerpendicularAxis(Direction.Axis axisOne, Direction.Axis axisTwo) {
        Preconditions.checkArgument(axisOne != axisTwo, "Provided axis must be perpendicular");
        int idx = Lookups.makePerpAxisIndex(axisOne, axisTwo);
        return Objects.requireNonNull(Lookups.PERP_AXIS[idx]);
    }

    /// {@return the set of directions surrounding the given axis}
    ///
    /// @param axis The axis to get the surrounding faces for
    public static Set<Direction> getAxisTubeFaces(Direction.Axis axis) {
        return Lookups.AXIS_TUBE_FACES[axis.ordinal()];
    }

    /// {@return the set of directions along the given axis}
    ///
    /// @param axis The axis to get the cap faces for
    public static Set<Direction> getAxisCapFaces(Direction.Axis axis) {
        return Lookups.AXIS_CAP_FACES[axis.ordinal()];
    }

    /// {@return the 2D index of the given direction around the given axis}
    ///
    /// @param axis The normal axis of the 2D plane
    /// @param dir  The direction on the 2D plane
    public static int get2dValueAround(Direction.Axis axis, Direction dir) {
        Preconditions.checkArgument(axis != dir.getAxis(), "Direction must be perpendicular to axis");
        return Lookups.DIR_2D_VALUE_AROUND_AXIS[Lookups.make2dValueIndex(axis, dir)];
    }

    /// {@return whether the given rotation is ninety degree in either direction}
    ///
    /// @param rotation The rotation to check
    public static boolean isNinetyDegree(Rotation rotation) {
        return rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90;
    }

    /// {@return the rotation rotating in the opposite direction}
    ///
    /// @param rotation The rotation to invert
    public static Rotation getOppositeRotation(Rotation rotation) {
        return switch (rotation) {
            case NONE -> Rotation.NONE;
            case CLOCKWISE_90 -> Rotation.COUNTERCLOCKWISE_90;
            case CLOCKWISE_180 -> Rotation.CLOCKWISE_180;
            case COUNTERCLOCKWISE_90 -> Rotation.CLOCKWISE_90;
        };
    }

    /// {@return the rotation between the given directions}
    ///
    /// @param dirOne The source direction
    /// @param dirTwo The target direction
    public static Rotation getRotationBetween(Direction dirOne, Direction dirTwo) {
        return Lookups.ROTATIONS[Lookups.makeDirToDirRotationIndex(dirOne, dirTwo)];
    }

    /// Performs the given action for all six directions and `null`.
    ///
    /// @param consumer The action to perform
    public static void forAllDirectionsAndNull(Consumer<@Nullable Direction> consumer) {
        consumer.accept(null);
        //noinspection NullableProblems
        forAllDirections(consumer);
    }

    /// Performs the given action for all six directions.
    ///
    /// @param consumer The action to perform
    public static void forAllDirections(Consumer<Direction> consumer) {
        for (Direction dir : DIRECTIONS) {
            consumer.accept(dir);
        }
    }

    /// Performs the given action for all four horizontal directions.
    ///
    /// @param consumer The action to perform
    public static void forHorizontalDirections(Consumer<Direction> consumer) {
        for (Direction dir : HORIZONTAL_DIRECTIONS) {
            consumer.accept(dir);
        }
    }

    /// {@return the ordinal of the given direction or `6` if the direction is null}
    ///
    /// @param dir The direction to get the ordinal of
    public static int maskNullDirection(@Nullable Direction dir) {
        return dir == null ? DIRECTIONS.length : dir.ordinal();
    }

    /// {@return the axis along which the given mirror operates}
    ///
    /// @param mirror The mirror to get the axis for
    public static Direction.Axis getMirrorAxis(Mirror mirror) {
        return switch (mirror) {
            case LEFT_RIGHT -> Direction.Axis.Z;
            case FRONT_BACK -> Direction.Axis.X;
            case NONE -> throw new IllegalArgumentException("Cannot get mirror axis of Mirror.NONE");
        };
    }

    /// {@return whether the 16-step rotation values of the given states are in different quadrants}
    ///
    /// @param oldState The first state
    /// @param newState The second state
    public static boolean isDifferentRot16Quadrant(BlockState oldState, BlockState newState) {
        int oldRot = oldState.getValue(BlockStateProperties.ROTATION_16);
        int newRot = newState.getValue(BlockStateProperties.ROTATION_16);
        return  (oldRot / 4) != (newRot / 4);
    }

    private DirUtils() { }
}
