package io.github.xfacthd.framedblocks.common.data.shapes.pillar;

import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.common.block.pillar.FramedChainBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class ChainShapes
{
    public static ShapeContainer generate(List<BlockState> states)
    {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        VoxelShape[] shapes = new VoxelShape[] {
                Block.box(0.0, 6.5, 6.5, 16.0, 9.5, 9.5),
                Block.box(6.5, 0.0, 6.5, 9.5, 16.0, 9.5),
                Block.box(6.5, 6.5, 0.0, 9.5, 9.5, 16.0)
        };

        for (BlockState state : states)
        {
            Direction.Axis axis = state.getValue(FramedChainBlock.AXIS);
            map.put(state, shapes[axis.ordinal()]);
        }

        return ShapeContainer.of(map);
    }

    private ChainShapes() { }
}
