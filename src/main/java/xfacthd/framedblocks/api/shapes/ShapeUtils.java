package xfacthd.framedblocks.api.shapes;

import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;
import java.util.Map;

public final class ShapeUtils
{
    private static final Direction[] HORIZONTAL_DIRECTIONS = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
    private static final int[] DIR_ROT_X_2D_DATA = Util.make(new int[6], arr ->
    {
        arr[Direction.DOWN.ordinal()] = 2;
        arr[Direction.UP.ordinal()] = 0;
        arr[Direction.NORTH.ordinal()] = 3;
        arr[Direction.SOUTH.ordinal()] = 1;
        arr[Direction.WEST.ordinal()] = -1;
        arr[Direction.EAST.ordinal()] = -1;
    });
    private static final int[] DIR_ROT_Z_2D_DATA = Util.make(new int[6], arr ->
    {
        arr[Direction.DOWN.ordinal()] = 2;
        arr[Direction.UP.ordinal()] = 0;
        arr[Direction.NORTH.ordinal()] = -1;
        arr[Direction.SOUTH.ordinal()] = -1;
        arr[Direction.WEST.ordinal()] = 3;
        arr[Direction.EAST.ordinal()] = 1;
    });

    public static VoxelShape orUnoptimized(VoxelShape first, VoxelShape second)
    {
        return Shapes.joinUnoptimized(first, second, BooleanOp.OR);
    }

    public static VoxelShape orUnoptimized(VoxelShape first, VoxelShape... others)
    {
        for (VoxelShape shape : others)
        {
            first = ShapeUtils.orUnoptimized(first, shape);
        }
        return first;
    }

    public static VoxelShape or(VoxelShape first, VoxelShape second)
    {
        return orUnoptimized(first, second).optimize();
    }

    public static VoxelShape or(VoxelShape first, VoxelShape... others)
    {
        return orUnoptimized(first, others).optimize();
    }

    public static VoxelShape andUnoptimized(VoxelShape first, VoxelShape second)
    {
        return Shapes.joinUnoptimized(first, second, BooleanOp.AND);
    }

    public static VoxelShape andUnoptimized(VoxelShape first, VoxelShape... others)
    {
        for (VoxelShape shape : others)
        {
            first = ShapeUtils.andUnoptimized(first, shape);
        }
        return first;
    }

    public static VoxelShape and(VoxelShape first, VoxelShape second)
    {
        return andUnoptimized(first, second).optimize();
    }

    public static VoxelShape and(VoxelShape first, VoxelShape... others)
    {
        return andUnoptimized(first, others).optimize();
    }

    public static VoxelShape rotateShapeAroundY(Direction from, Direction to, VoxelShape shape)
    {
        return rotateShapeUnoptimizedAroundY(from, to, shape).optimize();
    }

    public static VoxelShape rotateShapeUnoptimizedAroundY(Direction from, Direction to, VoxelShape shape)
    {
        if (Utils.isY(from) || Utils.isY(to))
        {
            throw new IllegalArgumentException("Invalid Direction!");
        }
        if (from == to)
        {
            return shape;
        }

        List<AABB> sourceBoxes = shape.toAabbs();
        VoxelShape rotatedShape = Shapes.empty();
        int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
        for (AABB box : sourceBoxes)
        {
            for (int i = 0; i < times; i++)
            {
                box = new AABB(1 - box.maxZ, box.minY, box.minX, 1 - box.minZ, box.maxY, box.maxX);
            }
            rotatedShape = orUnoptimized(rotatedShape, Shapes.create(box));
        }

        return rotatedShape;
    }

    public static VoxelShape rotateShapeAroundX(Direction from, Direction to, VoxelShape shape)
    {
        return rotateShapeUnoptimizedAroundX(from, to, shape).optimize();
    }

    public static VoxelShape rotateShapeUnoptimizedAroundX(Direction from, Direction to, VoxelShape shape)
    {
        if (Utils.isX(from) || Utils.isX(to))
        {
            throw new IllegalArgumentException("Invalid Direction!");
        }
        if (from == to)
        {
            return shape;
        }

        List<AABB> sourceBoxes = shape.toAabbs();
        VoxelShape rotatedShape = Shapes.empty();
        int times = (DIR_ROT_X_2D_DATA[to.ordinal()] - DIR_ROT_X_2D_DATA[from.ordinal()] + 4) % 4;
        for (AABB box : sourceBoxes)
        {
            for (int i = 0; i < times; i++)
            {
                box = new AABB(box.minX, 1 - box.maxZ, box.minY, box.maxX, 1 - box.minZ, box.maxY);
            }
            rotatedShape = orUnoptimized(rotatedShape, Shapes.create(box));
        }

        return rotatedShape;
    }

    public static VoxelShape rotateShapeAroundZ(Direction from, Direction to, VoxelShape shape)
    {
        return rotateShapeUnoptimizedAroundZ(from, to, shape).optimize();
    }

    public static VoxelShape rotateShapeUnoptimizedAroundZ(Direction from, Direction to, VoxelShape shape)
    {
        if (Utils.isZ(from) || Utils.isZ(to))
        {
            throw new IllegalArgumentException("Invalid Direction!");
        }
        if (from == to)
        {
            return shape;
        }

        List<AABB> sourceBoxes = shape.toAabbs();
        VoxelShape rotatedShape = Shapes.empty();
        int times = (DIR_ROT_Z_2D_DATA[to.ordinal()] - DIR_ROT_Z_2D_DATA[from.ordinal()] + 4) % 4;
        for (AABB box : sourceBoxes)
        {
            for (int i = 0; i < times; i++)
            {
                //noinspection SuspiciousNameCombination
                box = new AABB(box.minY, 1 - box.maxX, box.minZ, box.maxY, 1 - box.minX, box.maxZ);
            }
            rotatedShape = orUnoptimized(rotatedShape, Shapes.create(box));
        }

        return rotatedShape;
    }

