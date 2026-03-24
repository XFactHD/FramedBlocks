package io.github.xfacthd.framedblocks.common.data.shapes.stairs.standard;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.CommonShapes;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class DoubleHalfStairsShapes
{
    public static ShapeContainer generate(List<BlockState> states)
    {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean right = state.getValue(PropertyHolder.RIGHT);
            dir = right ? dir.getClockWise() : dir.getCounterClockWise();
            map.put(state, CommonShapes.PANEL.get(dir));
        }

        return ShapeContainer.of(map);
    }

    private DoubleHalfStairsShapes() { }
}
