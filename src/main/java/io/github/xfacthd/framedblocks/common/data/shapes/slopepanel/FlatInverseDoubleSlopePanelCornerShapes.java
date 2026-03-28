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
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class FlatInverseDoubleSlopePanelCornerShapes implements ShapeGenerator {
    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        return generate(states, FlatSlopePanelCornerShapes.SHAPES, FlatSlopePanelCornerShapes.INNER_SHAPES);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states) {
        return generate(states, FlatSlopePanelCornerShapes.OCCLUSION_SHAPES, FlatSlopePanelCornerShapes.INNER_OCCLUSION_SHAPES);
    }

    private static ShapeContainer generate(
            List<BlockState> states,
            ShapeCache<FlatSlopePanelCornerShapes.ShapeKey> cache,
            ShapeCache<FlatSlopePanelCornerShapes.ShapeKey> innerCache
    ) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        VoxelShape[] shapes = new VoxelShape[4 * 4];
        for (HorizontalRotation rot : HorizontalRotation.values()) {
            VoxelShape frontShape = cache.get(new FlatSlopePanelCornerShapes.ShapeKey(rot.getOpposite(), true));
            HorizontalRotation backRot = rot.rotate(rot.isVertical() ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90);
            VoxelShape backShape = innerCache.get(new FlatSlopePanelCornerShapes.ShapeKey(backRot, true));
            backShape = ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.SOUTH, backShape);

            VoxelShape preShape = ShapeUtils.orUnoptimized(frontShape, backShape);
            ShapeUtils.makeHorizontalRotations(preShape, Direction.NORTH, shapes, rot.ordinal() << 2);
        }

        for (BlockState state : states) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            int idx = dir.get2DDataValue() | (rot.ordinal() << 2);
            map.put(state, shapes[idx]);
        }

        return ShapeContainer.of(map);
    }
}
