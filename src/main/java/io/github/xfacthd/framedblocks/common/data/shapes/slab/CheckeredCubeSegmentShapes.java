package io.github.xfacthd.framedblocks.common.data.shapes.slab;

import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class CheckeredCubeSegmentShapes
{
    public static ShapeContainer generate(List<BlockState> states)
    {
        VoxelShape shapeFirst = ShapeUtils.or(
                Block.box(0, 0, 0,  8,  8,  8),
                Block.box(8, 0, 8, 16,  8, 16),
                Block.box(8, 8, 0, 16, 16,  8),
                Block.box(0, 8, 8,  8, 16, 16)
        );
        VoxelShape shapeSecond = ShapeUtils.rotateShapeAroundY(Direction.NORTH, Direction.EAST, shapeFirst);

        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());
        for (BlockState state : states)
        {
            boolean second = state.getValue(PropertyHolder.SECOND);
            map.put(state, second ? shapeSecond : shapeFirst);
        }
        return ShapeContainer.of(map);
    }

    private CheckeredCubeSegmentShapes() { }
}
