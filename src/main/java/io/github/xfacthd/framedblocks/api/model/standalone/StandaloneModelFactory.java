package io.github.xfacthd.framedblocks.api.model.standalone;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

/// Functional interface for creating a standalong model of arbitrary type from a map of blockstate models.
///
/// @see StandaloneWrapperKey
@FunctionalInterface
public interface StandaloneModelFactory<T> {
    /// {@return a standalone model backed by the given blockstate models}
    ///
    /// @param models The blockstate models to use
    T create(Map<BlockState, BlockStateModel> models);
}
