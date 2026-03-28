package io.github.xfacthd.framedblocks.common.data.shapes.stairs.standard;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.CommonShapes;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.data.shapes.slope.VerticalHalfSlopeShapes;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class SlopedStairsShapes implements ShapeGenerator {
    public static final SlopedStairsShapes INSTANCE = new SlopedStairsShapes();
    private static final ShapeCache<CommonShapes.DirBoolKey> SHAPES = makeCache(VerticalHalfSlopeShapes.SHAPES);
    private static final ShapeCache<CommonShapes.DirBoolKey> OCCLUSION_SHAPES = makeCache(VerticalHalfSlopeShapes.OCCLUSION_SHAPES);

    private SlopedStairsShapes() { }

    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        return generateShapes(states, SHAPES);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states) {
        return generateShapes(states, OCCLUSION_SHAPES);
    }

    private static ShapeContainer generateShapes(List<BlockState> states, ShapeCache<CommonShapes.DirBoolKey> shapeCache) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        for (BlockState state : states) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            map.put(state, shapeCache.get(new CommonShapes.DirBoolKey(dir, top)));
        }

        return ShapeContainer.of(map);
    }

    private static ShapeCache<CommonShapes.DirBoolKey> makeCache(ShapeCache<Boolean> shapeCache) {
        return ShapeCache.create(map -> {
            VoxelShape shapeBottom = ShapeUtils.orUnoptimized(
                    shapeCache.get(Boolean.TRUE),
                    CommonShapes.SLAB.get(Boolean.FALSE)
            );

            VoxelShape shapeTop = ShapeUtils.orUnoptimized(
                    shapeCache.get(Boolean.FALSE),
                    CommonShapes.SLAB.get(Boolean.TRUE)
            );

            ShapeUtils.makeHorizontalRotationsWithFlag(shapeBottom, shapeTop, Direction.NORTH, map, CommonShapes.DirBoolKey::new);
        });
    }
}
