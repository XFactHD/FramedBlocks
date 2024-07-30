package xfacthd.framedblocks.common.data.shapes.slopepanelcorner;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.block.slopepanel.SlopePanelShape;
import xfacthd.framedblocks.common.block.slopeslab.SlopeSlabShape;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;
import xfacthd.framedblocks.common.data.shapes.slopepanel.SlopePanelShapes;
import xfacthd.framedblocks.common.data.shapes.slopeslab.SlopeSlabShapes;

public final class CornerSlopePanelWallShapes
{
    public static final ShapeCache<HorizontalRotation> SHAPES_LARGE = makeCache(ExtendedCornerSlopePanelWallShapes.SHAPES, BooleanOp.NOT_SAME);
    public static final ShapeCache<HorizontalRotation> OCCLUSION_SHAPES_LARGE = makeCache(ExtendedCornerSlopePanelWallShapes.OCCLUSION_SHAPES, BooleanOp.NOT_SAME);
    public static final ShapeCache<HorizontalRotation> SHAPES_SMALL_INNER = makeCache(ExtendedCornerSlopePanelWallShapes.INNER_SHAPES, BooleanOp.AND);
    public static final ShapeCache<HorizontalRotation> OCCLUSION_SHAPES_SMALL_INNER = makeCache(ExtendedCornerSlopePanelWallShapes.INNER_OCCLUSION_SHAPES, BooleanOp.AND);

    public static final class SmallOuter implements SplitShapeGenerator
    {
        @Override
        public ShapeProvider generate(ImmutableList<BlockState> states)
        {
            return generate(states, SlopeSlabShapes.SHAPES, SlopePanelShapes.SHAPES);
        }

        @Override
        public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
        {
            return generate(states, SlopeSlabShapes.OCCLUSION_SHAPES, SlopePanelShapes.OCCLUSION_SHAPES);
        }

        private static ShapeProvider generate(
                ImmutableList<BlockState> states, ShapeCache<SlopeSlabShape> slabCache, ShapeCache<SlopePanelShape> panelCache
        )
        {
            ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

            VoxelShape shapeOneUpLeft = ShapeUtils.rotateShapeUnoptimized(
                    Direction.NORTH,
                    Direction.WEST,
                    panelCache.get(SlopePanelShape.LEFT_BACK)
            );
            VoxelShape shapeOneDownRight = ShapeUtils.rotateShapeUnoptimized(
                    Direction.NORTH,
                    Direction.EAST,
                    panelCache.get(SlopePanelShape.RIGHT_BACK)
            );

            VoxelShape[] shapes = new VoxelShape[4 * 4];
            for (HorizontalRotation rot : HorizontalRotation.values())
            {
                VoxelShape shapeOne = switch (rot)
                {
                    case UP, LEFT -> shapeOneUpLeft;
                    case DOWN, RIGHT -> shapeOneDownRight;
                };
                VoxelShape shapeTwo = switch (rot)
                {
                    case UP, RIGHT -> slabCache.get(SlopeSlabShape.TOP_TOP_HALF);
                    case DOWN, LEFT -> slabCache.get(SlopeSlabShape.BOTTOM_BOTTOM_HALF);
                };
                VoxelShape preShape = ShapeUtils.andUnoptimized(shapeOne, shapeTwo);
                ShapeUtils.makeHorizontalRotations(preShape, Direction.NORTH, shapes, rot.ordinal() << 2);
            }

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

    public static final class LargeOuter implements SplitShapeGenerator
    {
        @Override
        public ShapeProvider generate(ImmutableList<BlockState> states)
        {
            return generate(states, SHAPES_LARGE);
        }

        @Override
        public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
        {
            return generate(states, OCCLUSION_SHAPES_LARGE);
        }

        private static ShapeProvider generate(ImmutableList<BlockState> states, ShapeCache<HorizontalRotation> cache)
        {
            ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

            VoxelShape[] shapes = new VoxelShape[4 * 4];
            for (HorizontalRotation rot : HorizontalRotation.values())
            {
                VoxelShape preShape = cache.get(rot);
                ShapeUtils.makeHorizontalRotations(preShape, Direction.NORTH, shapes, rot.ordinal() << 2);
            }

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

    public static final class SmallInner implements SplitShapeGenerator
    {
        @Override
        public ShapeProvider generate(ImmutableList<BlockState> states)
        {
            return generate(states, SHAPES_SMALL_INNER);
        }

        @Override
        public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
        {
            return generate(states, OCCLUSION_SHAPES_SMALL_INNER);
        }

        private static ShapeProvider generate(ImmutableList<BlockState> states, ShapeCache<HorizontalRotation> cache)
        {
            ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

            VoxelShape[] shapes = new VoxelShape[4 * 4];
            for (HorizontalRotation rot : HorizontalRotation.values())
            {
                VoxelShape preShape = cache.get(rot);
                ShapeUtils.makeHorizontalRotations(preShape, Direction.NORTH, shapes, rot.ordinal() << 2);
            }

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

    public static final class LargeInner implements SplitShapeGenerator
    {
        @Override
        public ShapeProvider generate(ImmutableList<BlockState> states)
        {
            return generate(states, SlopeSlabShapes.SHAPES, SlopePanelShapes.SHAPES);
        }

        @Override
        public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
        {
            return generate(states, SlopeSlabShapes.OCCLUSION_SHAPES, SlopePanelShapes.OCCLUSION_SHAPES);
        }

        private static ShapeProvider generate(
                ImmutableList<BlockState> states, ShapeCache<SlopeSlabShape> slabCache, ShapeCache<SlopePanelShape> panelCache
        )
        {
            ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

            VoxelShape shapeOneUpLeft = ShapeUtils.rotateShapeUnoptimized(
                    Direction.NORTH,
                    Direction.EAST,
                    panelCache.get(SlopePanelShape.RIGHT_BACK)
            );
            VoxelShape shapeOneDownRight = ShapeUtils.rotateShapeUnoptimized(
                    Direction.NORTH,
                    Direction.WEST,
                    panelCache.get(SlopePanelShape.LEFT_BACK)
            );

            VoxelShape[] shapes = new VoxelShape[4 * 4];
            for (HorizontalRotation rot : HorizontalRotation.values())
            {
                VoxelShape shapeOne = switch (rot)
                {
                    case UP, LEFT -> shapeOneUpLeft;
                    case DOWN, RIGHT -> shapeOneDownRight;
                };
                VoxelShape shapeTwo = switch (rot)
                {
                    case UP, RIGHT -> slabCache.get(SlopeSlabShape.BOTTOM_BOTTOM_HALF);
                    case DOWN, LEFT -> slabCache.get(SlopeSlabShape.TOP_TOP_HALF);
                };
                VoxelShape preShape = ShapeUtils.orUnoptimized(shapeOne, shapeTwo);
                ShapeUtils.makeHorizontalRotations(preShape, Direction.NORTH, shapes, rot.ordinal() << 2);
            }

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

    private static ShapeCache<HorizontalRotation> makeCache(ShapeCache<HorizontalRotation> cache, BooleanOp joinOp)
    {
        return ShapeCache.createEnum(HorizontalRotation.class, map ->
        {
            for (HorizontalRotation rot : HorizontalRotation.values())
            {
                map.put(rot, Shapes.joinUnoptimized(cache.get(rot), rot.getCornerShape(), joinOp));
            }
        });
    }
}
