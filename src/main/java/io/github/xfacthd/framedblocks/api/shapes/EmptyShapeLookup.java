package io.github.xfacthd.framedblocks.api.shapes;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

final class EmptyShapeLookup implements ShapeLookup {
    static final EmptyShapeLookup INSTANCE = new EmptyShapeLookup();

    @Override
    public VoxelShape getShape(BlockState state) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state) {
        return Shapes.empty();
    }

    @Override
    public boolean hasSeparateOcclusionShapes() {
        return false;
    }

    @Override
    public boolean occludesBeaconBeam(BlockState state) {
        return false;
    }
}
