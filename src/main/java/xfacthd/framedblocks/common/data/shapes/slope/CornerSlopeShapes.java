package xfacthd.framedblocks.common.data.shapes.slope;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;

import java.util.HashMap;
import java.util.Map;

public final class CornerSlopeShapes implements SplitShapeGenerator
{
    public static final CornerSlopeShapes OUTER = new CornerSlopeShapes(BooleanOp.AND);
    public static final CornerSlopeShapes INNER = new CornerSlopeShapes(BooleanOp.OR);

    private final BooleanOp joinOp;

    private CornerSlopeShapes(BooleanOp joinOp)
    {
        this.joinOp = joinOp;
    }

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

    private ShapeProvider generate(ImmutableList<BlockState> states, ShapeCache<SlopeType> shapeCache)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeSlopeBottom = shapeCache.get(SlopeType.BOTTOM);
        VoxelShape shapeSlopeTop = shapeCache.get(SlopeType.TOP);
        VoxelShape shapeSlopeHorizontal = shapeCache.get(SlopeType.HORIZONTAL);
        VoxelShape shapeSlopeHorizontalEast = ShapeUtils.rotateShapeUnoptimized(
                Direction.NORTH, Direction.EAST, shapeSlopeHorizontal
        );

        VoxelShape shapeTop = Shapes.joinUnoptimized(
                shapeSlopeTop,
                ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, shapeSlopeTop),
                joinOp
        );
        VoxelShape shapeBottom = Shapes.joinUnoptimized(
                shapeSlopeBottom,
                ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, shapeSlopeBottom),
                joinOp
        );

        VoxelShape shapeBottomLeft = Shapes.joinUnoptimized(shapeSlopeBottom, shapeSlopeHorizontal, joinOp);
        VoxelShape shapeBottomRight = Shapes.joinUnoptimized(shapeSlopeBottom, shapeSlopeHorizontalEast, joinOp);
        VoxelShape shapeTopLeft = Shapes.joinUnoptimized(shapeSlopeTop, shapeSlopeHorizontal, joinOp);
        VoxelShape shapeTopRight = Shapes.joinUnoptimized(shapeSlopeTop, shapeSlopeHorizontalEast, joinOp);

        Map<ShapeKey, VoxelShape> shapes = new HashMap<>();
        for (CornerType type : CornerType.values())
        {
            VoxelShape shape = switch (type)
            {
                case BOTTOM -> shapeBottom;
                case TOP -> shapeTop;
                case HORIZONTAL_BOTTOM_LEFT -> shapeBottomLeft;
                case HORIZONTAL_BOTTOM_RIGHT -> shapeBottomRight;
                case HORIZONTAL_TOP_LEFT -> shapeTopLeft;
                case HORIZONTAL_TOP_RIGHT -> shapeTopRight;
            };
            ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, shapes, type, ShapeKey::new);
        }

        for (BlockState state : states)
        {
            CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            builder.put(state, shapes.get(new ShapeKey(dir, type)));
        }

        return ShapeProvider.of(builder.build());
    }



    private record ShapeKey(Direction dir, CornerType type) { }
}
