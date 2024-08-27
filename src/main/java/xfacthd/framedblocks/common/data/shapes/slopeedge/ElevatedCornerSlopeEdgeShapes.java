package xfacthd.framedblocks.common.data.shapes.slopeedge;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;

public final class ElevatedCornerSlopeEdgeShapes implements SplitShapeGenerator
{
    private static final ShapeCache<CornerType> BASE_SHAPES = makeCache();
    private static final ShapeCache<CornerType> INNER_BASE_SHAPES = makeInnerCache();
    public static final ElevatedCornerSlopeEdgeShapes OUTER = new ElevatedCornerSlopeEdgeShapes(false);
    public static final ElevatedCornerSlopeEdgeShapes INNER = new ElevatedCornerSlopeEdgeShapes(true);

    private final ShapeCache<CornerType> baseShapes;
    private final ShapeCache<CornerSlopeEdgeShapes.ShapeKey> edgeShapes;
    private final ShapeCache<CornerSlopeEdgeShapes.ShapeKey> edgeOcclusionShapes;

    private ElevatedCornerSlopeEdgeShapes(boolean inner)
    {
        this.baseShapes = inner ? INNER_BASE_SHAPES : BASE_SHAPES;
        this.edgeShapes = inner ? CornerSlopeEdgeShapes.INNER_SHAPES : CornerSlopeEdgeShapes.OUTER_SHAPES;
        this.edgeOcclusionShapes = inner ? CornerSlopeEdgeShapes.INNER_OCCLUSION_SHAPES : CornerSlopeEdgeShapes.OUTER_OCCLUSION_SHAPES;
    }

    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        return generate(states, edgeShapes, baseShapes);
    }

    @Override
    public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
    {
        return generate(states, edgeOcclusionShapes, baseShapes);
    }

    private static ShapeProvider generate(ImmutableList<BlockState> states, ShapeCache<CornerSlopeEdgeShapes.ShapeKey> edgeShapes, ShapeCache<CornerType> baseShapes)
    {
        VoxelShape[] shapes = new VoxelShape[4 * 6];
        for (CornerType type : CornerType.values())
        {
            VoxelShape edgeShape = edgeShapes.get(new CornerSlopeEdgeShapes.ShapeKey(type, true));
            VoxelShape shape = ShapeUtils.orUnoptimized(baseShapes.get(type), edgeShape);
            ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, shapes, type, ElevatedCornerSlopeEdgeShapes::makeIndex);
        }

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
            builder.put(state, shapes[makeIndex(dir, type)]);
        }
        return ShapeProvider.of(builder.build());
    }

    private static int makeIndex(Direction dir, CornerType type)
    {
        return (type.ordinal() << 2) | dir.get2DDataValue();
    }

    private static ShapeCache<CornerType> makeCache()
    {
        return ShapeCache.createEnum(CornerType.class, map ->
        {
            map.put(CornerType.BOTTOM, ShapeUtils.orUnoptimized(
                    CommonShapes.SLAB.get(false),
                    CommonShapes.CORNER_PILLAR.get(Direction.NORTH)
            ));
            map.put(CornerType.TOP, ShapeUtils.orUnoptimized(
                    CommonShapes.SLAB.get(true),
                    CommonShapes.CORNER_PILLAR.get(Direction.NORTH)
            ));

            VoxelShape horShape = ShapeUtils.orUnoptimized(
                    CommonShapes.PANEL.get(Direction.NORTH),
                    CommonShapes.SLAB_EDGE.get(new CommonShapes.DirBoolKey(Direction.WEST, false))
            );
            map.put(CornerType.HORIZONTAL_BOTTOM_LEFT, horShape);
            map.put(CornerType.HORIZONTAL_TOP_LEFT, ShapeUtils.rotateShapeUnoptimizedAroundZ(
                    Direction.DOWN, Direction.WEST, horShape
            ));
            map.put(CornerType.HORIZONTAL_TOP_RIGHT, ShapeUtils.rotateShapeUnoptimizedAroundZ(
                    Direction.DOWN, Direction.UP, horShape
            ));
            map.put(CornerType.HORIZONTAL_BOTTOM_RIGHT, ShapeUtils.rotateShapeUnoptimizedAroundZ(
                    Direction.DOWN, Direction.EAST, horShape
            ));
        });
    }

    private static ShapeCache<CornerType> makeInnerCache()
    {
        return ShapeCache.createEnum(CornerType.class, map ->
        {
            VoxelShape bottomShape = ShapeUtils.orUnoptimized(
                    CommonShapes.SLAB.get(false),
                    CommonShapes.STRAIGHT_VERTICAL_STAIRS.get(Direction.NORTH)
            );
            map.put(CornerType.BOTTOM, bottomShape);

            VoxelShape topShape = ShapeUtils.orUnoptimized(
                    CommonShapes.SLAB.get(true),
                    CommonShapes.STRAIGHT_VERTICAL_STAIRS.get(Direction.NORTH)
            );
            map.put(CornerType.TOP, topShape);

            map.put(CornerType.HORIZONTAL_BOTTOM_LEFT, bottomShape);
            map.put(CornerType.HORIZONTAL_BOTTOM_RIGHT, ShapeUtils.rotateShapeUnoptimizedAroundY(
                    Direction.NORTH, Direction.EAST, bottomShape
            ));
            map.put(CornerType.HORIZONTAL_TOP_LEFT, topShape);
            map.put(CornerType.HORIZONTAL_TOP_RIGHT, ShapeUtils.rotateShapeUnoptimizedAroundY(
                    Direction.NORTH, Direction.EAST, topShape
            ));
        });
    }
}
