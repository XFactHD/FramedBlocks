package io.github.xfacthd.framedblocks.common.data.shapes.sign;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class WallSignShapes
{
    public static ShapeContainer generate(List<BlockState> states)
    {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        for (BlockState state : states)
        {
            switch (state.getValue(FramedProperties.FACING_HOR))
            {
                case NORTH -> map.put(state, Block.box(0.0D, 4.5D, 14.0D, 16.0D, 12.5D, 16.0D));
                case EAST -> map.put(state, Block.box(0.0D, 4.5D, 0.0D, 2.0D, 12.5D, 16.0D));
                case SOUTH -> map.put(state, Block.box(0.0D, 4.5D, 0.0D, 16.0D, 12.5D, 2.0D));
                case WEST -> map.put(state, Block.box(14.0D, 4.5D, 0.0D, 16.0D, 12.5D, 16.0D));
            }
        }

        return ShapeContainer.of(map);
    }

    private WallSignShapes() { }
}
