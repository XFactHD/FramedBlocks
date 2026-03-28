package io.github.xfacthd.framedblocks.common.data.shapes.slopeedge;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.CommonShapes;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class SlopeEdgePanelShapes implements ShapeGenerator {
    private static final HorizontalRotation[] ROTATIONS = HorizontalRotation.values();

    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        return generateShapes(states, SlopeEdgeShapes.SHAPES);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states) {
        return generateShapes(states, SlopeEdgeShapes.OCCLUSION_SHAPES);
    }

    private static ShapeContainer generateShapes(List<BlockState> states, ShapeCache<SlopeEdgeShapes.ShapeKey> edgeShapes) {
        VoxelShape[] shapes = new VoxelShape[4 * 8];
        for (int i = 0; i < 8; i++) {
            HorizontalRotation rot = ROTATIONS[i >> 1];
            boolean front = (i & 1) != 0;

            VoxelShape slabEdgeShape = switch (rot) {
                case UP -> CommonShapes.SLAB_EDGE.get(new CommonShapes.DirBoolKey(front ? Direction.SOUTH : Direction.NORTH, false));
                case DOWN -> CommonShapes.SLAB_EDGE.get(new CommonShapes.DirBoolKey(front ? Direction.SOUTH : Direction.NORTH, true));
                case RIGHT -> CommonShapes.CORNER_PILLAR.get(front ? Direction.WEST : Direction.NORTH);
                case LEFT -> CommonShapes.CORNER_PILLAR.get(front ? Direction.SOUTH : Direction.EAST);
            };
            VoxelShape slopeEdgeShape = switch (rot) {
                case UP -> {
                    VoxelShape shape = edgeShapes.get(new SlopeEdgeShapes.ShapeKey(SlopeType.BOTTOM, front));
                    if (!front) { shape = shape.move(0, .5, 0); }
                    yield shape;
                }
                case DOWN -> {
                    VoxelShape shape = edgeShapes.get(new SlopeEdgeShapes.ShapeKey(SlopeType.TOP, front));
                    if (!front) { shape = shape.move(0, -.5, 0); }
                    yield shape;
                }
                case RIGHT -> {
                    VoxelShape shape = edgeShapes.get(new SlopeEdgeShapes.ShapeKey(SlopeType.HORIZONTAL, front));
                    if (!front) { shape = shape.move(.5, 0, 0); }
                    yield shape;
                }
                case LEFT -> {
                    VoxelShape shape = edgeShapes.get(new SlopeEdgeShapes.ShapeKey(SlopeType.HORIZONTAL, front));
                    shape = ShapeUtils.rotateShapeAroundY(Direction.NORTH, Direction.EAST, shape);
                    if (!front) { shape = shape.move(-.5, 0, 0); }
                    yield shape;
                }
            };
            VoxelShape shape = ShapeUtils.orUnoptimized(slabEdgeShape, slopeEdgeShape);

            ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, shapes, i * 4);
        }

        Map<BlockState, VoxelShape> map = new IdentityHashMap<>();
        for (BlockState state : states) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            boolean front = state.getValue(PropertyHolder.FRONT);
            int idx = dir.get2DDataValue() | (rot.ordinal() << 3) | (front ? 0b100 : 0);
            map.put(state, shapes[idx]);
        }
        return ShapeContainer.of(map);
    }
}
