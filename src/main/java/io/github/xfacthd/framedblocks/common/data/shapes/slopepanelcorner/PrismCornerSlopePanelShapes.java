package io.github.xfacthd.framedblocks.common.data.shapes.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class PrismCornerSlopePanelShapes implements ShapeGenerator {
    public static final PrismCornerSlopePanelShapes SMALL_OUTER = new PrismCornerSlopePanelShapes(PrismCornerShape.SMALL_OUTER);
    public static final PrismCornerSlopePanelShapes LARGE_OUTER = new PrismCornerSlopePanelShapes(PrismCornerShape.LARGE_OUTER);
    public static final PrismCornerSlopePanelShapes SMALL_INNER = new PrismCornerSlopePanelShapes(PrismCornerShape.SMALL_INNER);
    public static final PrismCornerSlopePanelShapes LARGE_INNER = new PrismCornerSlopePanelShapes(PrismCornerShape.LARGE_INNER);
    static final ShapeCache<PrismCornerShape> SHAPES = makeCache();
    static final ShapeCache<PrismCornerShape> OCCLUSION_SHAPES = makeOcclusionCache();

    private final PrismCornerShape cornerShape;

    private PrismCornerSlopePanelShapes(PrismCornerShape cornerShape) {
        this.cornerShape = cornerShape;
    }

    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        return generate(states, SHAPES);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states) {
        return generate(states, OCCLUSION_SHAPES);
    }

    private ShapeContainer generate(List<BlockState> states, ShapeCache<PrismCornerShape> cache) {
        VoxelShape baseShape = cache.get(cornerShape);
        if (baseShape.isEmpty()) {
            return ShapeContainer.EMPTY;
        }

        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        VoxelShape invBaseShape = ShapeUtils.mirrorShapeUnoptimizedAlongY(baseShape);
        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(baseShape, invBaseShape, Direction.NORTH);

        for (BlockState state : states) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            map.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeContainer.of(map);
    }

    private static ShapeCache<PrismCornerShape> makeCache() {
        return ShapeCache.createEnum(PrismCornerShape.class, map -> {
            map.put(PrismCornerShape.SMALL_OUTER, ShapeUtils.orUnoptimized(
                    Block.box(0, 0, 0, 4, 16, 4),
                    Block.box(0, 0, 0, 4,  8, 8),
                    Block.box(0, 0, 0, 8,  8, 4)
            ));
            map.put(PrismCornerShape.LARGE_OUTER, ShapeUtils.orUnoptimized(
                    Block.box(0, 0, 0,  4,  8, 16),
                    Block.box(0, 0, 0,  8,  8, 12),
                    Block.box(0, 0, 0, 12,  8,  8),
                    Block.box(0, 0, 0, 16,  8,  4),
                    Block.box(0, 8, 0,  4, 16, 12),
                    Block.box(0, 8, 0,  8, 16,  8),
                    Block.box(0, 8, 0, 12, 16,  4)
            ));
            map.put(PrismCornerShape.SMALL_INNER, ShapeUtils.orUnoptimized(
                    Block.box(0, 0, 0, 16,  8, 16),
                    Block.box(0, 8, 0, 12, 16, 16),
                    Block.box(0, 8, 0, 16, 16, 12)
            ));
            map.put(PrismCornerShape.LARGE_INNER, ShapeUtils.orUnoptimized(
                    Block.box(0, 0, 0, 12,  8, 16),
                    Block.box(0, 0, 0, 16,  8, 12),
                    Block.box(0, 8, 0,  8, 16, 16),
                    Block.box(0, 8, 0, 12, 16, 12),
                    Block.box(0, 8, 0, 16, 16,  8)
            ));
        });
    }

    private static ShapeCache<PrismCornerShape> makeOcclusionCache() {
        return ShapeCache.createEnum(PrismCornerShape.class, map -> {
            map.put(PrismCornerShape.SMALL_OUTER, Shapes.empty());
            map.put(PrismCornerShape.LARGE_OUTER, ShapeUtils.orUnoptimized(
                    Block.box(0, 0, 0,  8, .5,  8),
                    Block.box(0, 0, 0,  8, 16, .5),
                    Block.box(0, 0, 0, .5, 16,  8)
            ));
            map.put(PrismCornerShape.SMALL_INNER, ShapeUtils.orUnoptimized(
                    Block.box(0, 0, 0, 16, 16,  8),
                    Block.box(0, 0, 0,  8, 16, 16),
                    Block.box(0, 0, 0, 16, .5, 16)
            ));
            map.put(PrismCornerShape.LARGE_INNER, ShapeUtils.orUnoptimized(
                    Block.box(0, 0, 0, 16, .5,  8),
                    Block.box(0, 0, 0,  8, .5, 16),
                    Block.box(0, 0, 0, 16, 16, .5),
                    Block.box(0, 0, 0, .5, 16, 16),
                    Block.box(0, 0, 0,  8, 16,  8)
            ));
        });
    }

    enum PrismCornerShape {
        SMALL_OUTER,
        LARGE_OUTER,
        SMALL_INNER,
        LARGE_INNER,
    }
}
