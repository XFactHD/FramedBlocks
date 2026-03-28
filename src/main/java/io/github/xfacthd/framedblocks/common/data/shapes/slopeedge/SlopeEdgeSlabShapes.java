package io.github.xfacthd.framedblocks.common.data.shapes.slopeedge;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.CommonShapes;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class SlopeEdgeSlabShapes implements ShapeGenerator {
    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        return generateShapes(states, SlopeEdgeShapes.SHAPES);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states) {
        return generateShapes(states, SlopeEdgeShapes.OCCLUSION_SHAPES);
    }

    private static ShapeContainer generateShapes(List<BlockState> states, ShapeCache<SlopeEdgeShapes.ShapeKey> edgeShapes) {
        VoxelShape[] shapes = new VoxelShape[4 * 4];
        for (int i = 0; i < 4; i++) {
            boolean topHalf = i >> 1 != 0;
            boolean top = (i & 1) != 0;

            SlopeType type = top ? SlopeType.TOP : SlopeType.BOTTOM;
            boolean altType = top != topHalf;
            VoxelShape slopeEdgeShape = edgeShapes.get(new SlopeEdgeShapes.ShapeKey(type, altType));
            if (!altType) {
                slopeEdgeShape = slopeEdgeShape.move(0, 0, .5);
            }
            VoxelShape slabEdgeShape = CommonShapes.SLAB_EDGE.get(new CommonShapes.DirBoolKey(Direction.NORTH, topHalf));
            VoxelShape shape = ShapeUtils.orUnoptimized(slabEdgeShape, slopeEdgeShape);

            ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, shapes, i * 4);
        }

        Map<BlockState, VoxelShape> map = new IdentityHashMap<>();
        for (BlockState state : states) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);
            boolean top = state.getValue(FramedProperties.TOP);
            int idx = dir.get2DDataValue() | (topHalf ? 0b1000 : 0) | (top ? 0b0100 : 0);
            map.put(state, shapes[idx]);
        }
        return ShapeContainer.of(map);
    }
}