    public static void makeHorizontalRotations(VoxelShape shape, Direction srcDir, VoxelShape[] out, int baseOffset)
    {
        if (Utils.isY(srcDir))
        {
            throw new IllegalArgumentException("Invalid Direction!");
        }

        for (int i = 0; i < 4; i++)
        {
            boolean baseShape = i == srcDir.get2DDataValue();
            out[baseOffset + i] = baseShape ? shape : Shapes.empty();
        }

        List<AABB> sourceBoxes = shape.toAabbs();
        for (AABB box : sourceBoxes)
        {
            for (int i = 1; i < 4; i++)
            {
                int idx = baseOffset + ((srcDir.get2DDataValue() + i) % 4);
                box = new AABB(1 - box.maxZ, box.minY, box.minX, 1 - box.minZ, box.maxY, box.maxX);
                out[idx] = orUnoptimized(out[idx], Shapes.create(box));
            }
        }

        for (int i = 0; i < 4; i++)
        {
            out[baseOffset + i] = out[baseOffset + i].optimize();
        }
    }

    public static VoxelShape[] makeHorizontalRotations(VoxelShape shape, Direction srcDir)
    {
        VoxelShape[] shapes = new VoxelShape[4];
        makeHorizontalRotations(shape, srcDir, shapes, 0);
        return shapes;
    }

    public static void makeHorizontalRotations(VoxelShape shape, Direction srcDir, Map<Direction, VoxelShape> targetMap)
    {
        VoxelShape[] shapes = makeHorizontalRotations(shape, srcDir);
        for (Direction dir : HORIZONTAL_DIRECTIONS)
        {
            targetMap.put(dir, shapes[dir.get2DDataValue()]);
        }
    }

    public static <V, T> void makeHorizontalRotations(
            VoxelShape shape, Direction srcDir, Map<T, VoxelShape> targetMap, V staticKeyParam, ArbKeyGenerator<V, T> keyGen
    )
    {
        VoxelShape[] shapes = makeHorizontalRotations(shape, srcDir);
        for (Direction dir : HORIZONTAL_DIRECTIONS)
        {
            targetMap.put(keyGen.makeKey(dir, staticKeyParam), shapes[dir.get2DDataValue()]);
        }
    }

    public static <V> void makeHorizontalRotations(
            VoxelShape shape, Direction srcDir, VoxelShape[] shapes, V staticKeyParam, ArbIndexGenerator<V> keyGen
    )
    {
        VoxelShape[] preShapes = makeHorizontalRotations(shape, srcDir);
        for (Direction dir : HORIZONTAL_DIRECTIONS)
        {
            shapes[keyGen.makeKey(dir, staticKeyParam)] = preShapes[dir.get2DDataValue()];
        }
    }

    public static VoxelShape[] makeHorizontalRotationsWithFlag(VoxelShape shapeFalse, VoxelShape shapeTrue, Direction srcDir)
    {
        VoxelShape[] shapes = new VoxelShape[8];
        makeHorizontalRotations(shapeFalse, srcDir, shapes, 0);
        makeHorizontalRotations(shapeTrue, srcDir, shapes, 4);
        return shapes;
    }

    public static <T> void makeHorizontalRotationsWithFlag(
            VoxelShape shapeFalse,
            VoxelShape shapeTrue,
            Direction srcDir,
            Map<T, VoxelShape> targetMap,
            FlagKeyGenerator<T> keyGen
    )
    {
        VoxelShape[] shapes = makeHorizontalRotationsWithFlag(shapeFalse, shapeTrue, srcDir);
        for (Direction dir : HORIZONTAL_DIRECTIONS)
        {
            targetMap.put(keyGen.makeKey(dir, false), shapes[dir.get2DDataValue()]);
            targetMap.put(keyGen.makeKey(dir, true), shapes[dir.get2DDataValue() + 4]);
        }
    }

    public static void makeHorizontalRotationsWithFlag(
            VoxelShape shapeFalse, VoxelShape shapeTrue, Direction srcDir, VoxelShape[] shapes, FlagIndexGenerator keyGen
    )
    {
        VoxelShape[] preShapes = makeHorizontalRotationsWithFlag(shapeFalse, shapeTrue, srcDir);
        for (Direction dir : HORIZONTAL_DIRECTIONS)
        {
            shapes[keyGen.makeKey(dir, false)] = preShapes[dir.get2DDataValue()];
            shapes[keyGen.makeKey(dir, true)] = preShapes[dir.get2DDataValue() + 4];
        }
    }

    public interface ArbIndexGenerator<V>
    {
        int makeKey(Direction dir, V staticParam);
    }

    public interface ArbKeyGenerator<V, T>
    {
        T makeKey(Direction dir, V staticParam);
    }

    public interface FlagIndexGenerator
    {
        int makeKey(Direction dir, boolean flag);
    }

    public interface FlagKeyGenerator<T>
    {
        T makeKey(Direction dir, boolean flag);
    }



    private ShapeUtils() { }
}
