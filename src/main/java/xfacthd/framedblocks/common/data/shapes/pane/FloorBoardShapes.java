package xfacthd.framedblocks.common.data.shapes.pane;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.ShapeProvider;

public final class FloorBoardShapes
{
    public static ShapeProvider generate(ImmutableList<BlockState> states)
    {
        VoxelShape shapeBottom = Block.box(0, 0, 0, 16, 1, 16);
        VoxelShape shapeTop = Block.box(0, 15, 0, 16, 16, 16);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(state, top ? shapeTop : shapeBottom);
        }

        return ShapeProvider.of(builder.build());
    }



    private FloorBoardShapes() { }
}
