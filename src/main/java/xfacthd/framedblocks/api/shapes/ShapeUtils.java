package xfacthd.framedblocks.api.shapes;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;
import java.util.Map;

public final class ShapeUtils
{
    private static final Direction[] HORIZONTAL_DIRECTIONS = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);

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

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape)
    {
        return rotateShapeUnoptimized(from, to, shape).optimize();
    }

    public static VoxelShape rotateShapeUnoptimized(Direction from, Direction to, VoxelShape shape)
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
    }

    public static VoxelShape[] makeHorizontalRotations(VoxelShape shape, Direction srcDir)
    {
        VoxelShape[] shapes = new VoxelShape[4];
        makeHorizontalRotations(shape, srcDir, shapes, 0);
        return shapes;
    }

    public static void makeHorizontalRotations(VoxelShape shape, Direction srcDir, Map<Direction, VoxelShape> targetMap)
    {
        VoxelShape[] shapes = new VoxelShape[4];
        makeHorizontalRotations(shape, srcDir, shapes, 0);
        for (Direction dir : HORIZONTAL_DIRECTIONS)
        {
            targetMap.put(dir, shapes[dir.get2DDataValue()]);
        }
    }

    public static <V, T> void makeHorizontalRotations(
            VoxelShape shape, Direction srcDir, Map<T, VoxelShape> targetMap, V staticKeyParam, ArbKeyGenerator<V, T> keyGen
    )
    {
        VoxelShape[] shapes = new VoxelShape[4];
        makeHorizontalRotations(shape, srcDir, shapes, 0);
        for (Direction dir : HORIZONTAL_DIRECTIONS)
        {
            targetMap.put(keyGen.makeKey(dir, staticKeyParam), shapes[dir.get2DDataValue()]);
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
        VoxelShape[] shapes = new VoxelShape[8];
        makeHorizontalRotations(shapeFalse, srcDir, shapes, 0);
        makeHorizontalRotations(shapeTrue, srcDir, shapes, 4);
        for (Direction dir : HORIZONTAL_DIRECTIONS)
        {
            targetMap.put(keyGen.makeKey(dir, false), shapes[dir.get2DDataValue()]);
            targetMap.put(keyGen.makeKey(dir, true), shapes[dir.get2DDataValue() + 4]);
        }
    }

    public interface ArbKeyGenerator<V, T>
    {
        T makeKey(Direction dir, V staticParam);
    }

    public interface FlagKeyGenerator<T>
    {
        T makeKey(Direction dir, boolean flag);
    }



    private ShapeUtils() { }
}
