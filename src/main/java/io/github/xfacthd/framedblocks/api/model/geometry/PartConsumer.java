package io.github.xfacthd.framedblocks.api.model.geometry;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

/// Consumes parts of a source model, post-processes them as needed and appends them to the framed block's list of parts.
@ApiStatus.NonExtendable
public interface PartConsumer {
    /// Add the given part to the framed block's list of parts.
    ///
    /// @param part            The part to add
    /// @param state           The blockstate the part is from
    /// @param includeNull     Whether faces returned for `null` face should be included
    /// @param reclaimFromNull Whether cullable quads should be recovered from uncullable quads
    /// @param cullNonNull     Whether cullable quads should be culled according to the occlusion settings this consumer was constructed with
    /// @param shaderState     The blockstate to use as the "shader state" of the resulting model parts
    /// @param modifier        An optional modifier to post-process the quads with after filtering and quad recovery
    void accept(
            BlockStateModelPart part,
            BlockState state,
            boolean includeNull,
            boolean reclaimFromNull,
            boolean cullNonNull,
            @Nullable BlockState shaderState,
            @Nullable QuadListModifier modifier
    );

    /// Add all parts of the given model to the framed block's list of parts.
    ///
    /// @param model           The model whose model parts to add
    /// @param level           The level to provide to the model for part collection
    /// @param pos             The position to provide to the model for part collection
    /// @param random          The RNG to provide to the model for part collection
    /// @param state           The blockstate to provide to the model for part collection
    /// @param includeNull     Whether faces returned for `null` cullface should be included
    /// @param reclaimFromNull Whether cullable quads should be recovered from uncullable quads
    /// @param cullNonNull     Whether cullable quads should be culled according to the occlusion settings this consumer was constructed with
    /// @param shaderState     The blockstate to use as the "shader state" of the resulting model parts
    /// @param modifier        An optional modifier to post-process the quads with after filtering and quad recovery
    void acceptAll(
            BlockStateModel model,
            BlockAndTintGetter level,
            BlockPos pos,
            RandomSource random,
            BlockState state,
            boolean includeNull,
            boolean reclaimFromNull,
            boolean cullNonNull,
            @Nullable BlockState shaderState,
            @Nullable QuadListModifier modifier
    );
}
