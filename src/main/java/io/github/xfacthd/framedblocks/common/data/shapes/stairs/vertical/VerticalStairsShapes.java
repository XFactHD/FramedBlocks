package io.github.xfacthd.framedblocks.common.data.shapes.stairs.vertical;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.CommonShapes;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.StairsType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class VerticalStairsShapes
{
    public static final ShapeCache<ShapeKey> SHAPES = ShapeCache.create(map ->
    {
        VoxelShape topFwdShape = ShapeUtils.orUnoptimized(
                Block.box(0, 0, 0, 8, 16, 16),
                Block.box(8, 0, 0, 16, 8, 8)
        );

        VoxelShape topCcwShape = ShapeUtils.orUnoptimized(
                Block.box(0, 0, 0, 16, 16, 8),
                Block.box(0, 0, 8, 8, 8, 16)
        );

        VoxelShape topBothShape = ShapeUtils.orUnoptimized(
                Block.box(8, 0, 8, 16, 16, 16),
                Block.box(8, 0, 0, 16, 8, 8),
                Block.box(0, 0, 8, 8, 8, 16)
        );

        VoxelShape bottomFwdShape = ShapeUtils.orUnoptimized(
                Block.box(0, 0, 0, 8, 16, 16),
                Block.box(8, 8, 0, 16, 16, 8)
        );

        VoxelShape bottomCcwShape = ShapeUtils.orUnoptimized(
                Block.box(0, 0, 0, 16, 16, 8),
                Block.box(0, 8, 8, 8, 16, 16)
        );

        VoxelShape bottomBothShape = ShapeUtils.orUnoptimized(
                Block.box(8, 0, 8, 16, 16, 16),
                Block.box(8, 8, 0, 16, 16, 8),
                Block.box(0, 8, 8, 8, 16, 16)
        );

        CommonShapes.STRAIGHT_VERTICAL_STAIRS.forEach((dir, shape) ->
                map.put(new ShapeKey(dir, StairsType.VERTICAL), shape)
        );

        ShapeUtils.makeHorizontalRotations(topFwdShape, Direction.NORTH, map, StairsType.TOP_FWD, ShapeKey::new);
        ShapeUtils.makeHorizontalRotations(topCcwShape, Direction.NORTH, map, StairsType.TOP_CCW, ShapeKey::new);
        ShapeUtils.makeHorizontalRotations(topBothShape, Direction.SOUTH, map, StairsType.TOP_BOTH, ShapeKey::new);
        ShapeUtils.makeHorizontalRotations(bottomFwdShape, Direction.NORTH, map, StairsType.BOTTOM_FWD, ShapeKey::new);
        ShapeUtils.makeHorizontalRotations(bottomCcwShape, Direction.NORTH, map, StairsType.BOTTOM_CCW, ShapeKey::new);
        ShapeUtils.makeHorizontalRotations(bottomBothShape, Direction.SOUTH, map, StairsType.BOTTOM_BOTH, ShapeKey::new);
    });

    public static ShapeContainer generate(List<BlockState> states)
    {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);
            map.put(state, SHAPES.get(new ShapeKey(dir, type)));
        }

        return ShapeContainer.of(map);
    }

    public record ShapeKey(Direction dir, StairsType type) { }

    private VerticalStairsShapes() { }
}
