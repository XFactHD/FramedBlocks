package xfacthd.framedblocks.common.data.shapes.door;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.shapes.*;

public final class GateShapes
{
    private static final ShapeCache<Direction> SHAPES = ShapeCache.createEnum(Direction.class, map ->
    {
        VoxelShape shape = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
        ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, map);
    });

    public static ShapeProvider generate(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            if (state.getValue(BlockStateProperties.OPEN))
            {
                boolean rightHinge = state.getValue(BlockStateProperties.DOOR_HINGE) == DoorHingeSide.RIGHT;
                dir = rightHinge ? dir.getCounterClockWise() : dir.getClockWise();
            }
            builder.put(state, SHAPES.get(dir));
        }

        return ShapeProvider.of(builder.build());
    }



    private GateShapes() { }
}
