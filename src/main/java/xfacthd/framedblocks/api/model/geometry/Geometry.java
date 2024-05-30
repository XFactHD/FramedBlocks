package xfacthd.framedblocks.api.model.geometry;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.extensions.IBakedModelExtension;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;
import xfacthd.framedblocks.api.FramedBlocksClientAPI;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.camo.*;
import xfacthd.framedblocks.api.model.cache.QuadCacheKey;
import xfacthd.framedblocks.api.model.cache.SimpleQuadCacheKey;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import xfacthd.framedblocks.api.util.ConfigView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class Geometry
{
    /**
     * Called for each {@link BakedQuad} of the camo block's model for whose side this block's
     * {@link FullFacePredicate#test(BlockState, Direction)} returns {@code false}.
     * @param quadMap The target map to put all final quads into
     * @param quad The source quad. Must not be modified directly, use {@link QuadModifier}s to
     *             modify the quad
     * @param data The {@link ModelData}
     */
    public void transformQuad(QuadMap quadMap, BakedQuad quad, ModelData data)
    {
        transformQuad(quadMap, quad);
    }

    /**
     * Called for each {@link BakedQuad} of the camo block's model for whose side this block's
     * {@link FullFacePredicate#test(BlockState, Direction)} returns {@code false}.
     * @param quadMap The target map to put all final quads into
     * @param quad The source quad. Must not be modified directly, use {@link QuadModifier}s to
     *             modify the quad
     */
    public abstract void transformQuad(QuadMap quadMap, BakedQuad quad);

    /**
     * {@return whether this geometry needs to perform post-processing on uncached quads}
     */
    public boolean hasUncachedPostProcessing()
    {
        return false;
    }

    /**
     * Post-process quads on faces that return {@code false} from {@link FullFacePredicate#test(BlockState, Direction)}<br>
     * Any additional processing done in this method should be as fast as possible and must happen in-place
     * @apiNote Only called if {@link #hasUncachedPostProcessing()} returns {@code true}
     */
    public void postProcessUncachedQuads(List<BakedQuad> quads) { }

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
     * {@return the {@link BakedModel} to use as the base model when no camo is applied}
     * @apiNote Only called if {@link #useBaseModel()} returns {@code true}
     */
    public BakedModel getBaseModel(BakedModel baseModel, boolean useAltModel)
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
     * Return {@link RenderType}s which contain additional quads (i.e. non-camo quads read from other models)
     * or {@link ChunkRenderTypeSet#none()} when no additional render types are present
     */
    public ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        return ChunkRenderTypeSet.none();
    }

    /**
     * Add additional quads to faces that return {@code true} from {@link FullFacePredicate#test(BlockState, Direction)}<br>
     * The result of this method will NOT be cached, execution should therefore be as fast as possible
     */
    public void getAdditionalQuads(ArrayList<BakedQuad> quads, Direction side, RandomSource rand, ModelData data, RenderType renderType) { }

    /**
     * Add additional quads to faces that return {@code false} from {@link FullFacePredicate#test(BlockState, Direction)}<br>
     * The result of this method will be cached, processing time is therefore not critical
     */
    public void getAdditionalQuads(QuadMap quadMap, RandomSource rand, ModelData data, RenderType renderType) { }

    /**
     * Return {@link RenderType}s which contain overlay quads generated in {@link #getGeneratedOverlayQuads(QuadMap, RandomSource, ModelData, RenderType)}
     * or {@link ChunkRenderTypeSet#none()} when no overlay quads are used
     */
    public ChunkRenderTypeSet getOverlayRenderTypes(RandomSource rand, ModelData extraData)
    {
        return ChunkRenderTypeSet.none();
    }

    /**
     * Add additional generated quads based on the full set of previously generated quads to avoid z-fighting with the
     * other quads below the overlay on faces that return {@code false} from {@link FullFacePredicate#test(BlockState, Direction)}
     *
     * @param quadMap The {@link QuadMap} containing all transformed quads
     * @param layer The {@link RenderType} for which overlay quads are being requested
     * @see FramedBlocksClientAPI#generateOverlayQuads(QuadMap, Direction, TextureAtlasSprite)
     * @see FramedBlocksClientAPI#generateOverlayQuads(QuadMap, Direction, TextureAtlasSprite, Predicate)
     */
    public void getGeneratedOverlayQuads(QuadMap quadMap, RandomSource rand, ModelData data, RenderType layer) { }

    /**
     * Return a custom {@link QuadCacheKey} that holds additional metadata which influences the resulting quads.
     * @implNote The resulting object must at least store the given {@link BlockState} and connected textures context object
     * and should either be a record or have an otherwise properly implemented {@code hashCode()} and {@code equals()}
     * implementation
     * @param camo The {@link CamoContent} of the camo applied to the block
     * @param ctCtx The current connected textures context object, may be null
     * @param data The {@link ModelData} from the {@link FramedBlockEntity}
     */
    public QuadCacheKey makeCacheKey(CamoContent<?> camo, Object ctCtx, ModelData data)
    {
        // Avoid allocating a key if the CT context is null
        return ctCtx != null ? new SimpleQuadCacheKey(camo, ctCtx) : camo;
    }

    /**
     * Apply transformations to the item model when it is rendered in hand
     */
    public void applyInHandTransformation(PoseStack poseStack, ItemDisplayContext ctx) { }

    /**
     * {@return whether the model should use a solid model when no camo is applied}
     * @apiNote Only has an effect if {@link #useBaseModel()} returns {@code false}
     */
    public boolean useSolidNoCamoModel()
    {
        return false;
    }

    /**
     * Controls the AO behavior for all quads of this model.
     * @see IBakedModelExtension#useAmbientOcclusion(BlockState, ModelData, RenderType)
     */
    public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType)
    {
        FramedBlockData fbData = data.get(FramedBlockData.PROPERTY);
        CamoContent<?> camoContent;
        if (fbData != null && !(camoContent = fbData.getCamoContent()).isEmpty())
        {
            BakedModel model = CamoContainerHelper.Client.getOrCreateModel(camoContent);
            TriState camoAO = model.useAmbientOcclusion(camoContent.getAppearanceState(), ModelData.EMPTY, renderType);
            if (camoAO != TriState.DEFAULT)
            {
                return camoAO;
            }
            if (camoContent.getLightEmission() != 0 || camoContent.isEmissive())
            {
                return TriState.FALSE;
            }
        }
        if (ConfigView.Client.INSTANCE.shouldForceAmbientOcclusionOnGlowingBlocks())
        {
            return TriState.TRUE;
        }
        return TriState.DEFAULT;
    }
}
