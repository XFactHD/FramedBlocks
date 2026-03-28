package io.github.xfacthd.framedblocks.common.data.shapes.pillar;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.data.property.StairsType;
import io.github.xfacthd.framedblocks.common.data.shapes.stairs.vertical.VerticalStairsShapes;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class ThreewayCornerPillarShapes {
    public static ShapeContainer generate(List<BlockState> states) {
        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(
                VerticalStairsShapes.SHAPES.get(new VerticalStairsShapes.ShapeKey(Direction.NORTH, StairsType.TOP_BOTH)),
                VerticalStairsShapes.SHAPES.get(new VerticalStairsShapes.ShapeKey(Direction.NORTH, StairsType.BOTTOM_BOTH)),
                Direction.NORTH
        );

        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        for (BlockState state : states) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            map.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeContainer.of(map);
    }

    private ThreewayCornerPillarShapes() { }
}
