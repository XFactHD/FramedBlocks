package io.github.xfacthd.framedblocks.api.shapes;

import com.mojang.math.OctahedralGroup;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Arrays;
import java.util.Map;
import java.util.function.ToIntFunction;

/// Provides helpers for efficiently assembling voxel shapes.
public final class ShapeUtils {
    private static final Direction[] HORIZONTAL_DIRECTIONS = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
    private static final Direction[] ZY_PLANE_DIRECTIONS = Arrays.stream(Direction.values()).filter(dir -> !DirUtils.isX(dir)).toArray(Direction[]::new);
    private static final Direction[] XY_PLANE_DIRECTIONS = Arrays.stream(Direction.values()).filter(dir -> !DirUtils.isZ(dir)).toArray(Direction[]::new);
    private static final int[] DIR_ROT_X_2D_DATA = Util.make(new int[6], arr -> {
        arr[Direction.DOWN.ordinal()] = 2;
        arr[Direction.UP.ordinal()] = 0;
        arr[Direction.NORTH.ordinal()] = 3;
        arr[Direction.SOUTH.ordinal()] = 1;
        arr[Direction.WEST.ordinal()] = -1;
        arr[Direction.EAST.ordinal()] = -1;
    });
    private static final int[] DIR_ROT_Z_2D_DATA = Util.make(new int[6], arr -> {
        arr[Direction.DOWN.ordinal()] = 2;
        arr[Direction.UP.ordinal()] = 0;
        arr[Direction.NORTH.ordinal()] = -1;
        arr[Direction.SOUTH.ordinal()] = -1;
        arr[Direction.WEST.ordinal()] = 3;
        arr[Direction.EAST.ordinal()] = 1;
    });
    private static final OctahedralGroup[] DIR_ROT_Y_OCTAHEDRAL = collectOctahedralGroups(
            HORIZONTAL_DIRECTIONS,
            Direction::get2DDataValue,
            OctahedralGroup.ROT_90_Y_NEG, OctahedralGroup.ROT_180_FACE_XZ, OctahedralGroup.ROT_90_Y_POS
    );
    private static final OctahedralGroup[] DIR_ROT_X_OCTAHEDRAL = collectOctahedralGroups(
            ZY_PLANE_DIRECTIONS,
            dir -> DIR_ROT_X_2D_DATA[dir.ordinal()],
            OctahedralGroup.ROT_90_X_POS, OctahedralGroup.ROT_180_FACE_YZ, OctahedralGroup.ROT_90_X_NEG
    );
    private static final OctahedralGroup[] DIR_ROT_Z_OCTAHEDRAL = collectOctahedralGroups(
            XY_PLANE_DIRECTIONS,
            dir -> DIR_ROT_Z_2D_DATA[dir.ordinal()],
            OctahedralGroup.ROT_90_Z_NEG, OctahedralGroup.ROT_180_FACE_XY, OctahedralGroup.ROT_90_Z_POS
    );
    private static final VoxelShape BEACON_BEAM_SHAPE = Block.box(5, 0, 5, 11, 16, 11);

    /// Computes the sum of the given shapes without optimizing the resulting shape.
    ///
    /// @param first  The first shape to include in the sum
    /// @param second The second shape to include in the sum
    /// @return the unoptimized sum of the given shapes
    public static VoxelShape orUnoptimized(VoxelShape first, VoxelShape second) {
        return Shapes.joinUnoptimized(first, second, BooleanOp.OR);
    }

    /// Computes the sum of the given shapes without optimizing the resulting shape.
    ///
    /// @param first  The first shape to include in the sum
    /// @param others The other shapes to include in the sum
    /// @return the unoptimized sum of the given shapes
    public static VoxelShape orUnoptimized(VoxelShape first, VoxelShape... others) {
        for (VoxelShape shape : others) {
            first = ShapeUtils.orUnoptimized(first, shape);
        }
        return first;
    }

    /// Computes the sum of the given shapes and optimizes the resulting shape.
    ///
    /// @param first  The first shape to include in the sum
    /// @param second The second shape to include in the sum
    /// @return the optimized sum of the given shapes
    public static VoxelShape or(VoxelShape first, VoxelShape second) {
        return optimize(orUnoptimized(first, second));
    }

    /// Computes the sum of the given shapes and optimizes the resulting shape.
    ///
    /// @param first  The first shape to include in the sum
    /// @param others The other shapes to include in the sum
    /// @return the optimized sum of the given shapes
    public static VoxelShape or(VoxelShape first, VoxelShape... others) {
        return optimize(orUnoptimized(first, others));
    }

