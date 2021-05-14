package xfacthd.framedblocks.common.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.shapes.VoxelShape;

public interface VoxelShapeGenerator
{
    VoxelShapeGenerator EMTPTY = states -> ImmutableMap.<BlockState, VoxelShape>builder().build();

    ImmutableMap<BlockState, VoxelShape> generate(ImmutableList<BlockState> states);

    static VoxelShapeGenerator singleShape(VoxelShape shape)
    {
        return states ->
        {
            ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
            states.forEach(state -> builder.put(state, shape));
            return builder.build();
        };
    }
}