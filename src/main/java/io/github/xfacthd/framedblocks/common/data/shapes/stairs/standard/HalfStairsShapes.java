package io.github.xfacthd.framedblocks.common.data.shapes.stairs.standard;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
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

public final class HalfStairsShapes
{
    public static ShapeContainer generate(List<BlockState> states)
    {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        VoxelShape bottomLeft = ShapeUtils.orUnoptimized(
                Block.box(8, 0, 0, 16, 8, 16),
                Block.box(8, 8, 8, 16, 16, 16)
        );

        VoxelShape bottomRight = ShapeUtils.orUnoptimized(
                Block.box(0, 0, 0, 8, 8, 16),
                Block.box(0, 8, 8, 8, 16, 16)
        );

        VoxelShape topLeft = ShapeUtils.orUnoptimized(
                Block.box(8, 8, 0, 16, 16, 16),
                Block.box(8, 0, 8, 16, 8, 16)
        );

        VoxelShape topRight = ShapeUtils.orUnoptimized(
                Block.box(0, 8, 0, 8, 16, 16),
                Block.box(0, 0, 8, 8, 8, 16)
        );

        int maskTop = 0b0100;
        int maskRight = 0b1000;
        VoxelShape[] shapes = new VoxelShape[4 * 4];
        ShapeUtils.makeHorizontalRotations(bottomLeft, Direction.SOUTH, shapes, 0);
        ShapeUtils.makeHorizontalRotations(bottomRight, Direction.SOUTH, shapes, maskRight);
        ShapeUtils.makeHorizontalRotations(topLeft, Direction.SOUTH, shapes, maskTop);
        ShapeUtils.makeHorizontalRotations(topRight, Direction.SOUTH, shapes, maskTop | maskRight);

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            int top = state.getValue(FramedProperties.TOP) ? maskTop : 0;
            int right = state.getValue(PropertyHolder.RIGHT) ? maskRight : 0;
            map.put(state, shapes[dir.get2DDataValue() | top | right]);
        }

        return ShapeContainer.of(map);
    }

    private HalfStairsShapes() { }
}