    /// Computes the intersection of the given shapes without optimizing the resulting shape.
    ///
    /// @param first  The first shape to include in the intersection
    /// @param second The second shape to include in the intersection
    /// @return the unoptimized intersection of the given shapes
    public static VoxelShape andUnoptimized(VoxelShape first, VoxelShape second) {
        return Shapes.joinUnoptimized(first, second, BooleanOp.AND);
    }

    /// Computes the intersection of the given shapes without optimizing the resulting shape.
    ///
    /// @param first  The first shape to include in the intersection
    /// @param others The other shapes to include in the intersection
    /// @return the unoptimized intersection of the given shapes
    public static VoxelShape andUnoptimized(VoxelShape first, VoxelShape... others) {
        for (VoxelShape shape : others) {
            first = ShapeUtils.andUnoptimized(first, shape);
        }
        return first;
    }

    /// Computes the intersection of the given shapes and optimizes the resulting shape.
    ///
    /// @param first  The first shape to include in the intersection
    /// @param second The second shape to include in the intersection
    /// @return the optimized intersection of the given shapes
    public static VoxelShape and(VoxelShape first, VoxelShape second) {
        return optimize(andUnoptimized(first, second));
    }

    /// Computes the intersection of the given shapes and optimizes the resulting shape.
    ///
    /// @param first  The first shape to include in the intersection
    /// @param others The other shapes to include in the intersection
    /// @return the optimized intersection of the given shapes
    public static VoxelShape and(VoxelShape first, VoxelShape... others) {
        return optimize(andUnoptimized(first, others));
    }

    /// {@return an optimized version of the given shape}
    ///
    /// @param shape The shape to optimize
    public static VoxelShape optimize(VoxelShape shape) {
        // CubeVoxelShapes are already almost guaranteed to be optimal
        return shape instanceof ArrayVoxelShape ? shape.optimize() : shape;
    }

    /// Rotates the given shape from the first direction to the second direction around
    /// the Y axis and optimizes the resulting shape.
    ///
    /// @param from  The original orientation of the shape
    /// @param to    The target orientation
    /// @param shape The shape to rotate
    /// @return the rotated shape
    public static VoxelShape rotateShapeAroundY(Direction from, Direction to, VoxelShape shape) {
        return optimize(rotateShapeUnoptimizedAroundY(from, to, shape));
    }

    /// Rotates the given shape from the first direction to the second direction around
    /// the Y axis without optimizing the resulting shape.
    ///
    /// @param from  The original orientation of the shape
    /// @param to    The target orientation
    /// @param shape The shape to rotate
    /// @return the rotated shape
    public static VoxelShape rotateShapeUnoptimizedAroundY(Direction from, Direction to, VoxelShape shape) {
        if (DirUtils.isY(from) || DirUtils.isY(to)) {
            throw new IllegalArgumentException("Invalid Direction!");
        }
        if (from == to) {
            return shape;
        }
        return Shapes.rotate(shape, DIR_ROT_Y_OCTAHEDRAL[from.get2DDataValue() << 2 | to.get2DDataValue()]);
    }

    /// Rotates the given shape from the first direction to the second direction around
    /// the X axis and optimizes the resulting shape.
    ///
    /// @param from  The original orientation of the shape
    /// @param to    The target orientation
    /// @param shape The shape to rotate
    /// @return the rotated shape
    public static VoxelShape rotateShapeAroundX(Direction from, Direction to, VoxelShape shape) {
        return optimize(rotateShapeUnoptimizedAroundX(from, to, shape));
    }

    /// Rotates the given shape from the first direction to the second direction around
    /// the X axis without optimizing the resulting shape.
    ///
    /// @param from  The original orientation of the shape
    /// @param to    The target orientation
    /// @param shape The shape to rotate
    /// @return the rotated shape
    public static VoxelShape rotateShapeUnoptimizedAroundX(Direction from, Direction to, VoxelShape shape) {
        if (DirUtils.isX(from) || DirUtils.isX(to)) {
            throw new IllegalArgumentException("Invalid Direction!");
        }
        if (from == to) {
            return shape;
        }
        return Shapes.rotate(shape, DIR_ROT_X_OCTAHEDRAL[DIR_ROT_X_2D_DATA[from.ordinal()] << 2 | DIR_ROT_X_2D_DATA[to.ordinal()]]);
    }

