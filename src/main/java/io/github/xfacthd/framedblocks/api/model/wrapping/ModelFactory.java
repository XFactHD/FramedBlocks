package io.github.xfacthd.framedblocks.api.model.wrapping;

import io.github.xfacthd.framedblocks.api.model.AbstractUnbakedFramedBlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

/// Functional interface for creating unbaked blockstate models in a given context.
@FunctionalInterface
public interface ModelFactory {
    /// {@return the unbaked blockstate model for the given context}
    ///
    /// @param ctx The context in which the model is being instantiated
    AbstractUnbakedFramedBlockStateModel create(ModelFactory.Context ctx);

    /// Called at the start of model wrapping to reset any internal state of this factory.
    default void reset() { }

    /// Provides the context for instantiation of unbaked blockstate models.
    ///
    /// @param state     The blockstate for which the model is being instantiated
    /// @param baseModel The original model of the blockstate
    /// @param auxModels The auxiliary models specified in the blockstate file
    record Context(BlockState state, BlockStateModel.UnbakedRoot baseModel, Map<String, SingleVariant.Unbaked> auxModels) {
        @ApiStatus.Internal
        public Context {}
    }
}
