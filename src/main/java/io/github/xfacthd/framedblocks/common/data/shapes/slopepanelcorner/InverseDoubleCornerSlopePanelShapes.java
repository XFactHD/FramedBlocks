package io.github.xfacthd.framedblocks.common.data.shapes.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.data.CornerSlopePanelShape;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class InverseDoubleCornerSlopePanelShapes implements ShapeGenerator {
    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        return generate(states, CornerSlopePanelShapes.SHAPES);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states) {
        return generate(states, CornerSlopePanelShapes.OCCLUSION_SHAPES);
    }

    private static ShapeContainer generate(List<BlockState> states, ShapeCache<CornerSlopePanelShape> cache) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        VoxelShape shapeBottom = ShapeUtils.orUnoptimized(
                cache.get(CornerSlopePanelShape.LARGE_BOTTOM),
                ShapeUtils.rotateShapeUnoptimizedAroundY(
                        Direction.NORTH,
                        Direction.SOUTH,
                        cache.get(CornerSlopePanelShape.SMALL_INNER_TOP))
        );
        VoxelShape shapeTop = ShapeUtils.orUnoptimized(
                cache.get(CornerSlopePanelShape.LARGE_TOP),
                ShapeUtils.rotateShapeUnoptimizedAroundY(
                        Direction.NORTH,
                        Direction.SOUTH,
                        cache.get(CornerSlopePanelShape.SMALL_INNER_BOTTOM)
                )
        );
        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(shapeBottom, shapeTop, Direction.NORTH);

        for (BlockState state : states) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            map.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeContainer.of(map);
    }
}
