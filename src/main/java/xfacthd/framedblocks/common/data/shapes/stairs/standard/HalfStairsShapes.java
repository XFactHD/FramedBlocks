package xfacthd.framedblocks.common.data.shapes.stairs.standard;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeUtils;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class HalfStairsShapes
{
    public static ShapeProvider generate(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape bottomLeft = ShapeUtils.orUnoptimized(
                Block.box(8, 0, 0, 16, 8, 16),
                Block.box(8, 8, 8, 16, 16, 16)
        );

        VoxelShape bottomRight = ShapeUtils.orUnoptimized(
                Block.box(0, 0, 0, 8, 8, 16),
                Block.box(0, 8, 8, 8, 16, 16)
        );

        VoxelShape topLeft = ShapeUtils.orUnoptimized(
                Block.box(8, 8, 0, 16, 16, 16),
                Block.box(8, 0, 8, 16, 8, 16)
        );

        VoxelShape topRight = ShapeUtils.orUnoptimized(
                Block.box(0, 8, 0, 8, 16, 16),
                Block.box(0, 0, 8, 8, 8, 16)
        );

        int maskTop = 0b0100;
        int maskRight = 0b1000;
        VoxelShape[] shapes = new VoxelShape[4 * 4];
        ShapeUtils.makeHorizontalRotations(bottomLeft, Direction.SOUTH, shapes, 0);
        ShapeUtils.makeHorizontalRotations(bottomRight, Direction.SOUTH, shapes, maskRight);
        ShapeUtils.makeHorizontalRotations(topLeft, Direction.SOUTH, shapes, maskTop);
        ShapeUtils.makeHorizontalRotations(topRight, Direction.SOUTH, shapes, maskTop | maskRight);

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            int top = state.getValue(FramedProperties.TOP) ? maskTop : 0;
            int right = state.getValue(PropertyHolder.RIGHT) ? maskRight : 0;
            builder.put(state, shapes[dir.get2DDataValue() | top | right]);
        }

        return ShapeProvider.of(builder.build());
    }
}
