package xfacthd.framedblocks.common.data.shapes.stairs.vertical;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeUtils;

public final class VerticalHalfStairsShapes
{
    public static ShapeProvider generate(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeBottom = ShapeUtils.orUnoptimized(
                Block.box(0, 0, 8, 16, 8, 16),
                Block.box(8, 0, 0, 16, 8,  8)
        );

        VoxelShape shapeTop = ShapeUtils.orUnoptimized(
                Block.box(0, 8, 8, 16, 16, 16),
                Block.box(8, 8, 0, 16, 16,  8)
        );

        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(shapeBottom, shapeTop, Direction.NORTH);

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR).getOpposite();
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeProvider.of(builder.build());
    }



    private VerticalHalfStairsShapes() { }
}
