package io.github.xfacthd.framedblocks.common.data.shapes.door;

import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class GateShapes
{
    private static final ShapeCache<Direction> SHAPES = ShapeCache.createEnum(Direction.class, map ->
    {
        VoxelShape shape = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
        ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, map);
    });

    public static ShapeContainer generate(List<BlockState> states)
    {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        for (BlockState state : states)
        {
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            if (state.getValue(BlockStateProperties.OPEN))
            {
                boolean rightHinge = state.getValue(BlockStateProperties.DOOR_HINGE) == DoorHingeSide.RIGHT;
                dir = rightHinge ? dir.getCounterClockWise() : dir.getClockWise();
            }
            map.put(state, SHAPES.get(dir));
        }

        return ShapeContainer.of(map);
    }

    private GateShapes() { }
}
