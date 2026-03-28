package io.github.xfacthd.framedblocks.api.shapes;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

final class SingleShapeLookup implements ShapeLookup {
    private final VoxelShape shape;
    private final VoxelShape occlusionShape;
    private final boolean occludesBeaconBeam;

    SingleShapeLookup(SingleShapeContainer shapes, @Nullable SingleShapeContainer occlusionShapes) {
        this.shape = shapes.getShape();
        this.occlusionShape = occlusionShapes != null ? occlusionShapes.getShape() : shape;
        this.occludesBeaconBeam = ShapeUtils.occludesBeaconBeam(shape);
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        return shape;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state) {
        return occlusionShape;
    }

    @Override
    public boolean hasSeparateOcclusionShapes() {
        return shape != occlusionShape;
    }

    @Override
    public boolean occludesBeaconBeam(BlockState state) {
        return occludesBeaconBeam;
    }
}
