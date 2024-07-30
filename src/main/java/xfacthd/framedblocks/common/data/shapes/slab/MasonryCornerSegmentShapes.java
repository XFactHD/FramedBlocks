package xfacthd.framedblocks.common.data.shapes.slab;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;

public final class MasonryCornerSegmentShapes
{
    public static ShapeProvider generate(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape preShape = ShapeUtils.orUnoptimized(
                CommonShapes.SLAB_EDGE.get(new CommonShapes.DirBoolKey(Direction.SOUTH, false)),
                CommonShapes.SLAB_EDGE.get(new CommonShapes.DirBoolKey(Direction.EAST, true))
        );
        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotations(preShape, Direction.NORTH);

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            builder.put(state, shapes[dir.get2DDataValue()]);
        }

        return ShapeProvider.of(builder.build());
    }



    private MasonryCornerSegmentShapes() { }
}
