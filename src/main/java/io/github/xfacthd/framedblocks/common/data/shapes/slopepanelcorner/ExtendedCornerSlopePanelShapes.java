package io.github.xfacthd.framedblocks.common.data.shapes.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import io.github.xfacthd.framedblocks.common.data.shapes.slopepanel.ExtendedSlopePanelShapes;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class ExtendedCornerSlopePanelShapes implements ShapeGenerator {
    public static final ExtendedCornerSlopePanelShapes OUTER = new ExtendedCornerSlopePanelShapes(BooleanOp.AND, Direction.NORTH);
    public static final ExtendedCornerSlopePanelShapes INNER = new ExtendedCornerSlopePanelShapes(BooleanOp.OR, Direction.SOUTH);

    private final BooleanOp joinOp;
    private final Direction srcDir;

    private ExtendedCornerSlopePanelShapes(BooleanOp joinOp, Direction srcDir) {
        this.joinOp = joinOp;
        this.srcDir = srcDir;
    }

    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        return generate(states, ExtendedSlopePanelShapes.SHAPES);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states) {
        return generate(states, ExtendedSlopePanelShapes.OCCLUSION_SHAPES);
    }

    private ShapeContainer generate(List<BlockState> states, ShapeCache<HorizontalRotation> cache) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        VoxelShape bottomSlopeShape = cache.get(HorizontalRotation.UP);
        VoxelShape bottomShape = Shapes.joinUnoptimized(
                bottomSlopeShape,
                ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.WEST, bottomSlopeShape),
                joinOp
        );

        VoxelShape topSlopeShape = cache.get(HorizontalRotation.DOWN);
        VoxelShape topShape = Shapes.joinUnoptimized(
                topSlopeShape,
                ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.WEST, topSlopeShape),
                joinOp
        );

        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(bottomShape, topShape, srcDir);

        for (BlockState state : states) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            map.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeContainer.of(map);
    }
}
