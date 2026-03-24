package io.github.xfacthd.framedblocks.common.data.shapes.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.block.slopepanel.SlopePanelShape;
import io.github.xfacthd.framedblocks.common.block.slopeslab.SlopeSlabShape;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import io.github.xfacthd.framedblocks.common.data.shapes.slopepanel.SlopePanelShapes;
import io.github.xfacthd.framedblocks.common.data.shapes.slopeslab.SlopeSlabShapes;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class CornerSlopePanelWallShapes
{
    public static final ShapeCache<HorizontalRotation> SHAPES_LARGE = makeCache(ExtendedCornerSlopePanelWallShapes.SHAPES, BooleanOp.NOT_SAME);
    public static final ShapeCache<HorizontalRotation> OCCLUSION_SHAPES_LARGE = makeCache(ExtendedCornerSlopePanelWallShapes.OCCLUSION_SHAPES, BooleanOp.NOT_SAME);
    public static final ShapeCache<HorizontalRotation> SHAPES_SMALL_INNER = makeCache(ExtendedCornerSlopePanelWallShapes.INNER_SHAPES, BooleanOp.AND);
    public static final ShapeCache<HorizontalRotation> OCCLUSION_SHAPES_SMALL_INNER = makeCache(ExtendedCornerSlopePanelWallShapes.INNER_OCCLUSION_SHAPES, BooleanOp.AND);

    public static final class SmallOuter implements ShapeGenerator
    {
        @Override
        public ShapeContainer generatePrimary(List<BlockState> states)
        {
            return generate(states, SlopeSlabShapes.SHAPES, SlopePanelShapes.SHAPES);
        }

        @Override
        public ShapeContainer generateOcclusion(List<BlockState> states)
        {
            return generate(states, SlopeSlabShapes.OCCLUSION_SHAPES, SlopePanelShapes.OCCLUSION_SHAPES);
        }

        private static ShapeContainer generate(List<BlockState> states, ShapeCache<SlopeSlabShape> slabCache, ShapeCache<SlopePanelShape> panelCache)
        {
            Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

            VoxelShape shapeOneUpLeft = ShapeUtils.rotateShapeUnoptimizedAroundY(
                    Direction.NORTH,
                    Direction.WEST,
                    panelCache.get(SlopePanelShape.LEFT_BACK)
            );
            VoxelShape shapeOneDownRight = ShapeUtils.rotateShapeUnoptimizedAroundY(
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
                map.put(state, shapes[idx]);
            }

            return ShapeContainer.of(map);
        }
    }

    public static final class LargeOuter implements ShapeGenerator
    {
        @Override
        public ShapeContainer generatePrimary(List<BlockState> states)
        {
            return generate(states, SHAPES_LARGE);
        }

        @Override
        public ShapeContainer generateOcclusion(List<BlockState> states)
        {
            return generate(states, OCCLUSION_SHAPES_LARGE);
        }

        private static ShapeContainer generate(List<BlockState> states, ShapeCache<HorizontalRotation> cache)
        {
            Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

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
                map.put(state, shapes[idx]);
            }

            return ShapeContainer.of(map);
        }
    }

    public static final class SmallInner implements ShapeGenerator
    {
        @Override
        public ShapeContainer generatePrimary(List<BlockState> states)
        {
            return generate(states, SHAPES_SMALL_INNER);
        }

        @Override
        public ShapeContainer generateOcclusion(List<BlockState> states)
        {
            return generate(states, OCCLUSION_SHAPES_SMALL_INNER);
        }

        private static ShapeContainer generate(List<BlockState> states, ShapeCache<HorizontalRotation> cache)
        {
            Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

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
                map.put(state, shapes[idx]);
            }

            return ShapeContainer.of(map);
        }
    }

    public static final class LargeInner implements ShapeGenerator
    {
        @Override
        public ShapeContainer generatePrimary(List<BlockState> states)
        {
            return generate(states, SlopeSlabShapes.SHAPES, SlopePanelShapes.SHAPES);
        }

        @Override
        public ShapeContainer generateOcclusion(List<BlockState> states)
        {
            return generate(states, SlopeSlabShapes.OCCLUSION_SHAPES, SlopePanelShapes.OCCLUSION_SHAPES);
        }

        private static ShapeContainer generate(List<BlockState> states, ShapeCache<SlopeSlabShape> slabCache, ShapeCache<SlopePanelShape> panelCache)
        {
            Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

            VoxelShape shapeOneUpLeft = ShapeUtils.rotateShapeUnoptimizedAroundY(
                    Direction.NORTH,
                    Direction.EAST,
                    panelCache.get(SlopePanelShape.RIGHT_BACK)
            );
            VoxelShape shapeOneDownRight = ShapeUtils.rotateShapeUnoptimizedAroundY(
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
                map.put(state, shapes[idx]);
            }

            return ShapeContainer.of(map);
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

    private CornerSlopePanelWallShapes() { }
}
