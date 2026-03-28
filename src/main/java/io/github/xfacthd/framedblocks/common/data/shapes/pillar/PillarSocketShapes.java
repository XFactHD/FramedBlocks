package io.github.xfacthd.framedblocks.common.data.shapes.pillar;

import io.github.xfacthd.framedblocks.api.shapes.CommonShapes;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class PillarSocketShapes {
    private static final ShapeCache<Direction> SHAPES = ShapeCache.createEnum(Direction.class, PillarSocketShapes::createShapes);

    public static ShapeContainer generate(List<BlockState> states) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());
        for (BlockState state : states) {
            Direction dir = state.getValue(BlockStateProperties.FACING);
            map.put(state, SHAPES.get(dir));
        }
        return ShapeContainer.of(map);
    }

    private static void createShapes(Map<Direction, VoxelShape> map) {
        VoxelShape shapeDown = ShapeUtils.orUnoptimized(
                CommonShapes.SLAB.get(Boolean.FALSE),
                Block.box(4, 8, 4, 12, 16, 12)
        );
        VoxelShape shapeUp = ShapeUtils.rotateShapeAroundX(Direction.DOWN, Direction.UP, shapeDown);
        VoxelShape shapeNorth = ShapeUtils.rotateShapeUnoptimizedAroundX(Direction.DOWN, Direction.NORTH, shapeDown);

        map.put(Direction.DOWN, ShapeUtils.optimize(shapeDown));
        map.put(Direction.UP, shapeUp);
        ShapeUtils.makeHorizontalRotations(shapeNorth, Direction.NORTH, map);
    }

    private PillarSocketShapes() { }
}
