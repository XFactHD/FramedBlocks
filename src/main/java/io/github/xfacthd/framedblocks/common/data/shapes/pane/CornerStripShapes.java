package io.github.xfacthd.framedblocks.common.data.shapes.pane;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class CornerStripShapes
{
    public static ShapeContainer generate(List<BlockState> states)
    {
        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(
                Block.box(0,  0, 0, 16,  1, 1),
                Block.box(0, 15, 0, 16, 16, 1),
                Direction.NORTH
        );
        VoxelShape[] vertShapes = ShapeUtils.makeHorizontalRotations(
                Block.box(0, 0, 0, 1, 16, 1),
                Direction.NORTH
        );

        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());
        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
            if (type == SlopeType.HORIZONTAL)
            {
                map.put(state, vertShapes[dir.get2DDataValue()]);
            }
            else
            {
                int offset = type == SlopeType.TOP ? 4 : 0;
                map.put(state, shapes[dir.get2DDataValue() + offset]);
            }
        }
        return ShapeContainer.of(map);
    }

    private CornerStripShapes() { }
}
