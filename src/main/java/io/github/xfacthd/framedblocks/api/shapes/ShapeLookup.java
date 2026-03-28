package io.github.xfacthd.framedblocks.api.shapes;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public sealed interface ShapeLookup permits EmptyShapeLookup, SingleShapeLookup, MapBackedShapeLookup, ReloadableShapeLookup {
    ShapeLookup EMPTY = EmptyShapeLookup.INSTANCE;

    VoxelShape getShape(BlockState state);

    VoxelShape getOcclusionShape(BlockState state);

    boolean hasSeparateOcclusionShapes();

    boolean occludesBeaconBeam(BlockState state);

    static <T extends Block & IFramedBlock> ShapeLookup of(T owner) {
        ShapeGenerator generator = owner.getBlockType().getShapeGenerator();
        if (generator == ShapeGenerator.EMPTY) {
            return EMPTY;
        }
        return ReloadableShapeLookup.of(generator, owner.getStateDefinition().getPossibleStates());
    }
}
