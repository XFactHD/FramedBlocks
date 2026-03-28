package io.github.xfacthd.framedblocks.common.data.shapes.slab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class CenteredPanelShapes {
    public static ShapeContainer generate(List<BlockState> states) {
        VoxelShape shapeNorth = Block.box(0, 0, 4, 16, 16, 12);
        VoxelShape shapeEast = Block.box(4, 0, 0, 12, 16, 16);

        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());
        for (BlockState state : states) {
            Direction dir = state.getValue(FramedProperties.FACING_NE);
            map.put(state, dir == Direction.NORTH ? shapeNorth : shapeEast);
        }
        return ShapeContainer.of(map);
    }

    private CenteredPanelShapes() { }
}
