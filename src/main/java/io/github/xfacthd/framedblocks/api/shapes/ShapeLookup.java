package io.github.xfacthd.framedblocks.api.shapes;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

/// Provides the shapes of a framed block, optionally including separate occlusion shapes, and indicates whether
/// a given state of the block occludes a beacon beam.
public sealed interface ShapeLookup permits SingleShapeLookup, MapBackedShapeLookup, ReloadableShapeLookup {
    /// The singleton empty shape lookup.
    ShapeLookup EMPTY = new SingleShapeLookup();

    /// {@return the shape for the given state}
    ///
    /// @param state The blockstate to get the shape for
    VoxelShape getShape(BlockState state);

    /// {@return the occlusion shape for the given state}
    ///
    /// @param state The blockstate to get the occlusion shape for
    VoxelShape getOcclusionShape(BlockState state);

    /// {@return whether the block has separate primary and occlusion shapes}
    boolean hasSeparateOcclusionShapes();

    /// {@return whether the shape of the given state occludes a beacon beam}
    ///
    /// @param state The blockstate to check
    boolean occludesBeaconBeam(BlockState state);

    /// {@return a shape lookup for the given block}
    ///
    /// @param owner The block to create the shape lookup for
    static <T extends Block & IFramedBlock> ShapeLookup of(T owner) {
        ShapeGenerator generator = owner.getBlockType().getShapeGenerator();
        if (generator == ShapeGenerator.EMPTY) {
            return EMPTY;
        }
        return ReloadableShapeLookup.of(generator, owner.getStateDefinition().getPossibleStates());
    }
}
