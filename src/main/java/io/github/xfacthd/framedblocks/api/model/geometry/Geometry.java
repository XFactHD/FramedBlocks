package io.github.xfacthd.framedblocks.api.model.geometry;

import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.camo.CamoContent;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.item.ItemModelInfo;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.api.util.ConfigView;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.BlockModelPartExtension;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("MethodMayBeStatic")
public abstract class Geometry
{
    /**
     * Called for each {@link BakedQuad} of the camo block's model for whose side this block's
     * {@link FullFacePredicate#test(BlockState, Direction)} returns {@code false} or all quads
     * if {@link #transformAllQuads()} returns {@code true}.
     *
     * @param quadMap          The target map to put all final quads into
     * @param quad             The source quad. Must not be modified directly, use {@link QuadModifier}s to modify the quad
     * @param blockData        The {@link FramedBlockData} holding the block's camo and related metadata
     * @param cacheKeyUserData The additional user data, if available, from the cache key used to cache the transformed quads
     */
    public abstract void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object cacheKeyUserData);

    /**
     * Return true if the base model loaded from JSON should be used when no camo is applied without going
     * through the quad manipulation process
     */
    public boolean forceUngeneratedBaseModel()
    {
        return false;
    }

    /**
     * Return true if the base model loaded from JSON should be used instead of the Framed Cube model
     * when no camo is applied. Quad manipulation will still be done if
     * {@link Geometry#forceUngeneratedBaseModel()} returns false
     * @apiNote Must return true if {@link Geometry#forceUngeneratedBaseModel()} returns true
     */
    public boolean useBaseModel()
    {
        return forceUngeneratedBaseModel();
    }

    /**
     * {@return the {@link BlockStateModel} to use as the base model when no camo is applied}
     * @apiNote Only called if {@link #useBaseModel()} returns {@code true}
     */
    public BlockStateModel getBaseModel(BlockStateModel baseModel, boolean useAltModel)
    {
        return baseModel;
    }

    /**
     * Return true if all quads should be submitted for transformation, even if their cull-face would be filtered
     * by the {@link FullFacePredicate}
     */
    public boolean transformAllQuads()
    {
        return false;
    }

    /**
     * Return true if this geometry may add additional parts in {@link #collectAdditionalPartsUncached(PartConsumer, BlockAndTintGetter, BlockPos, RandomSource, ModelData)}.
     */
    public boolean hasAdditionalUncachedParts()
    {
        return false;
    }

    /**
     * Add additional {@link BlockModelPart}s which should not be cached.
     * The result of this method will NOT be cached, execution should therefore be as fast as possible.
     * Only called if {@link #hasAdditionalUncachedParts()} returns true.
     *
     * @param consumer The {@link PartConsumer} to pass the additional parts to
     * @param level    The {@linkplain BlockAndTintGetter level} the block is being rendered in
     * @param pos      The {@link BlockPos} the block is being rendered at
     * @param random   The {@link RandomSource} to use for randomization
     * @param data     The {@link ModelData} from the {@link IFramedBlockEntity}
     */
    public void collectAdditionalPartsUncached(PartConsumer consumer, BlockAndTintGetter level, BlockPos pos, RandomSource random, ModelData data) { }

    /**
     * Add additional {@link BlockModelPart}s which should be cached.
     * The result of this method will be cached, processing time is therefore not critical
     *
     * @param consumer         The {@link PartConsumer} to pass the additional parts to
     * @param level            The {@linkplain BlockAndTintGetter level} the block is being rendered in
     * @param pos              The {@link BlockPos} the block is being rendered at
     * @param random           The {@link RandomSource} to use for randomization
     * @param blockData        The {@link FramedBlockData} holding the block's camo and related metadata
     * @param cacheKeyUserData The additional user data, if available, from the cache key used to cache the generated geometry
     */
    public void collectAdditionalPartsCached(PartConsumer consumer, BlockAndTintGetter level, BlockPos pos, RandomSource random, FramedBlockData blockData, @Nullable Object cacheKeyUserData) { }

    /**
     * Return whether this geometry needs to generate additional overlay quads.
     *
     * @param blockData        The {@link FramedBlockData} holding the block's camo and related metadata
     * @param cacheKeyUserData The additional user data, if available, from the cache key used to cache the generated geometry
     */
    public boolean hasGeneratedOverlay(FramedBlockData blockData, @Nullable Object cacheKeyUserData)
    {
        return false;
    }

    /**
     * Add additional generated quads based on the full set of previously generated quads to avoid z-fighting with the
     * other quads below the overlay on faces that return {@code false} from {@link FullFacePredicate#test(BlockState, Direction)}.
     * <p>
     * Only called if {@link #hasGeneratedOverlay(FramedBlockData, Object)} returns true.
     *
     * @param generator        The {@link OverlayPartGenerator} used to generate the overlay parts
     * @param cacheKeyUserData The additional user data, if available, from the cache key used to cache the generated geometry
     */
    public void generateOverlayParts(OverlayPartGenerator generator, RandomSource rand, @Nullable Object cacheKeyUserData) { }

    /**
     * Compute additional data to be included in the cache key which influences the resulting quads.
     *
     * @param level      The {@linkplain BlockAndTintGetter level} the block is being rendered in
     * @param pos        The {@link BlockPos} the block is being rendered at
     * @param random     The {@link RandomSource} to use for randomization
     * @param data       The {@link ModelData} from the {@link IFramedBlockEntity}
     */
    @Nullable
    public Object computeCacheKeyUserData(BlockAndTintGetter level, BlockPos pos, RandomSource random, ModelData data)
    {
        return null;
    }

    /**
     * {@return whether the model should use a solid model when no camo is applied}
     * @apiNote Only has an effect if {@link #useBaseModel()} returns {@code false}
     */
    public boolean useSolidNoCamoModel()
    {
        return false;
    }

    /**
     * Compute the default AO behavior which is used unless the source {@link BlockModelPart} specifies something else
     * @see BlockModelPartExtension#ambientOcclusion()
     */
    public DefaultAO computeDefaultAmbientOcclusion(BlockState state, ModelData data)
    {
        FramedBlockData fbData = AbstractFramedBlockData.getOrDefault(data, state, null);
        if (fbData != null)
        {
            if (fbData.isEmissive())
            {
                return DefaultAO.FORCE_DISABLE;
            }

            CamoContent<?> camoContent = fbData.getCamoContent();
            if (!camoContent.isEmpty() && (camoContent.getLightEmission() != 0 || camoContent.isEmissive()))
            {
                return DefaultAO.DISABLE;
            }
        }
        if (ConfigView.Client.INSTANCE.shouldForceAmbientOcclusionOnGlowingBlocks())
        {
            return DefaultAO.ENABLE;
        }
        return DefaultAO.DEFAULT;
    }

    /**
     * {@return the {@link ItemModelInfo} to use for controlling item model geometry caching}
     */
    public ItemModelInfo getItemModelInfo()
    {
        return ItemModelInfo.DEFAULT;
    }
}
