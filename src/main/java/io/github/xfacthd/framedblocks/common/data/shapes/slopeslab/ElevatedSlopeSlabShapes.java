package io.github.xfacthd.framedblocks.common.data.shapes.slopeslab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.block.slopeslab.SlopeSlabShape;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class ElevatedSlopeSlabShapes implements ShapeGenerator {
    public static final ElevatedSlopeSlabShapes INSTANCE = new ElevatedSlopeSlabShapes();
    public static final ShapeCache<Boolean> SHAPES = ShapeCache.createIdentity(map -> {
        map.put(Boolean.FALSE, ShapeUtils.orUnoptimized(
                SlopeSlabShapes.SHAPES.get(SlopeSlabShape.BOTTOM_TOP_HALF),
                Block.box(0, 0, 0, 16, 8, 16)
        ));

        map.put(Boolean.TRUE, ShapeUtils.orUnoptimized(
                SlopeSlabShapes.SHAPES.get(SlopeSlabShape.TOP_BOTTOM_HALF),
                Block.box(0, 8, 0, 16, 16, 16)
        ));
    });
    public static final ShapeCache<Boolean> OCCLUSION_SHAPES = ShapeCache.createIdentity(map -> {
        map.put(Boolean.FALSE, ShapeUtils.orUnoptimized(
                SlopeSlabShapes.OCCLUSION_SHAPES.get(SlopeSlabShape.BOTTOM_TOP_HALF),
                Block.box(0, 0, 0, 16, 8, 16)
        ));

        map.put(Boolean.TRUE, ShapeUtils.orUnoptimized(
                SlopeSlabShapes.OCCLUSION_SHAPES.get(SlopeSlabShape.TOP_BOTTOM_HALF),
                Block.box(0, 8, 0, 16, 16, 16)
        ));
    });
    private static final ShapeCache<ShapeKey> FINAL_SHAPES = ShapeCache.create(map ->
            ShapeUtils.makeHorizontalRotationsWithFlag(
                    SHAPES.get(Boolean.FALSE),
                    SHAPES.get(Boolean.TRUE),
                    Direction.NORTH,
                    map,
                    ShapeKey::new
            )
    );
    private static final ShapeCache<ShapeKey> FINAL_OCCLUSION_SHAPES = ShapeCache.create(map ->
            ShapeUtils.makeHorizontalRotationsWithFlag(
                    OCCLUSION_SHAPES.get(Boolean.FALSE),
                    OCCLUSION_SHAPES.get(Boolean.TRUE),
                    Direction.NORTH,
                    map,
                    ShapeKey::new
            )
    );

    private ElevatedSlopeSlabShapes() { }

    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        return generate(states, FINAL_SHAPES);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states) {
        return generate(states, FINAL_OCCLUSION_SHAPES);
    }

    private static ShapeContainer generate(List<BlockState> states, ShapeCache<ShapeKey> cache) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        for (BlockState state : states) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            map.put(state, cache.get(new ShapeKey(dir, top)));
        }

        return ShapeContainer.of(map);
    }

    private record ShapeKey(Direction dir, boolean top) { }
}
