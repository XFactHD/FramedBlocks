package io.github.xfacthd.framedblocks.common.data.shapes.slab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class SlabCornerShapes {
    public static ShapeContainer generate(List<BlockState> states) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        VoxelShape shapeBot = Block.box(0, 0, 0, 8,  8, 8);
        VoxelShape shapeTop = Block.box(0, 8, 0, 8, 16, 8);
        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(shapeBot, shapeTop, Direction.NORTH);

        for (BlockState state : states) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            map.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeContainer.of(map);
    }

    private SlabCornerShapes() { }
}
