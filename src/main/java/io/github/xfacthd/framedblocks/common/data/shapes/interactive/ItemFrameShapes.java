package io.github.xfacthd.framedblocks.common.data.shapes.interactive;

import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class ItemFrameShapes {
    private static final ShapeCache<ShapeKey> SHAPES = ShapeCache.create(map -> {
        map.put(new ShapeKey(Direction.UP,   false), Block.box(2, 15, 2, 14, 16, 14));
        map.put(new ShapeKey(Direction.UP,   true),  Block.box(0, 15, 0, 16, 16, 16));
        map.put(new ShapeKey(Direction.DOWN, false), Block.box(2,  0, 2, 14,  1, 14));
        map.put(new ShapeKey(Direction.DOWN, true),  Block.box(0,  0, 0, 16,  1, 16));

        VoxelShape northShape = Block.box(2, 2, 0, 14, 14, 1);
        VoxelShape northMapShape = Block.box(0, 0, 0, 16, 16, 1);
        ShapeUtils.makeHorizontalRotationsWithFlag(northShape, northMapShape, Direction.NORTH, map, ShapeKey::new);
    });

    public static ShapeContainer generate(List<BlockState> states) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        for (BlockState state : states) {
            Direction dir = state.getValue(BlockStateProperties.FACING);
            boolean mapFrame = state.getValue(PropertyHolder.MAP_FRAME);
            map.put(state, SHAPES.get(new ShapeKey(dir, mapFrame)));
        }

        return ShapeContainer.of(map);
    }

    private record ShapeKey(Direction dir, boolean map) { }

    private ItemFrameShapes() { }
}
