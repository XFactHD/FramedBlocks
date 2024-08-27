package xfacthd.framedblocks.common.data.shapes.slopeedge;

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

public final class CornerSlopeEdgeShapes implements SplitShapeGenerator
{
    public static final ShapeCache<ShapeKey> OUTER_SHAPES = makeCache(SlopeEdgeShapes.SHAPES, false);
    public static final ShapeCache<ShapeKey> OUTER_OCCLUSION_SHAPES = makeCache(SlopeEdgeShapes.OCCLUSION_SHAPES, false);
    public static final ShapeCache<ShapeKey> INNER_SHAPES = makeCache(SlopeEdgeShapes.SHAPES, true);
    public static final ShapeCache<ShapeKey> INNER_OCCLUSION_SHAPES = makeCache(SlopeEdgeShapes.OCCLUSION_SHAPES, true);

    public static final CornerSlopeEdgeShapes OUTER = new CornerSlopeEdgeShapes(false);
    public static final CornerSlopeEdgeShapes INNER = new CornerSlopeEdgeShapes(true);

    private static final CornerType[] TYPES = CornerType.values();

    private final boolean inner;

    private CornerSlopeEdgeShapes(boolean inner)
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
        VoxelShape[] shapes = new VoxelShape[4 * 2 * 6];

        for (CornerType type : TYPES)
        {
            ShapeUtils.makeHorizontalRotations(
                    cache.get(new ShapeKey(type, false)),
                    Direction.NORTH,
                    shapes,
                    type,
                    (dir, idxType) -> makeShapeIndex(dir, idxType, false)
            );
            ShapeUtils.makeHorizontalRotations(
                    cache.get(new ShapeKey(type, true)),
                    Direction.NORTH,
                    shapes,
                    type,
                    (dir, idxType) -> makeShapeIndex(dir, idxType, true)
            );
        }