    /// Rotates the given shape from the first direction to the second direction around
    /// the Z axis and optimizes the resulting shape.
    ///
    /// @param from  The original orientation of the shape
    /// @param to    The target orientation
    /// @param shape The shape to rotate
    /// @return the rotated shape
    public static VoxelShape rotateShapeAroundZ(Direction from, Direction to, VoxelShape shape) {
        return optimize(rotateShapeUnoptimizedAroundZ(from, to, shape));
    }

    /// Rotates the given shape from the first direction to the second direction around
    /// the Z axis without optimizing the resulting shape.
    ///
    /// @param from  The original orientation of the shape
    /// @param to    The target orientation
    /// @param shape The shape to rotate
    /// @return the rotated shape
    public static VoxelShape rotateShapeUnoptimizedAroundZ(Direction from, Direction to, VoxelShape shape) {
        if (DirUtils.isZ(from) || DirUtils.isZ(to)) {
            throw new IllegalArgumentException("Invalid Direction!");
        }
        if (from == to) {
            return shape;
        }
        return Shapes.rotate(shape, DIR_ROT_Z_OCTAHEDRAL[DIR_ROT_Z_2D_DATA[from.ordinal()] << 2 | DIR_ROT_Z_2D_DATA[to.ordinal()]]);
    }

    /// Mirrors the given shape along the Y axis and optimizes the resulting shape.
    ///
    /// @param shape The shape to mirror
    /// @return the optimized mirrored shape
    public static VoxelShape mirrorShapeAlongY(VoxelShape shape) {
        return optimize(mirrorShapeUnoptimizedAlongY(shape));
    }

    /// Mirrors the given shape along the Y axis without optimizing the resulting shape.
    ///
    /// @param shape The shape to mirror
    /// @return the unoptimized mirrored shape
    public static VoxelShape mirrorShapeUnoptimizedAlongY(VoxelShape shape) {
        VoxelShape mirroredShape = Shapes.empty();
        for (AABB box : shape.toAabbs()) {
            VoxelShape boxShape = Shapes.create(box.minX, 1D - box.maxY, box.minZ, box.maxX, 1D - box.minY, box.maxZ);
            mirroredShape = orUnoptimized(mirroredShape, boxShape);
        }
        return mirroredShape;
    }

    /// Computes the four horizontal rotations of the given shape and inserts them into the given array at the
    /// given offset, indexed by [Direction#get2DDataValue()].
    ///
    /// @param shape      The original shape
    /// @param srcDir     The original orientation of the shape
    /// @param out        The target array to add the shapes to
    /// @param baseOffset The offset into the output array
    public static void makeHorizontalRotations(VoxelShape shape, Direction srcDir, VoxelShape[] out, int baseOffset) {
        if (DirUtils.isY(srcDir)) {
            throw new IllegalArgumentException("Invalid Direction!");
        }

        for (int i = 0; i < 4; i++) {
            boolean baseShape = i == srcDir.get2DDataValue();
            out[baseOffset + i] = optimize(baseShape ? shape : Shapes.rotate(shape, DIR_ROT_Y_OCTAHEDRAL[srcDir.get2DDataValue() << 2 | i]));
        }
    }

    /// Computes the four horizontal rotations of the given shape and returns them as an array, indexed by [Direction#get2DDataValue()].
    ///
    /// @param shape  The original shape
    /// @param srcDir The original orientation of the shape
    /// @return the array of shapes
    public static VoxelShape[] makeHorizontalRotations(VoxelShape shape, Direction srcDir) {
        VoxelShape[] shapes = new VoxelShape[4];
        makeHorizontalRotations(shape, srcDir, shapes, 0);
        return shapes;
    }

    /// Computes the four horizontal rotations of the given shape and inserts them into the given map.
    ///
    /// @param shape     The original shape
    /// @param srcDir    The original orientation of the shape
    /// @param targetMap The target map to add the shapes to
    public static void makeHorizontalRotations(VoxelShape shape, Direction srcDir, Map<Direction, VoxelShape> targetMap) {
        VoxelShape[] shapes = makeHorizontalRotations(shape, srcDir);
        for (Direction dir : HORIZONTAL_DIRECTIONS) {
            targetMap.put(dir, shapes[dir.get2DDataValue()]);
        }
    }

