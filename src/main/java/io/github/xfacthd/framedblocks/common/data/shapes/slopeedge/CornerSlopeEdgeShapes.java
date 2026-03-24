package io.github.xfacthd.framedblocks.common.data.shapes.slopeedge;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.CommonShapes;
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

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class CornerSlopeEdgeShapes implements ShapeGenerator
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
    public ShapeContainer generatePrimary(List<BlockState> states)
    {
        return generate(states, inner ? INNER_SHAPES : OUTER_SHAPES);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states)
    {
        return generate(states, inner ? INNER_OCCLUSION_SHAPES : OUTER_OCCLUSION_SHAPES);
    }

    private static ShapeContainer generate(List<BlockState> states, ShapeCache<ShapeKey> cache)
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

        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
            boolean altType = state.getValue(PropertyHolder.ALT_TYPE);
            map.put(state, shapes[makeShapeIndex(dir, type, altType)]);
        }

        return ShapeContainer.of(map);
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
