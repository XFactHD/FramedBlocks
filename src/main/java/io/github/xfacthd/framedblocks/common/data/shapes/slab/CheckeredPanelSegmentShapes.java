package io.github.xfacthd.framedblocks.common.data.shapes.slab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class CheckeredPanelSegmentShapes
{
    public static ShapeContainer generate(List<BlockState> states)
    {
        VoxelShape shapeFirst = ShapeUtils.orUnoptimized(
                Block.box(0, 0, 0,  8,  8,  8),
                Block.box(8, 8, 0, 16, 16,  8)
        );
        VoxelShape shapeSecond = ShapeUtils.orUnoptimized(
                Block.box(0, 8, 0,  8, 16,  8),
                Block.box(8, 0, 0, 16,  8,  8)
        );

        VoxelShape[] shapes = new VoxelShape[8];
        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            int idx = dir.get2DDataValue();
            boolean x = DirUtils.isX(dir);
            shapes[idx] = ShapeUtils.rotateShapeAroundY(Direction.NORTH, dir, x ? shapeSecond : shapeFirst);
            shapes[idx + 4] = ShapeUtils.rotateShapeAroundY(Direction.NORTH, dir, x ? shapeFirst : shapeSecond);
        }

        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());
        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean second = state.getValue(PropertyHolder.SECOND);
            int idx = dir.get2DDataValue() + (second ? 4 : 0);
            map.put(state, shapes[idx]);
        }
        return ShapeContainer.of(map);
    }

    private CheckeredPanelSegmentShapes() { }
}
