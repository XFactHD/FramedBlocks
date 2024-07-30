package xfacthd.framedblocks.common.data.shapes.slopepanelcorner;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.block.slopepanel.SlopePanelShape;
import xfacthd.framedblocks.common.data.CornerSlopePanelShape;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;
import xfacthd.framedblocks.common.data.shapes.slopepanel.SlopePanelShapes;

public final class CornerSlopePanelShapes implements SplitShapeGenerator
{
    public static final CornerSlopePanelShapes SMALL_OUTER = new CornerSlopePanelShapes(CornerSlopePanelShape.SMALL_BOTTOM, CornerSlopePanelShape.SMALL_TOP, Direction.NORTH);
    public static final CornerSlopePanelShapes LARGE_OUTER = new CornerSlopePanelShapes(CornerSlopePanelShape.LARGE_BOTTOM, CornerSlopePanelShape.LARGE_TOP, Direction.NORTH);
    public static final CornerSlopePanelShapes SMALL_INNER = new CornerSlopePanelShapes(CornerSlopePanelShape.SMALL_INNER_BOTTOM, CornerSlopePanelShape.SMALL_INNER_TOP, Direction.SOUTH);
    public static final CornerSlopePanelShapes LARGE_INNER = new CornerSlopePanelShapes(CornerSlopePanelShape.LARGE_INNER_BOTTOM, CornerSlopePanelShape.LARGE_INNER_TOP, Direction.SOUTH);
    public static final ShapeCache<CornerSlopePanelShape> SHAPES = makeCache(SlopePanelShapes.SHAPES);
    public static final ShapeCache<CornerSlopePanelShape> OCCLUSION_SHAPES = makeCache(SlopePanelShapes.OCCLUSION_SHAPES);

    private final CornerSlopePanelShape bottomShape;
    private final CornerSlopePanelShape topShape;
    private final Direction srcDir;

    private CornerSlopePanelShapes(CornerSlopePanelShape bottomShape, CornerSlopePanelShape topShape, Direction srcDir)
    {
        this.bottomShape = bottomShape;
        this.topShape = topShape;
        this.srcDir = srcDir;
    }

    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        return generate(states, SHAPES);
    }

    @Override
    public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
    {
        return generate(states, OCCLUSION_SHAPES);
    }

    private ShapeProvider generate(ImmutableList<BlockState> states, ShapeCache<CornerSlopePanelShape> cache)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(cache.get(bottomShape), cache.get(topShape), srcDir);

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeProvider.of(builder.build());
    }

    private static ShapeCache<CornerSlopePanelShape> makeCache(ShapeCache<SlopePanelShape> cache)
    {
        return ShapeCache.createEnum(CornerSlopePanelShape.class, map ->
        {
            {
                VoxelShape panelShapeBottom = cache.get(SlopePanelShape.UP_BACK);
                map.put(CornerSlopePanelShape.SMALL_BOTTOM, ShapeUtils.andUnoptimized(
                        panelShapeBottom,
                        ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, panelShapeBottom)
                ));
            }

            {
                VoxelShape panelShapeTop = cache.get(SlopePanelShape.DOWN_BACK);
                map.put(CornerSlopePanelShape.SMALL_TOP, ShapeUtils.andUnoptimized(
                        panelShapeTop,
                        ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, panelShapeTop)
                ));
            }

            {
                VoxelShape panelShapeBot = cache.get(SlopePanelShape.UP_FRONT);
                VoxelShape panelShapeBotRot = ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, panelShapeBot);
                map.put(CornerSlopePanelShape.LARGE_BOTTOM, ShapeUtils.orUnoptimized(
                        ShapeUtils.andUnoptimized(panelShapeBot, panelShapeBotRot),
                        ShapeUtils.orUnoptimized(
                                ShapeUtils.andUnoptimized(panelShapeBot, Block.box(0, 0, 8, 8, 16, 16)),
                                ShapeUtils.andUnoptimized(panelShapeBotRot, Block.box(8, 0, 0, 16, 16, 8))
                        )
                ));
            }

            {
                VoxelShape panelShapeTop = cache.get(SlopePanelShape.DOWN_FRONT);
                VoxelShape panelShapeTopRot = ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, panelShapeTop);
                map.put(CornerSlopePanelShape.LARGE_TOP, ShapeUtils.orUnoptimized(
                        ShapeUtils.andUnoptimized(panelShapeTop, panelShapeTopRot),
                        ShapeUtils.orUnoptimized(
                                ShapeUtils.andUnoptimized(panelShapeTop, Block.box(0, 0, 8, 8, 16, 16)),
                                ShapeUtils.andUnoptimized(panelShapeTopRot, Block.box(8, 0, 0, 16, 16, 8))
                        )
                ));
            }

            {
                VoxelShape panelShapeBottom = cache.get(SlopePanelShape.UP_FRONT);
                map.put(CornerSlopePanelShape.SMALL_INNER_BOTTOM, ShapeUtils.andUnoptimized(
                        Block.box(8, 0, 8, 16, 16, 16),
                        ShapeUtils.orUnoptimized(
                                panelShapeBottom,
                                ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, panelShapeBottom)
                        )
                ));
            }

            {
                VoxelShape panelShapeTop = cache.get(SlopePanelShape.DOWN_FRONT);
                map.put(CornerSlopePanelShape.SMALL_INNER_TOP, ShapeUtils.andUnoptimized(
                        Block.box(8, 0, 8, 16, 16, 16),
                        ShapeUtils.orUnoptimized(
                                panelShapeTop,
                                ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, panelShapeTop)
                        )
                ));
            }

            {
                VoxelShape panelShapeBottom = cache.get(SlopePanelShape.UP_BACK);
                map.put(CornerSlopePanelShape.LARGE_INNER_BOTTOM, ShapeUtils.orUnoptimized(
                        panelShapeBottom,
                        ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, panelShapeBottom)
                ));
            }

            {
                VoxelShape panelShapeTop = cache.get(SlopePanelShape.DOWN_BACK);
                map.put(CornerSlopePanelShape.LARGE_INNER_TOP, ShapeUtils.orUnoptimized(
                        panelShapeTop,
                        ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, panelShapeTop)
                ));
            }
        });
    }
}
