package xfacthd.framedblocks.common.data.shapes.slab;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeUtils;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class CheckeredCubeSegmentShapes
{
    public static ShapeProvider generate(ImmutableList<BlockState> states)
    {
        VoxelShape shapeFirst = ShapeUtils.or(
                Block.box(0, 0, 0,  8,  8,  8),
                Block.box(8, 0, 8, 16,  8, 16),
                Block.box(8, 8, 0, 16, 16,  8),
                Block.box(0, 8, 8,  8, 16, 16)
        );
        VoxelShape shapeSecond = ShapeUtils.rotateShapeAroundY(Direction.NORTH, Direction.EAST, shapeFirst);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
        for (BlockState state : states)
        {
            boolean second = state.getValue(PropertyHolder.SECOND);
            builder.put(state, second ? shapeSecond : shapeFirst);
        }
        return ShapeProvider.of(builder.build());
    }



    private CheckeredCubeSegmentShapes() { }
}
