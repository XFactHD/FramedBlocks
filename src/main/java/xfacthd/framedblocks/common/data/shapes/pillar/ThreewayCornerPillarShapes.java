package xfacthd.framedblocks.common.data.shapes.pillar;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeUtils;
import xfacthd.framedblocks.common.data.property.StairsType;
import xfacthd.framedblocks.common.data.shapes.stairs.vertical.VerticalStairsShapes;

public final class ThreewayCornerPillarShapes
{
    public static ShapeProvider generate(ImmutableList<BlockState> states)
    {
        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(
                VerticalStairsShapes.SHAPES.get(new VerticalStairsShapes.ShapeKey(Direction.NORTH, StairsType.TOP_BOTH)),
                VerticalStairsShapes.SHAPES.get(new VerticalStairsShapes.ShapeKey(Direction.NORTH, StairsType.BOTTOM_BOTH)),
                Direction.NORTH
        );

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeProvider.of(builder.build());
    }



    private ThreewayCornerPillarShapes() { }
}