    /// Computes the four horizontal rotations of the given shape and inserts them into the given map.
    ///
    /// @param shape          The original shape
    /// @param srcDir         The original orientation of the shape
    /// @param targetMap      The target map to add the shapes to
    /// @param staticKeyParam The secondary parameter of the map key
    /// @param keyGen         A function computing the key from the direction and the secondary key parameter
    public static <V, T> void makeHorizontalRotations(VoxelShape shape, Direction srcDir, Map<T, VoxelShape> targetMap, V staticKeyParam, ArbKeyGenerator<V, T> keyGen) {
        VoxelShape[] shapes = makeHorizontalRotations(shape, srcDir);
        for (Direction dir : HORIZONTAL_DIRECTIONS) {
            targetMap.put(keyGen.makeKey(dir, staticKeyParam), shapes[dir.get2DDataValue()]);
        }
    }

    /// Computes the four horizontal rotations of the given shape and inserts them into the given array,
    /// indexed by the indices computed by the given index generator.
    ///
    /// @param shape          The original shape
    /// @param srcDir         The original orientation of the shape
    /// @param shapes         The target array to add the shapes to
    /// @param staticKeyParam The secondary parameter of the array index computation
    /// @param keyGen         A function computing the index from the direction and the secondary key parameter
    public static <V> void makeHorizontalRotations(VoxelShape shape, Direction srcDir, VoxelShape[] shapes, V staticKeyParam, ArbIndexGenerator<V> keyGen) {
        VoxelShape[] preShapes = makeHorizontalRotations(shape, srcDir);
        for (Direction dir : HORIZONTAL_DIRECTIONS) {
            shapes[keyGen.makeKey(dir, staticKeyParam)] = preShapes[dir.get2DDataValue()];
        }
    }

    /// Computes the four horizontal rotations of the given shapes and returns them as an array, indexed by [Direction#get2DDataValue()]
    /// with the "true" shapes offset by four indices.
    ///
    /// @param shapeFalse The original shape for flag=flase
    /// @param shapeTrue  The original shape for flag=true
    /// @param srcDir     The original orientation of the shapes
    /// @return the array of shapes
    public static VoxelShape[] makeHorizontalRotationsWithFlag(VoxelShape shapeFalse, VoxelShape shapeTrue, Direction srcDir) {
        VoxelShape[] shapes = new VoxelShape[8];
        makeHorizontalRotations(shapeFalse, srcDir, shapes, 0);
        makeHorizontalRotations(shapeTrue, srcDir, shapes, 4);
        return shapes;
    }

    /// Computes the four horizontal rotations of the given shapes and inserts them into the given map.
    ///
    /// @param shapeFalse The original shape for flag=flase
    /// @param shapeTrue  The original shape for flag=true
    /// @param srcDir     The original orientation of the shapes
    /// @param targetMap  The map to add the shapes to
    /// @param keyGen     A function computing the map key from the direction and flag
    public static <T> void makeHorizontalRotationsWithFlag(VoxelShape shapeFalse, VoxelShape shapeTrue, Direction srcDir, Map<T, VoxelShape> targetMap, FlagKeyGenerator<T> keyGen) {
        VoxelShape[] shapes = makeHorizontalRotationsWithFlag(shapeFalse, shapeTrue, srcDir);
        for (Direction dir : HORIZONTAL_DIRECTIONS) {
            targetMap.put(keyGen.makeKey(dir, false), shapes[dir.get2DDataValue()]);
            targetMap.put(keyGen.makeKey(dir, true), shapes[dir.get2DDataValue() + 4]);
        }
    }

    /// Computes the four horizontal rotations of the given shapes and inserts them into the given array,
    /// indexed by the indices computed by the given index generator.
    ///
    /// @param shapeFalse The original shape for flag=flase
    /// @param shapeTrue  The original shape for flag=true
    /// @param srcDir     The original orientation of the shapes
    /// @param shapes     The array to add the shapes to
    /// @param keyGen     A function computing the array index from the direction and flag
    public static void makeHorizontalRotationsWithFlag(VoxelShape shapeFalse, VoxelShape shapeTrue, Direction srcDir, VoxelShape[] shapes, FlagIndexGenerator keyGen) {
        VoxelShape[] preShapes = makeHorizontalRotationsWithFlag(shapeFalse, shapeTrue, srcDir);
        for (Direction dir : HORIZONTAL_DIRECTIONS) {
            shapes[keyGen.makeKey(dir, false)] = preShapes[dir.get2DDataValue()];
            shapes[keyGen.makeKey(dir, true)] = preShapes[dir.get2DDataValue() + 4];
        }
    }

