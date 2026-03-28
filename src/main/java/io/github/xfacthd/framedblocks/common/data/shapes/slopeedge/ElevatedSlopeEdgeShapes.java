package io.github.xfacthd.framedblocks.common.data.shapes.slopeedge;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import io.github.xfacthd.framedblocks.common.data.property.StairsType;
import io.github.xfacthd.framedblocks.common.data.shapes.stairs.vertical.VerticalStairsShapes;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class ElevatedSlopeEdgeShapes implements ShapeGenerator {
    public static final ElevatedSlopeEdgeShapes INSTANCE = new ElevatedSlopeEdgeShapes();

    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        return generate(states, SlopeEdgeShapes.SHAPES);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states) {
        return generate(states, SlopeEdgeShapes.OCCLUSION_SHAPES);
    }

    private static ShapeContainer generate(List<BlockState> states, ShapeCache<SlopeEdgeShapes.ShapeKey> cache) {
        VoxelShape shapeBottom = ShapeUtils.orUnoptimized(
                ShapeUtils.orUnoptimized(Block.box(0, 0, 0, 16, 8, 16), Block.box(0, 8, 0, 16, 16, 8)),
                cache.get(new SlopeEdgeShapes.ShapeKey(SlopeType.BOTTOM, true))
        );
        VoxelShape shapeHorizontal = ShapeUtils.orUnoptimized(
                VerticalStairsShapes.SHAPES.get(new VerticalStairsShapes.ShapeKey(Direction.NORTH, StairsType.VERTICAL)),
                cache.get(new SlopeEdgeShapes.ShapeKey(SlopeType.HORIZONTAL, true))
        );
        VoxelShape shapeTop = ShapeUtils.orUnoptimized(
                ShapeUtils.orUnoptimized(Block.box(0, 8, 0, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 8)),
                cache.get(new SlopeEdgeShapes.ShapeKey(SlopeType.TOP, true))
        );

        VoxelShape[] shapes = new VoxelShape[4 * 3];

        ShapeUtils.makeHorizontalRotations(shapeBottom, Direction.NORTH, shapes, 0);
        ShapeUtils.makeHorizontalRotations(shapeHorizontal, Direction.NORTH, shapes, SlopeType.HORIZONTAL.ordinal() << 2);
        ShapeUtils.makeHorizontalRotations(shapeTop, Direction.NORTH, shapes, SlopeType.TOP.ordinal() << 2);

        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        for (BlockState state : states) {
            SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            int idx = (type.ordinal() << 2) + dir.get2DDataValue();
            map.put(state, shapes[idx]);
        }

        return ShapeContainer.of(map);
    }
}
