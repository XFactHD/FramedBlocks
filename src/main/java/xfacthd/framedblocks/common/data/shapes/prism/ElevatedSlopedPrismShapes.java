package xfacthd.framedblocks.common.data.shapes.prism;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CompoundDirection;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;
import xfacthd.framedblocks.common.data.shapes.slope.SlopeShapes;

public final class ElevatedSlopedPrismShapes implements SplitShapeGenerator
{
    public static final ElevatedSlopedPrismShapes INNER = new ElevatedSlopedPrismShapes();

    private ElevatedSlopedPrismShapes() { }

    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        return generate(states, SlopeShapes.SHAPES);
    }

    @Override
    public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
    {
        return generate(states, SlopeShapes.OCCLUSION_SHAPES);
    }

    private static ShapeProvider generate(ImmutableList<BlockState> states, ShapeCache<SlopeType> slopeShapes)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape slopeShapeBottom = slopeShapes.get(SlopeType.BOTTOM);
        VoxelShape shapeBottom = ShapeUtils.orUnoptimized(
                ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.SOUTH, slopeShapeBottom),
                ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.EAST, slopeShapeBottom),
                ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, slopeShapeBottom)
        );
        VoxelShape shapeTop = ShapeUtils.rotateShapeUnoptimizedAroundZ(Direction.DOWN, Direction.UP, shapeBottom);

        VoxelShape shapeUp = ShapeUtils.rotateShapeUnoptimizedAroundX(Direction.DOWN, Direction.NORTH, shapeTop);
        VoxelShape shapeDown = ShapeUtils.rotateShapeUnoptimizedAroundZ(Direction.UP, Direction.DOWN, shapeUp);
        VoxelShape shapeRight = ShapeUtils.rotateShapeUnoptimizedAroundZ(Direction.UP, Direction.EAST, shapeUp);
        VoxelShape shapeLeft = ShapeUtils.rotateShapeUnoptimizedAroundZ(Direction.UP, Direction.WEST, shapeUp);

        VoxelShape[] shapes = new VoxelShape[CompoundDirection.COUNT];
        for (CompoundDirection cmpDir : CompoundDirection.values())
        {
            Direction facing = cmpDir.direction();
            Direction orientation = cmpDir.orientation();

            if (Utils.isY(facing))
            {
                shapes[cmpDir.ordinal()] = ShapeUtils.rotateShape(
                        Direction.NORTH,
                        orientation,
                        facing == Direction.UP ? shapeBottom : shapeTop
                );
            }
            else
            {
                VoxelShape shape;
                if (orientation == Direction.UP)
                {
                    shape = shapeUp;
                }
                else if (orientation == Direction.DOWN)
                {
                    shape = shapeDown;
                }
                else if (orientation == facing.getClockWise())
                {
                    shape = shapeRight;
                }
                else if (orientation == facing.getCounterClockWise())
                {
                    shape = shapeLeft;
                }
                else
                {
                    throw new IllegalArgumentException("Invalid orientation for direction!");
                }

                shapes[cmpDir.ordinal()] = ShapeUtils.rotateShape(Direction.NORTH, facing, shape);
            }
        }

        for (BlockState state : states)
        {
            CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
            builder.put(state, shapes[cmpDir.ordinal()]);
        }

        return ShapeProvider.of(builder.build());
    }
}
