package xfacthd.framedblocks.common.data.shapes.pillar;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.common.block.pillar.FramedChainBlock;

public final class ChainShapes
{
    public static ShapeProvider generate(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape[] shapes = new VoxelShape[] {
                Block.box(0.0, 6.5, 6.5, 16.0, 9.5, 9.5),
                Block.box(6.5, 0.0, 6.5, 9.5, 16.0, 9.5),
                Block.box(6.5, 6.5, 0.0, 9.5, 9.5, 16.0)
        };

        for (BlockState state : states)
        {
            Direction.Axis axis = state.getValue(FramedChainBlock.AXIS);
            builder.put(state, shapes[axis.ordinal()]);
        }

        return ShapeProvider.of(builder.build());
    }



    private ChainShapes() { }
}
