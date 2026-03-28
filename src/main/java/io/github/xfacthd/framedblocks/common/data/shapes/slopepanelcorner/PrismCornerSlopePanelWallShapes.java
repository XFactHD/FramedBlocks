package io.github.xfacthd.framedblocks.common.data.shapes.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class PrismCornerSlopePanelWallShapes implements ShapeGenerator {
    public static final PrismCornerSlopePanelWallShapes SMALL_OUTER = new PrismCornerSlopePanelWallShapes(
            PrismCornerSlopePanelShapes.PrismCornerShape.SMALL_OUTER
    );
    public static final PrismCornerSlopePanelWallShapes LARGE_OUTER = new PrismCornerSlopePanelWallShapes(
            PrismCornerSlopePanelShapes.PrismCornerShape.LARGE_OUTER
    );
    public static final PrismCornerSlopePanelWallShapes SMALL_INNER = new PrismCornerSlopePanelWallShapes(
            PrismCornerSlopePanelShapes.PrismCornerShape.SMALL_INNER
    );
    public static final PrismCornerSlopePanelWallShapes LARGE_INNER = new PrismCornerSlopePanelWallShapes(
            PrismCornerSlopePanelShapes.PrismCornerShape.LARGE_INNER
    );
    private static final HorizontalRotation[] ROTATIONS = HorizontalRotation.values();

    private final PrismCornerSlopePanelShapes.PrismCornerShape cornerShape;

    private PrismCornerSlopePanelWallShapes(PrismCornerSlopePanelShapes.PrismCornerShape cornerShape) {
        this.cornerShape = cornerShape;
    }

    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        return generate(states, PrismCornerSlopePanelShapes.SHAPES);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states) {
        return generate(states, PrismCornerSlopePanelShapes.OCCLUSION_SHAPES);
    }

    private ShapeContainer generate(List<BlockState> states, ShapeCache<PrismCornerSlopePanelShapes.PrismCornerShape> cache) {
        VoxelShape baseShape = cache.get(cornerShape);
        if (baseShape.isEmpty()) {
            return ShapeContainer.EMPTY;
        }

        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        VoxelShape[] shapes = new VoxelShape[16];
        shapes[0] = ShapeUtils.rotateShapeUnoptimizedAroundX(Direction.UP, Direction.SOUTH, baseShape);
        for (HorizontalRotation rot : ROTATIONS) {
            shapes[rot.ordinal() * 4] = ShapeUtils.rotateShapeAroundZ(Direction.UP, rot.withFacing(Direction.NORTH), shapes[0]);
        }
        for (int i = 0; i < 4; i++) {
            int baseIdx = i * 4;
            ShapeUtils.makeHorizontalRotations(shapes[baseIdx], Direction.NORTH, shapes, baseIdx);
        }

        for (BlockState state : states) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            map.put(state, shapes[rot.ordinal() << 2 | dir.get2DDataValue()]);
        }

        return ShapeContainer.of(map);
    }
}
