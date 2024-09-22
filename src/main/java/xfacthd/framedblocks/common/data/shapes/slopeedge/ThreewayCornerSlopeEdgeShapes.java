package xfacthd.framedblocks.common.data.shapes.slopeedge;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;

public final class ThreewayCornerSlopeEdgeShapes implements SplitShapeGenerator
{
    public static final ShapeCache<ShapeKey> OUTER_SHAPES = makeCache(SlopeEdgeShapes.SHAPES, false);
    public static final ShapeCache<ShapeKey> OUTER_OCCLUSION_SHAPES = makeCache(SlopeEdgeShapes.OCCLUSION_SHAPES, false);
    public static final ShapeCache<ShapeKey> INNER_SHAPES = makeCache(SlopeEdgeShapes.SHAPES, true);
    public static final ShapeCache<ShapeKey> INNER_OCCLUSION_SHAPES = makeCache(SlopeEdgeShapes.OCCLUSION_SHAPES, true);

    public static final ThreewayCornerSlopeEdgeShapes OUTER = new ThreewayCornerSlopeEdgeShapes(false);
    public static final ThreewayCornerSlopeEdgeShapes INNER = new ThreewayCornerSlopeEdgeShapes(true);

    private final boolean inner;

    private ThreewayCornerSlopeEdgeShapes(boolean inner)
    {
        this.inner = inner;
    }

    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        return generate(states, inner ? INNER_SHAPES : OUTER_SHAPES);
    }

    @Override
    public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
    {
        return generate(states, inner ? INNER_OCCLUSION_SHAPES : OUTER_OCCLUSION_SHAPES);
    }

    private static ShapeProvider generate(ImmutableList<BlockState> states, ShapeCache<ShapeKey> cache)
    {
        VoxelShape[] shapes = new VoxelShape[4 * 2 * 2];

        for (int i = 0; i < 4; i++)
        {
            boolean altType = (i & 1) != 0;
            ShapeUtils.makeHorizontalRotationsWithFlag(
                    cache.get(new ShapeKey(false, altType)),
                    cache.get(new ShapeKey(true, altType)),
                    Direction.NORTH,
                    altType,
                    shapes,
                    ThreewayCornerSlopeEdgeShapes::makeShapeIndex
            );
        }

        ImmutableMap.Builder<BlockState, VoxelShape> builder = new ImmutableMap.Builder<>();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            if (state.getValue(PropertyHolder.RIGHT))
            {
                dir = dir.getClockWise();
            }
            boolean top = state.getValue(FramedProperties.TOP);
            boolean altType = state.getValue(PropertyHolder.ALT_TYPE);
            builder.put(state, shapes[makeShapeIndex(dir, top, altType)]);
        }

        return ShapeProvider.of(builder.build());
    }

    private static int makeShapeIndex(Direction dir, boolean top, boolean altType)
    {
        return (dir.get2DDataValue() << 2) | (top ? 2 : 0) | (altType ? 1 : 0);
    }

    private static ShapeCache<ShapeKey> makeCache(ShapeCache<SlopeEdgeShapes.ShapeKey> cache, boolean inner)
    {
        return ShapeCache.create(map ->
        {
            VoxelShape edgeShapeHor = cache.get(new SlopeEdgeShapes.ShapeKey(SlopeType.HORIZONTAL, false));
            VoxelShape edgeShapeBottom = cache.get(new SlopeEdgeShapes.ShapeKey(SlopeType.BOTTOM, false));
            VoxelShape edgeShapeTop = cache.get(new SlopeEdgeShapes.ShapeKey(SlopeType.TOP, false));

            map.put(new ShapeKey(false, false), makeCornerShape(edgeShapeBottom, edgeShapeHor, inner));
            map.put(new ShapeKey(true, false), makeCornerShape(edgeShapeTop, edgeShapeHor, inner));

            VoxelShape edgeShapeHorAlt = cache.get(new SlopeEdgeShapes.ShapeKey(SlopeType.HORIZONTAL, true));
            VoxelShape edgeShapeBottomAlt = cache.get(new SlopeEdgeShapes.ShapeKey(SlopeType.BOTTOM, true));
            VoxelShape edgeShapeTopAlt = cache.get(new SlopeEdgeShapes.ShapeKey(SlopeType.TOP, true));

            map.put(new ShapeKey(false, true), makeAltCornerShape(edgeShapeBottomAlt, edgeShapeHorAlt, inner, false));
            map.put(new ShapeKey(true, true), makeAltCornerShape(edgeShapeTopAlt, edgeShapeHorAlt, inner, true));
        });
    }

    private static VoxelShape makeCornerShape(VoxelShape edgeShape, VoxelShape edgeShapeHor, boolean inner)
    {
        VoxelShape edgeShapeRot = ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.WEST, edgeShape);
        BooleanOp joinOp = inner ? BooleanOp.OR : BooleanOp.AND;
        return Shapes.joinUnoptimized(Shapes.joinUnoptimized(edgeShape, edgeShapeRot, joinOp), edgeShapeHor, joinOp);
    }

    private static VoxelShape makeAltCornerShape(VoxelShape edgeShape, VoxelShape edgeShapeHor, boolean inner, boolean top)
    {
        VoxelShape edgeShapeRot = ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.WEST, edgeShape);
        VoxelShape mask = Shapes.box(.5, top ? 0 : .5, .5, 1, top ? .5 : 1, 1);
        if (inner)
        {
            return ShapeUtils.andUnoptimized(
                    ShapeUtils.orUnoptimized(edgeShape, edgeShapeRot, edgeShapeHor),
                    mask
            );
        }
        else
        {
            return ShapeUtils.orUnoptimized(
                    ShapeUtils.andUnoptimized(edgeShape, edgeShapeRot, edgeShapeHor),
                    ShapeUtils.andUnoptimized(
                            ShapeUtils.orUnoptimized(edgeShape, edgeShapeRot, edgeShapeHor),
                            Shapes.joinUnoptimized(Shapes.block(), mask, BooleanOp.ONLY_FIRST)
                    )
            );
        }
    }



    public record ShapeKey(boolean top, boolean altType) { }
}
