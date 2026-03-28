package io.github.xfacthd.framedblocks.common.data.shapes.slopepanel;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.block.slopepanel.SlopePanelShape;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class InverseDoubleSlopePanelShapes implements ShapeGenerator {
    public static final InverseDoubleSlopePanelShapes INSTANCE = new InverseDoubleSlopePanelShapes();
    private static final ShapeCache<ShapeKey> SHAPES = makeCache(SlopePanelShapes.SHAPES);
    private static final ShapeCache<ShapeKey> OCCLUSION_SHAPES = makeCache(SlopePanelShapes.OCCLUSION_SHAPES);

    private InverseDoubleSlopePanelShapes() { }

    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        return generate(states, SHAPES);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states) {
        return generate(states, OCCLUSION_SHAPES);
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

    private static ShapeCache<ShapeKey> makeCache(ShapeCache<SlopePanelShape> cache) {
        return ShapeCache.create(map -> {
            for (HorizontalRotation rot : HorizontalRotation.values()) {
                HorizontalRotation rotOne = rot.isVertical() ? rot.getOpposite() : rot;
                VoxelShape shapeOne = cache.get(SlopePanelShape.get(rotOne, true));
                VoxelShape preShape = ShapeUtils.orUnoptimized(
                        ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.SOUTH, shapeOne),
                        cache.get(SlopePanelShape.get(rot, true))
                );
                ShapeUtils.makeHorizontalRotations(preShape, Direction.NORTH, map, rot, ShapeKey::new);
            }
        });
    }

    private record ShapeKey(Direction dir, HorizontalRotation rot) { }
}
