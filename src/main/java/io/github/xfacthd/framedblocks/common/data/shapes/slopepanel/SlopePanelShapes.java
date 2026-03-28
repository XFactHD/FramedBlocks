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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class SlopePanelShapes implements ShapeGenerator {
    public static final ShapeCache<SlopePanelShape> SHAPES = makeCache(() -> ShapeUtils.orUnoptimized(
            Block.box(0, 0, 0, 16, 16, 4),
            Block.box(0, 0, 0, 16,  8, 8)
    ));
    public static final ShapeCache<SlopePanelShape> OCCLUSION_SHAPES = makeCache(() -> ShapeUtils.orUnoptimized(
            Block.box(0, 0, 0, 16, .5, 8),
            Block.box(0, 0, 0, 16,  8, 7.75),
            Block.box(0, 0, 0, 16, 15, 4),
            Block.box(0, 15, 0, 16, 16, 0.5)
    ));

    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        return generate(states, SHAPES);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states) {
        return generate(states, OCCLUSION_SHAPES);
    }

    private static ShapeContainer generate(List<BlockState> states, ShapeCache<SlopePanelShape> cache) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        int maskFront = 0b10000;
        VoxelShape[] shapes = new VoxelShape[4 * 4 * 2];
        for (HorizontalRotation rot : HorizontalRotation.values()) {
            VoxelShape shape = cache.get(SlopePanelShape.get(rot, false));
            VoxelShape shapeFront = cache.get(SlopePanelShape.get(rot, true));
            ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, shapes, rot.ordinal() << 2);
            ShapeUtils.makeHorizontalRotations(shapeFront, Direction.NORTH, shapes, maskFront | (rot.ordinal() << 2));
        }

        for (BlockState state : states) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            int front = state.getValue(PropertyHolder.FRONT) ? maskFront : 0;
            int idx = dir.get2DDataValue() | (rot.ordinal() << 2) | front;
            map.put(state, shapes[idx]);
        }

        return ShapeContainer.of(map);
    }

    private static ShapeCache<SlopePanelShape> makeCache(Supplier<VoxelShape> upShapeFactory) {
        return ShapeCache.createEnum(SlopePanelShape.class, map -> {
            VoxelShape shapeUp = upShapeFactory.get();
            map.put(SlopePanelShape.UP_BACK, shapeUp);
            map.put(SlopePanelShape.UP_FRONT, shapeUp.move(0, 0, .5));

            VoxelShape shapeRight = ShapeUtils.rotateShapeUnoptimizedAroundZ(Direction.UP, Direction.EAST, shapeUp);
            map.put(SlopePanelShape.RIGHT_BACK, shapeRight);
            map.put(SlopePanelShape.RIGHT_FRONT, shapeRight.move(0, 0, .5));

            VoxelShape shapeDown = ShapeUtils.rotateShapeUnoptimizedAroundZ(Direction.UP, Direction.DOWN, shapeUp);
            map.put(SlopePanelShape.DOWN_BACK, shapeDown);
            map.put(SlopePanelShape.DOWN_FRONT, shapeDown.move(0, 0, .5));

            VoxelShape shapeLeft = ShapeUtils.rotateShapeUnoptimizedAroundZ(Direction.UP, Direction.WEST, shapeUp);
            map.put(SlopePanelShape.LEFT_BACK, shapeLeft);
            map.put(SlopePanelShape.LEFT_FRONT, shapeLeft.move(0, 0, .5));
        });
    }
}
