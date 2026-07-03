package io.github.xfacthd.framedblocks.api.model.geometry;

import io.github.xfacthd.framedblocks.api.camo.CamoContent;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.api.util.ConfigView;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.BlockStateModelPartExtension;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

/// Describes how the geometry of a framed block is built from the quads of the camo's model.
@SuppressWarnings("MethodMayBeStatic")
public abstract class Geometry {
    /// Called for each quad of the camo's model for whose side this block's [FullFacePredicate] returns `false`
    /// or all quads if [#transformAllQuads()] returns `true`.
    ///
    /// @param quadMap          The target map to put the final quads into
    /// @param quad             The source quad to be transformed with [QuadModifier]s
    /// @param blockData        The [FramedBlockData] holding the block's camo and related metadata
    /// @param cacheKeyUserData The additional user data, if available, from the cache key used to cache the transformed quads
    public abstract void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object cacheKeyUserData);

    /// {@return whether the base model loaded from JSON should be used when no camo is applied without going
    /// through the quad manipulation process}
    public boolean forceUngeneratedBaseModel() {
        return false;
    }

    /// {@return whether the base model loaded from JSON should be used instead of the Framed Cube model when no
    /// camo is applied}
    /// Quad manipulation will still be done if [#forceUngeneratedBaseModel()] returns false.
    ///
    /// @apiNote Must return true if [#forceUngeneratedBaseModel()] returns true
    public boolean useBaseModel() {
        return forceUngeneratedBaseModel();
    }

    /// {@return the {@link BlockStateModel} to use as the base model when no camo is applied}
    ///
    /// @apiNote Only called if [#useBaseModel()] returns `true`
    public BlockStateModel getBaseModel(BlockStateModel baseModel, boolean useAltModel) {
        return baseModel;
    }

    /// {@return whether all quads should be submitted for transformation, regardless of what the {@link FullFacePredicate} returns}
    public boolean transformAllQuads() {
        return false;
    }

    /// {@return true if this geometry may add additional parts in {@link #collectAdditionalPartsUncached(PartConsumer, BlockAndTintGetter, BlockPos, RandomSource, FramedBlockData, ModelData)}}
    public boolean hasAdditionalUncachedParts() {
        return false;
    }

    /// Add additional model parts which should not be cached.
    ///
    /// The result of this method will NOT be cached, execution should therefore be as fast as possible.
    /// Only called if [#hasAdditionalUncachedParts()] returns true.
    ///
    /// @param consumer  The part consumer to pass the additional parts to
    /// @param level     The level the block is being rendered in
    /// @param pos       The position the block is being rendered at
    /// @param random    The RNG to use for randomization
    /// @param blockData The block data holding the block's camo and related metadata
    /// @param data      The full model data from the BE
    public void collectAdditionalPartsUncached(PartConsumer consumer, BlockAndTintGetter level, BlockPos pos, RandomSource random, FramedBlockData blockData, ModelData data) { }

    /// Add additional [BlockStateModelPart]s which should be cached.
    ///
    /// The result of this method will be cached, processing time is therefore not critical
    /// Implementors of this method which pull geometry from another model (including the geometry's base model) should also override
    /// [#computeCacheKeyUserData(BlockAndTintGetter, BlockPos, RandomSource, ModelData)] and return the geometry key from the
    /// model being queried in this method to ensure things like randomized models are cached correctly.
    ///
    /// @param consumer         The part consumer to pass the additional parts to
    /// @param level            The level the block is being rendered in
    /// @param pos              The position the block is being rendered at
    /// @param random           The RNG to use for randomization
    /// @param blockData        The block data holding the block's camo and related metadata
    /// @param cacheKeyUserData The additional user data, if available, from the cache key used to cache the generated geometry
    public void collectAdditionalPartsCached(PartConsumer consumer, BlockAndTintGetter level, BlockPos pos, RandomSource random, FramedBlockData blockData, @Nullable Object cacheKeyUserData) { }

    /// {@return whether this geometry needs to generate additional overlay quads}
    ///
    /// @param blockData        The block data holding the block's camo and related metadata
    /// @param cacheKeyUserData The additional user data, if available, from the cache key used to cache the generated geometry
    public boolean hasGeneratedOverlay(FramedBlockData blockData, @Nullable Object cacheKeyUserData) {
        return false;
    }

    /// Add additional generated quads based on the full set of previously generated quads to avoid z-fighting with the
    /// other quads below the overlay on faces that return `false` from [FullFacePredicate#test(BlockState, Direction)].
    ///
    /// Only called if [#hasGeneratedOverlay(FramedBlockData, Object)] returns true.
    ///
    /// @param generator        The [OverlayPartGenerator] used to generate the overlay parts
    /// @param cacheKeyUserData The additional user data, if available, from the cache key used to cache the generated geometry
    public void generateOverlayParts(OverlayPartGenerator generator, RandomSource rand, @Nullable Object cacheKeyUserData) { }

    /// {@return additional data to be included in the cache key which influences the resulting quads}
    ///
    /// @param level  The level the block is being rendered in
    /// @param pos    The position the block is being rendered at
    /// @param random The RNG to use for randomization
    /// @param data   The full model data from the BE
    public @Nullable Object computeCacheKeyUserData(BlockAndTintGetter level, BlockPos pos, RandomSource random, ModelData data) {
        return null;
    }

    /// {@return whether the model should use a solid model when no camo is applied}
    ///
    /// @apiNote Only has an effect if [#useBaseModel()] returns `false`
    public boolean useSolidNoCamoModel() {
        return false;
    }

    /// {@return material flags from additional parts added by this geometry}
    ///
    /// @param level     The level this geometry is being rendered in
    /// @param pos       The position this geometry is being rendered at
    /// @param modelData The model data this geometry is being rendered with
    /// @param blockData The {@link FramedBlockData} holding the block's camo and related metadata
    public int getMaterialFlags(BlockAndTintGetter level, BlockPos pos, ModelData modelData, FramedBlockData blockData) {
        return 0;
    }

    /// Compute the default AO behavior which is used unless the source [BlockStateModelPart] specifies something else.
    ///
    /// @param blockData The primary block data
    /// @param data      The full model data
    /// @return the default AO behavior
    /// @see BlockStateModelPartExtension#ambientOcclusion()
    public DefaultAO computeDefaultAmbientOcclusion(FramedBlockData blockData, ModelData data) {
        if (blockData.isEmissive()) {
            return DefaultAO.FORCE_DISABLE;
        }
        CamoContent<?> camoContent = blockData.getCamoContent();
        if (!camoContent.isEmpty() && (camoContent.getLightEmission() != 0 || camoContent.isEmissive())) {
            return DefaultAO.DISABLE;
        }
        if (ConfigView.Client.INSTANCE.shouldForceAmbientOcclusionOnGlowingBlocks()) {
            return DefaultAO.ENABLE;
        }
        return DefaultAO.DEFAULT;
    }
}
