package io.github.xfacthd.framedblocks.common.data.shapes.slopepanel;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class FlatExtendedSlopePanelCornerShapes implements ShapeGenerator {
    private static final ShapeCache<ShapeKey> FINAL_SHAPES = makeCache(ExtendedSlopePanelShapes.SHAPES, BooleanOp.AND);
    private static final ShapeCache<ShapeKey> FINAL_OCCLUSION_SHAPES = makeCache(ExtendedSlopePanelShapes.OCCLUSION_SHAPES, BooleanOp.AND);
    private static final ShapeCache<ShapeKey> FINAL_INNER_SHAPES = makeCache(ExtendedSlopePanelShapes.SHAPES, BooleanOp.OR);
    private static final ShapeCache<ShapeKey> FINAL_INNER_OCCLUSION_SHAPES = makeCache(ExtendedSlopePanelShapes.OCCLUSION_SHAPES, BooleanOp.OR);
    public static final FlatExtendedSlopePanelCornerShapes OUTER = new FlatExtendedSlopePanelCornerShapes(FINAL_SHAPES, FINAL_OCCLUSION_SHAPES);
    public static final FlatExtendedSlopePanelCornerShapes INNER = new FlatExtendedSlopePanelCornerShapes(FINAL_INNER_SHAPES, FINAL_INNER_OCCLUSION_SHAPES);

    private final ShapeCache<ShapeKey> shapes;
    private final ShapeCache<ShapeKey> occlusionShapes;

    private FlatExtendedSlopePanelCornerShapes(ShapeCache<ShapeKey> shapes, ShapeCache<ShapeKey> occlusionShapes) {
        this.shapes = shapes;
        this.occlusionShapes = occlusionShapes;
    }

    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        return generate(states, shapes);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states) {
        return generate(states, occlusionShapes);
    }

    private static ShapeContainer generate(List<BlockState> states, ShapeCache<ShapeKey> cache) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        for (BlockState state : states) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            map.put(state, cache.get(new ShapeKey(dir, rot)));
        }

        return ShapeContainer.of(map);
    }

    private static ShapeCache<ShapeKey> makeCache(ShapeCache<HorizontalRotation> cache, BooleanOp joinOp) {
        return ShapeCache.create(map -> {
            for (HorizontalRotation rot : HorizontalRotation.values()) {
                VoxelShape shape = Shapes.joinUnoptimized(
                        cache.get(rot), cache.get(rot.rotate(Rotation.COUNTERCLOCKWISE_90)), joinOp
                );
                ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, map, rot, ShapeKey::new);
            }
        });
    }

    private record ShapeKey(Direction dir, HorizontalRotation rot) { }
}
