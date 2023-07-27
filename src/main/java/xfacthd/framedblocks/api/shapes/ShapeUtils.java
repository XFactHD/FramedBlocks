package xfacthd.framedblocks.api.shapes;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;

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

    public static VoxelShape[] makeHorizontalRotations(VoxelShape shape, Direction srcDir)
    {
        VoxelShape[] shapes = new VoxelShape[4];
        for (Direction dir : HORIZONTAL_DIRECTIONS)
        {
            shapes[dir.get2DDataValue()] = rotateShape(srcDir, dir, shape);
        }
        return shapes;
    }

    public static VoxelShape[] makeHorizontalRotationsWithFlag(VoxelShape shapeFalse, VoxelShape shapeTrue, Direction srcDir)
    {
        VoxelShape[] shapes = new VoxelShape[8];
        for (Direction dir : HORIZONTAL_DIRECTIONS)
        {
            shapes[dir.get2DDataValue()] = rotateShape(srcDir, dir, shapeFalse);
            shapes[dir.get2DDataValue() + 4] = rotateShape(srcDir, dir, shapeTrue);
        }
        return shapes;
    }



    private ShapeUtils() { }
}