        ImmutableMap.Builder<BlockState, VoxelShape> builder = new ImmutableMap.Builder<>();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
            boolean altType = state.getValue(PropertyHolder.ALT_TYPE);
            builder.put(state, shapes[makeShapeIndex(dir, type, altType)]);
        }

        return ShapeProvider.of(builder.build());
    }

    private static int makeShapeIndex(Direction dir, CornerType type, boolean altType)
    {
        return (type.ordinal() << 3) | (dir.get2DDataValue() << 1) | (altType ? 1 : 0);
    }

    private static ShapeCache<ShapeKey> makeCache(ShapeCache<SlopeEdgeShapes.ShapeKey> cache, boolean inner)
    {
        return ShapeCache.create(map ->
        {
            VoxelShape edgeShapeBottom = cache.get(new SlopeEdgeShapes.ShapeKey(SlopeType.BOTTOM, false));
            map.put(new ShapeKey(CornerType.BOTTOM, false), makeCornerShape(edgeShapeBottom, inner));

            VoxelShape edgeShapeTop = cache.get(new SlopeEdgeShapes.ShapeKey(SlopeType.TOP, false));
            map.put(new ShapeKey(CornerType.TOP, false), makeCornerShape(edgeShapeTop, inner));

            VoxelShape edgeBotLeft = Shapes.joinUnoptimized(
                    edgeShapeBottom,
                    cache.get(new SlopeEdgeShapes.ShapeKey(SlopeType.HORIZONTAL, false)),
                    inner ? BooleanOp.OR : BooleanOp.AND
            );
            map.put(new ShapeKey(CornerType.HORIZONTAL_BOTTOM_LEFT, false), edgeBotLeft);
            map.put(new ShapeKey(CornerType.HORIZONTAL_TOP_LEFT, false), ShapeUtils.rotateShapeUnoptimizedAroundZ(
                    Direction.WEST, Direction.UP, edgeBotLeft
            ));
            map.put(new ShapeKey(CornerType.HORIZONTAL_TOP_RIGHT, false), ShapeUtils.rotateShapeUnoptimizedAroundZ(
                    Direction.WEST, Direction.EAST, edgeBotLeft
            ));
            map.put(new ShapeKey(CornerType.HORIZONTAL_BOTTOM_RIGHT, false), ShapeUtils.rotateShapeUnoptimizedAroundZ(
                    Direction.WEST, Direction.DOWN, edgeBotLeft
            ));

            VoxelShape edgeShapeBottomAlt = cache.get(new SlopeEdgeShapes.ShapeKey(SlopeType.BOTTOM, true));
            map.put(new ShapeKey(CornerType.BOTTOM, true), makeAltCornerShape(edgeShapeBottomAlt, inner));

            VoxelShape edgeShapeTopAlt = cache.get(new SlopeEdgeShapes.ShapeKey(SlopeType.TOP, true));
            map.put(new ShapeKey(CornerType.TOP, true), makeAltCornerShape(edgeShapeTopAlt, inner));

            VoxelShape edgeShapeHorAlt = cache.get(new SlopeEdgeShapes.ShapeKey(SlopeType.HORIZONTAL, true));
            VoxelShape edgeBotLeftAlt;
            if (inner)
            {
                edgeBotLeftAlt = ShapeUtils.andUnoptimized(
                        ShapeUtils.orUnoptimized(
                                edgeShapeBottomAlt,
                                edgeShapeHorAlt
                        ),
                        CommonShapes.SLAB_EDGE.get(new CommonShapes.DirBoolKey(Direction.EAST, true))
                );
            }
            else
            {
                edgeBotLeftAlt = ShapeUtils.orUnoptimized(
                        ShapeUtils.andUnoptimized(
                                edgeShapeBottomAlt,
                                edgeShapeHorAlt
                        ),
                        ShapeUtils.andUnoptimized(
                                ShapeUtils.orUnoptimized(
                                        edgeShapeBottomAlt,
                                        edgeShapeHorAlt
                                ),
                                CommonShapes.STRAIGHT_STAIRS.get(new CommonShapes.DirBoolKey(Direction.WEST, false))
                        )
                );
            }
            map.put(new ShapeKey(CornerType.HORIZONTAL_BOTTOM_LEFT, true), edgeBotLeftAlt);
            map.put(new ShapeKey(CornerType.HORIZONTAL_TOP_LEFT, true), ShapeUtils.rotateShapeUnoptimizedAroundZ(
                    Direction.WEST, Direction.UP, edgeBotLeftAlt
            ));
            map.put(new ShapeKey(CornerType.HORIZONTAL_TOP_RIGHT, true), ShapeUtils.rotateShapeUnoptimizedAroundZ(
                    Direction.WEST, Direction.EAST, edgeBotLeftAlt
            ));
            map.put(new ShapeKey(CornerType.HORIZONTAL_BOTTOM_RIGHT, true), ShapeUtils.rotateShapeUnoptimizedAroundZ(
                    Direction.WEST, Direction.DOWN, edgeBotLeftAlt
            ));
        });
    }

    private static VoxelShape makeCornerShape(VoxelShape edgeShape, boolean inner)
    {
        return Shapes.joinUnoptimized(
                edgeShape,
                ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.WEST, edgeShape),
                inner ? BooleanOp.OR : BooleanOp.AND
        );
    }

    private static VoxelShape makeAltCornerShape(VoxelShape edgeShape, boolean inner)
    {
        if (inner)
        {
            return ShapeUtils.andUnoptimized(
                    ShapeUtils.orUnoptimized(
                            edgeShape,
                            ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.WEST, edgeShape)
                    ),
                    CommonShapes.CORNER_PILLAR.get(Direction.SOUTH)
            );
        }
        else
        {
            VoxelShape edgeShapeRot = ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.WEST, edgeShape);
            return ShapeUtils.orUnoptimized(
                    ShapeUtils.andUnoptimized(
                            edgeShape,
                            edgeShapeRot
                    ),
                    ShapeUtils.andUnoptimized(
                            ShapeUtils.orUnoptimized(
                                    edgeShape,
                                    edgeShapeRot
                            ),
                            CommonShapes.STRAIGHT_VERTICAL_STAIRS.get(Direction.NORTH)
                    )
            );
        }
    }



    public record ShapeKey(CornerType type, boolean altType) { }
}
