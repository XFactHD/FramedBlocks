package io.github.xfacthd.framedblocks.common.data.shapes.slab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.CommonShapes;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class MasonryCornerSegmentShapes {
    public static ShapeContainer generate(List<BlockState> states) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        VoxelShape preShapeBottom = ShapeUtils.orUnoptimized(
                CommonShapes.SLAB_EDGE.get(new CommonShapes.DirBoolKey(Direction.SOUTH, false)),
                CommonShapes.SLAB_EDGE.get(new CommonShapes.DirBoolKey(Direction.EAST, true))
        );
        VoxelShape preShapeTop = ShapeUtils.orUnoptimized(
                CommonShapes.SLAB_EDGE.get(new CommonShapes.DirBoolKey(Direction.EAST, false)),
                CommonShapes.SLAB_EDGE.get(new CommonShapes.DirBoolKey(Direction.SOUTH, true))
        );
        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(preShapeBottom, preShapeTop, Direction.NORTH);

        for (BlockState state : states) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            map.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeContainer.of(map);
    }

    private MasonryCornerSegmentShapes() { }
}
