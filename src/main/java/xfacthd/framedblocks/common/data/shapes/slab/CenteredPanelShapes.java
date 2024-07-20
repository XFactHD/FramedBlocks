package xfacthd.framedblocks.common.data.shapes.slab;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.ShapeProvider;

public final class CenteredPanelShapes
{
    public static ShapeProvider generate(ImmutableList<BlockState> states)
    {
        VoxelShape shapeNorth = Block.box(0, 0, 4, 16, 16, 12);
        VoxelShape shapeEast = Block.box(4, 0, 0, 12, 16, 16);

        ImmutableMap.Builder<BlockState, VoxelShape> shapes = ImmutableMap.builder();
        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_NE);
            shapes.put(state, dir == Direction.NORTH ? shapeNorth : shapeEast);
        }
        return ShapeProvider.of(shapes.build());
    }
}
