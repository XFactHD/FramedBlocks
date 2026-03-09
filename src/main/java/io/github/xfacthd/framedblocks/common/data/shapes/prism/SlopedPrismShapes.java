package io.github.xfacthd.framedblocks.common.data.shapes.prism;

import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import io.github.xfacthd.framedblocks.common.data.shapes.slope.SlopeShapes;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class SlopedPrismShapes implements ShapeGenerator
{
    public static final SlopedPrismShapes OUTER = new SlopedPrismShapes();

    private SlopedPrismShapes() { }

    @Override
    public ShapeContainer generatePrimary(List<BlockState> states)
    {
        return generate(states, SlopeShapes.SHAPES);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states)
    {
        return generate(states, SlopeShapes.OCCLUSION_SHAPES);
    }

    private static ShapeContainer generate(List<BlockState> states, ShapeCache<SlopeType> slopeShapes)
    {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        VoxelShape slopeShapeBottom = slopeShapes.get(SlopeType.BOTTOM);
        VoxelShape shapeBottom = ShapeUtils.andUnoptimized(
                slopeShapeBottom,
                ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.EAST, slopeShapeBottom),
                ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.WEST, slopeShapeBottom)
        );
        VoxelShape shapeTop = ShapeUtils.rotateShapeUnoptimizedAroundZ(Direction.DOWN, Direction.UP, shapeBottom);

        VoxelShape shapeUp = ShapeUtils.rotateShapeUnoptimizedAroundX(Direction.UP, Direction.SOUTH, shapeTop);
        VoxelShape shapeDown = ShapeUtils.rotateShapeUnoptimizedAroundZ(Direction.UP, Direction.DOWN, shapeUp);
        VoxelShape shapeRight = ShapeUtils.rotateShapeUnoptimizedAroundZ(Direction.UP, Direction.WEST, shapeUp);
        VoxelShape shapeLeft = ShapeUtils.rotateShapeUnoptimizedAroundZ(Direction.UP, Direction.EAST, shapeUp);

        VoxelShape[] shapes = new VoxelShape[CompoundDirection.COUNT];
        for (CompoundDirection cmpDir : CompoundDirection.values())
        {
            Direction facing = cmpDir.direction();
            Direction orientation = cmpDir.orientation();

            if (DirUtils.isY(facing))
            {
                shapes[cmpDir.ordinal()] = ShapeUtils.rotateShapeAroundY(
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
                    shape = shapeLeft;
                }
                else if (orientation == facing.getCounterClockWise())
                {
                    shape = shapeRight;
                }
                else
                {
                    throw new IllegalArgumentException("Invalid orientation for direction!");
                }

                shapes[cmpDir.ordinal()] = ShapeUtils.rotateShapeAroundY(Direction.NORTH, facing, shape);
            }
        }

        for (BlockState state : states)
        {
            CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
            map.put(state, shapes[cmpDir.ordinal()]);
        }

        return ShapeContainer.of(map);
    }
}
