package io.github.xfacthd.framedblocks.api.model.geometry;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.NonExtendable
public interface PartConsumer
{
    /**
     * @param part            The {@link BlockStateModelPart} to add
     * @param state           The {@link BlockState} the part is from (required for {@link ChunkSectionLayer} lookup)
     * @param includeNull     Whether faces returned for {@code null} face should be included
     * @param reclaimFromNull Whether cullable faces should be recovered from uncullable quads
     * @param cullNonNull     Whether cullable faces should be culled according to the occlusion settings this {@code PartConsumer} was constructed with
     * @param shaderState     The {@link BlockState} to use as the "shader state" of the resulting model parts
     * @param modifier        An optional modifier to pre-process the quads with after filtering and quad recovery
     */
    void accept(
            BlockStateModelPart part,
            BlockState state,
            boolean includeNull,
            boolean reclaimFromNull,
            boolean cullNonNull,
            @Nullable BlockState shaderState,
            @Nullable QuadListModifier modifier
    );

    /**
     * @param model           The model whose {@link BlockStateModelPart}s to add
     * @param level           The {@link BlockAndTintGetter} to provide to the model for part collection
     * @param pos             The {@link BlockPos} to provide to the model for part collection
     * @param random          The {@link RandomSource} to provide to the model for part collection
     * @param state           The {@link BlockState} the part is from (required for part and {@link ChunkSectionLayer} lookup)
     * @param includeNull     Whether faces returned for {@code null} face should be included
     * @param reclaimFromNull Whether cullable faces should be recovered from uncullable quads
     * @param cullNonNull     Whether cullable faces should be culled according to the occlusion settings this {@code PartConsumer} was constructed with
     * @param shaderState     The {@link BlockState} to use as the "shader state" of the resulting model parts
     * @param modifier        An optional modifier to pre-process the quads with after filtering and quad recovery
     */
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
