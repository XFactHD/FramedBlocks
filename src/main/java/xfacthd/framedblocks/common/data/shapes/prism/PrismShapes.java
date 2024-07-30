package xfacthd.framedblocks.common.data.shapes.prism;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.DirectionAxis;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;
import xfacthd.framedblocks.common.data.shapes.slope.SlopeShapes;

public final class PrismShapes implements SplitShapeGenerator
{
    public static final PrismShapes OUTER = new PrismShapes();

    private PrismShapes() { }

    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        return generateShapes(states, SlopeShapes.SHAPES);
    }

    @Override
    public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
    {
        return generateShapes(states, SlopeShapes.OCCLUSION_SHAPES);
    }

    private static ShapeProvider generateShapes(ImmutableList<BlockState> states, ShapeCache<SlopeType> shapeCache)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeBottom = ShapeUtils.andUnoptimized(
                shapeCache.get(SlopeType.BOTTOM),
                ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.SOUTH, shapeCache.get(SlopeType.BOTTOM))
        );
        VoxelShape shapeTop = ShapeUtils.andUnoptimized(
                shapeCache.get(SlopeType.TOP),
                ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.SOUTH, shapeCache.get(SlopeType.TOP))
        );
        VoxelShape shapeXZ = ShapeUtils.andUnoptimized(
                shapeCache.get(SlopeType.BOTTOM),
                shapeCache.get(SlopeType.TOP)
        );
        VoxelShape shapeY = ShapeUtils.andUnoptimized(
                shapeCache.get(SlopeType.HORIZONTAL),
                ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.EAST, shapeCache.get(SlopeType.HORIZONTAL))
        );

        VoxelShape[] shapes = new VoxelShape[DirectionAxis.COUNT];
        for (DirectionAxis dirAxis : DirectionAxis.values())
        {
            Direction facing = dirAxis.direction();
            Direction.Axis axis = dirAxis.axis();

            if (Utils.isY(facing))
            {
                shapes[dirAxis.ordinal()] = ShapeUtils.rotateShape(
                        Direction.EAST,
                        Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE),
                        facing == Direction.UP ? shapeBottom : shapeTop
                );
            }
            else
            {
                shapes[dirAxis.ordinal()] = ShapeUtils.rotateShape(
                        Direction.SOUTH,
                        facing,
                        axis == Direction.Axis.Y ? shapeY : shapeXZ
                );
            }
        }

        for (BlockState state : states)
        {
            DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
            builder.put(state, shapes[dirAxis.ordinal()]);
        }

        return ShapeProvider.of(builder.build());
    }
}
