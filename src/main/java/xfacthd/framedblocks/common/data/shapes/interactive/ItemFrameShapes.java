package xfacthd.framedblocks.common.data.shapes.interactive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class ItemFrameShapes
{
    private static final ShapeCache<ShapeKey> SHAPES = ShapeCache.create(map ->
    {
        map.put(new ShapeKey(Direction.UP,   false), Block.box(2, 15, 2, 14, 16, 14));
        map.put(new ShapeKey(Direction.UP,   true),  Block.box(0, 15, 0, 16, 16, 16));
        map.put(new ShapeKey(Direction.DOWN, false), Block.box(2,  0, 2, 14,  1, 14));
        map.put(new ShapeKey(Direction.DOWN, true),  Block.box(0,  0, 0, 16,  1, 16));

        VoxelShape northShape = Block.box(2, 2, 0, 14, 14, 1);
        VoxelShape northMapShape = Block.box(0, 0, 0, 16, 16, 1);
        ShapeUtils.makeHorizontalRotationsWithFlag(northShape, northMapShape, Direction.NORTH, map, ShapeKey::new);
    });

    public static ShapeProvider generate(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(BlockStateProperties.FACING);
            boolean map = state.getValue(PropertyHolder.MAP_FRAME);
            builder.put(state, SHAPES.get(new ShapeKey(dir, map)));
        }

        return ShapeProvider.of(builder.build());
    }



    private record ShapeKey(Direction dir, boolean map) { }



    private ItemFrameShapes() { }
}
