package xfacthd.framedblocks.common.data.shapes.stairs.vertical;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;
import xfacthd.framedblocks.common.data.shapes.slope.HalfSlopeShapes;

public final class VerticalSlopedStairsShapes implements SplitShapeGenerator
{
    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        return generateShapes(states, HalfSlopeShapes.SHAPES);
    }

    @Override
    public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
    {
        return generateShapes(states, HalfSlopeShapes.OCCLUSION_SHAPES);
    }

    private static ShapeProvider generateShapes(ImmutableList<BlockState> states, ShapeCache<HalfSlopeShapes.ShapeKey> shapeCache)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape panelShape = CommonShapes.PANEL.get(Direction.NORTH);

        VoxelShape shapeUp = ShapeUtils.orUnoptimized(
                panelShape,
                ShapeUtils.rotateShapeUnoptimizedAroundY(
                        Direction.NORTH,
                        Direction.EAST,
                        shapeCache.get(new HalfSlopeShapes.ShapeKey(false, true))
                )
        );

        VoxelShape shapeDown = ShapeUtils.orUnoptimized(
                panelShape,
                ShapeUtils.rotateShapeUnoptimizedAroundY(
                        Direction.NORTH,
                        Direction.WEST,
                        shapeCache.get(new HalfSlopeShapes.ShapeKey(true, false))
                )
        );

        VoxelShape shapeRight = ShapeUtils.orUnoptimized(
                panelShape,
                ShapeUtils.rotateShapeUnoptimizedAroundY(
                        Direction.NORTH,
                        Direction.WEST,
                        shapeCache.get(new HalfSlopeShapes.ShapeKey(false, false))
                )
        );

        VoxelShape shapeLeft = ShapeUtils.orUnoptimized(
                panelShape,
                ShapeUtils.rotateShapeUnoptimizedAroundY(
                        Direction.NORTH,
                        Direction.EAST,
                        shapeCache.get(new HalfSlopeShapes.ShapeKey(true, true))
                )
        );

        VoxelShape[] shapes = new VoxelShape[4 * 4];
        ShapeUtils.makeHorizontalRotations(shapeUp, Direction.NORTH, shapes, 0);
        ShapeUtils.makeHorizontalRotations(shapeDown, Direction.NORTH, shapes, HorizontalRotation.DOWN.ordinal() << 2);
        ShapeUtils.makeHorizontalRotations(shapeRight, Direction.NORTH, shapes, HorizontalRotation.RIGHT.ordinal() << 2);
        ShapeUtils.makeHorizontalRotations(shapeLeft, Direction.NORTH, shapes, HorizontalRotation.LEFT.ordinal() << 2);

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            int idx = dir.get2DDataValue() | (rot.ordinal() << 2);
            builder.put(state, shapes[idx]);
        }

        return ShapeProvider.of(builder.build());
    }
}