    /// Computes the four horizontal rotations of the given shapes and inserts them into the given array,
    /// indexed by the indices computed by the given index generator.
    ///
    /// @param shapeFalse The original shape for flag=flase
    /// @param shapeTrue  The original shape for flag=true
    /// @param srcDir     The original orientation of the shapes
    /// @param auxFlag    The secondary flag of the array index computation
    /// @param shapes     The array to add the shapes to
    /// @param keyGen     A function computing the array index from the direction and flag
    public static void makeHorizontalRotationsWithFlag(VoxelShape shapeFalse, VoxelShape shapeTrue, Direction srcDir, boolean auxFlag, VoxelShape[] shapes, MultiFlagIndexGenerator keyGen) {
        VoxelShape[] preShapes = makeHorizontalRotationsWithFlag(shapeFalse, shapeTrue, srcDir);
        for (Direction dir : HORIZONTAL_DIRECTIONS) {
            shapes[keyGen.makeKey(dir, false, auxFlag)] = preShapes[dir.get2DDataValue()];
            shapes[keyGen.makeKey(dir, true, auxFlag)] = preShapes[dir.get2DDataValue() + 4];
        }
    }

    /// {@return whether the given shape occludes a beacon beam}
    ///
    /// @param shape The shape to test
    public static boolean occludesBeaconBeam(VoxelShape shape) {
        VoxelShape intersection = andUnoptimized(shape, BEACON_BEAM_SHAPE);
        return intersection.min(Direction.Axis.X) <= BEACON_BEAM_SHAPE.min(Direction.Axis.X) &&
               intersection.min(Direction.Axis.Z) <= BEACON_BEAM_SHAPE.min(Direction.Axis.Z) &&
               intersection.max(Direction.Axis.X) >= BEACON_BEAM_SHAPE.max(Direction.Axis.X) &&
               intersection.max(Direction.Axis.Z) >= BEACON_BEAM_SHAPE.max(Direction.Axis.Z);
    }

    /// Functional interface for computing an array index from a direction and a secondary parameter.
    @FunctionalInterface
    public interface ArbIndexGenerator<V> {
        /// {@return the array index computed from the given direction and secondary parameter}
        ///
        /// @param dir         The target direction
        /// @param staticParam The secondary parameter
        int makeKey(Direction dir, V staticParam);
    }

    /// Functional interface for computing a map key from a direction and a secondary parameter.
    @FunctionalInterface
    public interface ArbKeyGenerator<V, T> {
        /// {@return the map key computed from the given direction and secondary parameter}
        ///
        /// @param dir         The target direction
        /// @param staticParam The secondary parameter
        T makeKey(Direction dir, V staticParam);
    }

    /// Functional interface for computing an array index from a direction and a secondary flag.
    @FunctionalInterface
    public interface FlagIndexGenerator {
        /// {@return the array index computed from the given direction and secondary flag}
        ///
        /// @param dir  The target direction
        /// @param flag The secondary flag
        int makeKey(Direction dir, boolean flag);
    }

    /// Functional interface for computing a map key from a direction and a secondary flag.
    @FunctionalInterface
    public interface FlagKeyGenerator<T> {
        /// {@return the map key computed from the given direction and secondary flag}
        ///
        /// @param dir  The target direction
        /// @param flag The secondary flag
        T makeKey(Direction dir, boolean flag);
    }

    /// Functional interface for computing an array index from a direction, a primary flag ana a secondary flag.
    @FunctionalInterface
    public interface MultiFlagIndexGenerator {
        /// {@return the array index computed from the given direction, a primary flag and a secondary flag}
        ///
        /// @param dir     The target direction
        /// @param flag    The primary flag
        /// @param auxFlag The secondary flag
        int makeKey(Direction dir, boolean flag, boolean auxFlag);
    }

    private static OctahedralGroup[] collectOctahedralGroups(Direction[] directions, ToIntFunction<Direction> idxGetter, OctahedralGroup... rotGroups) {
        OctahedralGroup[] arr = new OctahedralGroup[16];
        for (Direction dirIn : directions) {
            for (Direction dirOut : directions) {
                int idx = idxGetter.applyAsInt(dirIn) << 2 | idxGetter.applyAsInt(dirOut);
                int times = (idxGetter.applyAsInt(dirOut) - idxGetter.applyAsInt(dirIn) + 4) % 4;
                arr[idx] = times == 0 ? OctahedralGroup.IDENTITY : rotGroups[times - 1];
            }
        }
        return arr;
    }

    private ShapeUtils() { }
}
