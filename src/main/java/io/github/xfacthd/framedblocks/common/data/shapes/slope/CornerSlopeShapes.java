package io.github.xfacthd.framedblocks.common.data.shapes.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CornerType;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class CornerSlopeShapes implements ShapeGenerator
{
    public static final CornerSlopeShapes OUTER = new CornerSlopeShapes(BooleanOp.AND);
    public static final CornerSlopeShapes INNER = new CornerSlopeShapes(BooleanOp.OR);

    private final BooleanOp joinOp;

    private CornerSlopeShapes(BooleanOp joinOp)
    {
        this.joinOp = joinOp;
    }

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

    private ShapeContainer generate(List<BlockState> states, ShapeCache<SlopeType> shapeCache)
    {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        VoxelShape shapeSlopeBottom = shapeCache.get(SlopeType.BOTTOM);
        VoxelShape shapeSlopeTop = shapeCache.get(SlopeType.TOP);
        VoxelShape shapeSlopeHorizontal = shapeCache.get(SlopeType.HORIZONTAL);
        VoxelShape shapeSlopeHorizontalEast = ShapeUtils.rotateShapeUnoptimizedAroundY(
                Direction.NORTH, Direction.EAST, shapeSlopeHorizontal
        );

        VoxelShape shapeTop = Shapes.joinUnoptimized(
                shapeSlopeTop,
                ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.WEST, shapeSlopeTop),
                joinOp
        );
        VoxelShape shapeBottom = Shapes.joinUnoptimized(
                shapeSlopeBottom,
                ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.WEST, shapeSlopeBottom),
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
            map.put(state, shapes.get(new ShapeKey(dir, type)));
        }

        return ShapeContainer.of(map);
    }

    private record ShapeKey(Direction dir, CornerType type) { }
}
