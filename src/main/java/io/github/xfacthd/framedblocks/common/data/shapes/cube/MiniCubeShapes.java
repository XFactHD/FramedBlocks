package io.github.xfacthd.framedblocks.common.data.shapes.cube;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class MiniCubeShapes
{
    public static ShapeContainer generate(List<BlockState> states)
    {
        VoxelShape bottomShape = Block.box(4, 0, 4, 12, 8, 12);
        VoxelShape topShape = Block.box(4, 8, 4, 12, 16, 12);

        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());
        for (BlockState state : states)
        {
            boolean top = state.getValue(FramedProperties.TOP);
            map.put(state, top ? topShape : bottomShape);
        }
        return ShapeContainer.of(map);
    }

    private MiniCubeShapes() { }
}
