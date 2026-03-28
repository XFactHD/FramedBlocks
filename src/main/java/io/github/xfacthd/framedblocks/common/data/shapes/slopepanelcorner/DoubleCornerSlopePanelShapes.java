package io.github.xfacthd.framedblocks.common.data.shapes.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.CommonShapes;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class DoubleCornerSlopePanelShapes {
    public static ShapeContainer generate(List<BlockState> states) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        for (BlockState state : states) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            map.put(state, CommonShapes.STRAIGHT_VERTICAL_STAIRS.get(dir.getOpposite()));
        }

        return ShapeContainer.of(map);
    }

    private DoubleCornerSlopePanelShapes() { }
}
