package io.github.xfacthd.framedblocks.api.model.wrapping;

import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

/// Functional interface for creating geometries in a given context.
@FunctionalInterface
public interface GeometryFactory {
    /// {@return the geometry for the given context}
    ///
    /// @param ctx The context in which the geometry is being instantiated
    Geometry create(Context ctx);

    /// Provides the context for instantiation of geometries.
    ///
    /// @param state          The blockstate for which the geometry is being instantiated
    /// @param baseModel      The original model of the blockstate
    /// @param auxModels      The auxiliary models specified in the blockstate file
    /// @param materialLookup The material lookup to use for resolving materials from the block atlas
    record Context(BlockState state, BlockStateModel baseModel, AuxModelProvider auxModels, MaterialLookup materialLookup) {
        @ApiStatus.Internal
        public Context {}

        /// {@return a copy of this context with its blockstate replaced by the given state}
        ///
        /// @param newState The replacement blockstate
        public Context withState(BlockState newState) {
            return new Context(newState, baseModel, auxModels, materialLookup);
        }
    }
}
