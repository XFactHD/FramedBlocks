package io.github.xfacthd.framedblocks.common.data.shapes.stairs.vertical;

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

public final class VerticalHalfStairsShapes
{
    public static ShapeContainer generate(List<BlockState> states)
    {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        VoxelShape shapeBottom = ShapeUtils.orUnoptimized(
                Block.box(0, 0, 8, 16, 8, 16),
                Block.box(8, 0, 0, 16, 8,  8)
        );

        VoxelShape shapeTop = ShapeUtils.orUnoptimized(
                Block.box(0, 8, 8, 16, 16, 16),
                Block.box(8, 8, 0, 16, 16,  8)
        );

        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(shapeBottom, shapeTop, Direction.NORTH);

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR).getOpposite();
            boolean top = state.getValue(FramedProperties.TOP);
            map.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeContainer.of(map);
    }

    private VerticalHalfStairsShapes() { }
}
