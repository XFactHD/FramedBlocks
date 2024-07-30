package xfacthd.framedblocks.common.data.shapes.stairs.vertical;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.CommonShapes;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class VerticalSlicedStairsShapes
{
    public static ShapeProvider generate(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean right = state.getValue(PropertyHolder.RIGHT);
            builder.put(state, CommonShapes.STRAIGHT_VERTICAL_STAIRS.get(right ? dir.getClockWise() : dir));
        }

        return ShapeProvider.of(builder.build());
    }



    private VerticalSlicedStairsShapes() { }
}
