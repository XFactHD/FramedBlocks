package xfacthd.framedblocks.client.model;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.ApiStatus;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.block.cache.StateCache;
import xfacthd.framedblocks.api.model.cache.QuadCacheKey;
import xfacthd.framedblocks.api.model.data.*;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.util.ModelCache;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.api.predicate.contex.ConTexMode;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.TestProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.data.ConTexDataHandler;
import xfacthd.framedblocks.common.config.ClientConfig;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.*;

@SuppressWarnings("deprecation")
public final class FramedBlockModel extends BakedModelProxy
{
    private static final boolean DISABLE_QUAD_CACHE = !FMLEnvironment.production && TestProperties.DISABLE_MODEL_QUAD_CACHE;
    private static final FramedBlockData DEFAULT_DATA = new FramedBlockData.Immutable(
            Blocks.AIR.defaultBlockState(), new boolean[6], false
    );
    private static final ChunkRenderTypeSet BASE_MODEL_RENDER_TYPES = ModelUtils.CUTOUT;
    public static final ResourceLocation REINFORCEMENT_LOCATION = Utils.rl("block/framed_reinforcement");
    private static BakedModel reinforcementModel = null;

    private final Cache<QuadCacheKey, QuadTable> quadCache = Utils.makeLRUCache(ModelCache.DEFAULT_CACHE_DURATION);
    private final Cache<QuadCacheKey, CachedRenderTypes> renderTypeCache = Utils.makeLRUCache(ModelCache.DEFAULT_CACHE_DURATION);
    private final BlockState state;
    private final Geometry geometry;
    private final IBlockType type;
    private final boolean isBaseCube;
    private final boolean cacheFullRenderTypes;
    private final boolean forceUngeneratedBaseModel;
    private final boolean useBaseModel;
    private final boolean transformAllQuads;
    private final boolean useSolidBase;
    private final StateCache stateCache;

    public FramedBlockModel(GeometryFactory.Context ctx, Geometry geometry)
    {
        super(ctx.baseModel());
        this.state = ctx.state();
        this.geometry = geometry;
        this.type = ((IFramedBlock) state.getBlock()).getBlockType();
        this.isBaseCube = state.getBlock() == FBContent.BLOCK_FRAMED_CUBE.value();
        this.cacheFullRenderTypes = geometry.canFullyCacheRenderTypes();
        this.forceUngeneratedBaseModel = geometry.forceUngeneratedBaseModel();
        this.useBaseModel = geometry.useBaseModel();
        this.transformAllQuads = geometry.transformAllQuads();
        this.useSolidBase = geometry.useSolidNoCamoModel();
        this.stateCache = ((IFramedBlock) state.getBlock()).getCache(state);

        Preconditions.checkState(
                this.useBaseModel || !this.forceUngeneratedBaseModel,
                "FramedBlockModel::useBaseModel() must return true when FramedBlockModel::forceUngeneratedBaseModel() returns true"
        );
    }

    @Override
    public List<BakedQuad> getQuads(
            BlockState state, Direction side, RandomSource rand, ModelData extraData, RenderType renderType
    )
    {
        BlockState camoState = Blocks.AIR.defaultBlockState();

        if (state == null)
        {
            state = this.state;
        }

        FramedBlockData data = extraData.get(FramedBlockData.PROPERTY);
        if (data != null && renderType != null)
        {
            if (side != null && data.isSideHidden(side))
            {
                return Collections.emptyList();
            }

            camoState = data.getCamoState();
            if (camoState != null && !camoState.isAir())
            {
                return getCamoQuads(state, camoState, side, rand, extraData, data, renderType);
            }
        }

        if (data == null)
        {
            data = DEFAULT_DATA;
        }
        if (renderType == null)
        {
            renderType = RenderType.cutout();
        }
        if (camoState == null || camoState.isAir())
        {
            return getCamoQuads(state, null, side, rand, extraData, data, renderType);
        }

        return Collections.emptyList();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand)
    {
        if (state == null)
        {
            state = this.state;
        }
        return getCamoQuads(state, null, side, rand, ModelData.EMPTY, DEFAULT_DATA, RenderType.cutout());
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data)
    {
        FramedBlockData fbData = data.get(FramedBlockData.PROPERTY);
        if (isBaseCube && (fbData == null || fbData.getCamoState().isAir()))
        {
            return baseModel.getRenderTypes(state, rand, data);
        }
        if (fbData == null)
        {
            fbData = DEFAULT_DATA;
        }

        BlockState camoState = fbData.getCamoState();
        BlockState keyState = camoState;
        if (camoState == null || camoState.isAir())
        {
            camoState = Blocks.AIR.defaultBlockState();
            keyState = getNoCamoModelState(FramedBlocksAPI.INSTANCE.getDefaultModelState(), fbData);
        }

        CachedRenderTypes cachedTypes = getCachedRenderTypes(keyState, camoState, rand, data);
        if (cacheFullRenderTypes)
        {
            return cachedTypes.allTypes;
        }

        ChunkRenderTypeSet renderTypes = cachedTypes.camoTypes;
        ChunkRenderTypeSet additionalTypes = geometry.getAdditionalRenderTypes(rand, data);
        ChunkRenderTypeSet overlayTypes = geometry.getOverlayRenderTypes(rand, data);
        if (!additionalTypes.isEmpty() || !overlayTypes.isEmpty())
        {
            renderTypes = ChunkRenderTypeSet.union(renderTypes, additionalTypes, overlayTypes);
        }
        return renderTypes;
    }

    private CachedRenderTypes getCachedRenderTypes(
            BlockState keyState, BlockState camoState, RandomSource rand, ModelData data
    )
    {
        return renderTypeCache.get(
                geometry.makeCacheKey(keyState, null, data),
                key -> buildRenderTypeCache(camoState, rand, data)
        );
    }

    private CachedRenderTypes buildRenderTypeCache(BlockState camoState, RandomSource rand, ModelData data)
    {
        ChunkRenderTypeSet camoTypes = BASE_MODEL_RENDER_TYPES;
        if (!camoState.isAir())
        {
            camoTypes = ChunkRenderTypeSet.union(
                ModelCache.getRenderTypes(camoState, rand, ModelData.EMPTY),
                ModelCache.getCamoRenderTypes(camoState, rand, data)
            );
        }
        ChunkRenderTypeSet additionalTypes = ChunkRenderTypeSet.none();
        ChunkRenderTypeSet overlayTypes = ChunkRenderTypeSet.none();
        if (cacheFullRenderTypes)
        {
            additionalTypes = geometry.getAdditionalRenderTypes(rand, data);
            overlayTypes = geometry.getOverlayRenderTypes(rand, data);
        }
        return new CachedRenderTypes(camoTypes, additionalTypes, overlayTypes);
    }

    private List<BakedQuad> getCamoQuads(
            BlockState state,
            BlockState camoState,
            Direction side,
            RandomSource rand,
            ModelData extraData,
            FramedBlockData fbData,
            RenderType renderType
    )
    {
        ModelData camoData;
        BakedModel model;
        boolean noProcessing;
        boolean noCamo = camoState == null;
        boolean needCtCtx;
        boolean camoInRenderType;
        boolean addReinforcement;

        if (noCamo)
        {
            needCtCtx = false;
            camoState = getNoCamoModelState(FramedBlocksAPI.INSTANCE.getDefaultModelState(), fbData);
            addReinforcement = useBaseModel && fbData.isReinforced();
            camoInRenderType = BASE_MODEL_RENDER_TYPES.contains(renderType);
            noProcessing = (camoInRenderType && forceUngeneratedBaseModel) || stateCache.isFullFace(side);
            model = getCamoModel(camoState, useBaseModel);
            camoData = ModelData.EMPTY;
        }
        else
        {
            noProcessing = stateCache.isFullFace(side);
            needCtCtx = type.supportsConnectedTextures() && needCtContext(noProcessing, type.getMinimumConTexMode());
            model = getCamoModel(camoState, false);
            camoData = needCtCtx ? ModelUtils.getCamoModelData(extraData) : ModelData.EMPTY;
            camoInRenderType = getCachedRenderTypes(camoState, camoState, rand, camoData).camoTypes.contains(renderType);
            addReinforcement = false;
        }

        if (noProcessing)
        {
            ChunkRenderTypeSet addLayers = geometry.getAdditionalRenderTypes(rand, extraData);
            boolean additionalQuads = addLayers.contains(renderType);
            if (!camoInRenderType && !additionalQuads && !addReinforcement)
            {
                return List.of();
            }

            ArrayList<BakedQuad> quads = new ArrayList<>();

            if (camoInRenderType)
            {
                Utils.copyAll(model.getQuads(camoState, side, rand, camoData, renderType), quads);
            }

            if (additionalQuads)
            {
                geometry.getAdditionalQuads(quads, side, state, rand, extraData, renderType);
            }

            if (addReinforcement && renderType == RenderType.cutout())
            {
                Utils.copyAll(reinforcementModel.getQuads(camoState, side, rand, camoData, renderType), quads);
            }

            return geometry.postProcessUncachedQuads(quads);
        }
        else
        {
            Object ctCtx = needCtCtx ? ConTexDataHandler.extractConTexData(camoData) : null;
            if (DISABLE_QUAD_CACHE)
            {
                return buildQuadCache(state, camoState, rand, extraData, ctCtx != null ? camoData : ModelData.EMPTY, noCamo, addReinforcement)
                        .getQuads(renderType, side);
            }
            return quadCache.get(
                    geometry.makeCacheKey(camoState, ctCtx, extraData),
                    key -> buildQuadCache(state, key.state(), rand, extraData, ctCtx != null ? camoData : ModelData.EMPTY, noCamo, addReinforcement)
            ).getQuads(renderType, side);
        }
    }

    private static boolean needCtContext(boolean noProcessing, ConTexMode minMode)
    {
        ConTexMode mode = ClientConfig.VIEW.getConTexMode();
        if (mode == ConTexMode.NONE)
        {
            return false;
        }
        return noProcessing || (mode.atleast(ConTexMode.FULL_EDGE) && mode.atleast(minMode));
    }

    /**
     * Builds a {@link RenderType} -> {@link Direction} -> {@link List<BakedQuad>} table with all render types used by this model
     */
    private QuadTable buildQuadCache(
            BlockState state,
            BlockState camoState,
            RandomSource rand,
            ModelData data,
            ModelData camoData,
            boolean noCamo,
            boolean addReinforcement
    )
    {
        QuadTable quadTable = new QuadTable();

        ChunkRenderTypeSet modelLayers = getRenderTypes(state, rand, data);
        ChunkRenderTypeSet camoLayers = BASE_MODEL_RENDER_TYPES;
        if (!noCamo)
        {
            camoLayers = ModelCache.getRenderTypes(camoState, rand, camoData);
        }
        else
        {
            // Make sure the RenderType set being iterated actually contains the no-camo layers in case getQuads()
            // was called with a null RenderType while a camo is provided (i.e. block breaking overlay)
            modelLayers = ChunkRenderTypeSet.union(modelLayers, camoLayers);
        }

        BakedModel camoModel = getCamoModel(camoState, noCamo && useBaseModel);

        for (RenderType renderType : modelLayers)
        {
            boolean camoInRenderType = camoLayers.contains(renderType);

            makeQuadsForLayer(
                    quadTable,
                    state,
                    camoState,
                    camoModel,
                    rand,
                    data,
                    camoData,
                    renderType,
                    camoInRenderType,
                    addReinforcement && renderType == RenderType.cutout()
            );
        }
        for (RenderType renderType : geometry.getOverlayRenderTypes(rand, data))
        {
            quadTable.initializeForLayer(renderType);
            geometry.getGeneratedOverlayQuads(quadTable, rand, data, renderType);
        }
        quadTable.bindRenderType(null);

        return quadTable;
    }

    /**
     * Builds the list of quads per side for a given {@linkplain BlockState camo state} and {@link RenderType}
     */
    private void makeQuadsForLayer(
            QuadTable quadMap,
            BlockState state,
            BlockState camoState,
            BakedModel camoModel,
            RandomSource rand,
            ModelData data,
            ModelData camoData,
            RenderType renderType,
            boolean camoInRenderType,
            boolean addReinforcement
    )
    {
        quadMap.initializeForLayer(renderType);

        if (camoInRenderType)
        {
            ArrayList<BakedQuad> quads = ModelUtils.getAllCullableQuads(camoModel, camoState, rand, camoData, renderType);
            if (addReinforcement)
            {
                Utils.copyAll(ModelUtils.getAllCullableQuads(reinforcementModel, camoState, rand, camoData, renderType), quads);
            }
            if (!transformAllQuads)
            {
                quads.removeIf(q -> stateCache.isFullFace(q.getDirection()));
            }

            for (BakedQuad quad : quads)
            {
                geometry.transformQuad(quadMap, quad, data);
            }
        }

        ChunkRenderTypeSet addLayers = geometry.getAdditionalRenderTypes(rand, data);
        if (addLayers.contains(renderType))
        {
            geometry.getAdditionalQuads(quadMap, state, rand, data, renderType);
        }
    }

    @ApiStatus.Internal
    private BlockState getNoCamoModelState(BlockState camoState, FramedBlockData fbData)
    {
        if (isBaseCube)
        {
            camoState = state;
        }
        if (fbData.useAltModel())
        {
            camoState = camoState.setValue(PropertyHolder.ALT, true);
        }
        if (fbData.isReinforced())
        {
            camoState = camoState.setValue(PropertyHolder.REINFORCED, true);
        }
        if (ClientConfig.VIEW.getSolidFrameMode().useSolidFrame(useSolidBase))
        {
            camoState = camoState.setValue(PropertyHolder.SOLID_BG, true);
        }
        return camoState;
    }

    /**
     * Return the {@link BakedModel} to use as the camo model for the given camoState
     *
     * @param camoState The {@link BlockState} used as camo
     * @param useBaseModel If true, the {@link BakedModelProxy#baseModel} is requested instead of the model of the given state
     *
     * @apiNote Most models shouldn't need to override this. If the model loaded from JSON should be used when no camo
     * is applied, return true from {@link Geometry#useBaseModel()}. If the model loaded from JSON should be
     * used without applying any quad modifications when no camo is applied, return true from
     * {@link Geometry#forceUngeneratedBaseModel()} as well
     */
    private BakedModel getCamoModel(BlockState camoState, boolean useBaseModel)
    {
        if (useBaseModel)
        {
            return baseModel;
        }
        return ModelCache.getModel(camoState);
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData tileData)
    {
        if (!type.supportsConnectedTextures())
        {
            return tileData;
        }

        FramedBlockData data = tileData.get(FramedBlockData.PROPERTY);
        if (data == null)
        {
            return tileData;
        }

        BlockState camoState = data.getCamoState();
        if (!camoState.isAir() && needCtContext(stateCache.hasAnyFullFace(), type.getMinimumConTexMode()))
        {
            BakedModel model = ModelCache.getModel(camoState);
            ModelData camoData;
            try
            {
                // Try getting camo data with the enclosing state, some mods may not like that
                // This option provides better CT behaviour
                camoData = model.getModelData(level, pos, state, tileData);
            }
            catch (Throwable t)
            {
                // Fall back to getting camo data with the camo state if a mod didn't like it
                // This option may cause some CT weirdness
                camoData = model.getModelData(level, pos, camoState, tileData);
            }
            tileData = tileData.derive().with(FramedBlockData.CAMO_DATA, camoData).build();
        }
        return tileData;
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data)
    {
        FramedBlockData fbdata = data.get(FramedBlockData.PROPERTY);
        if (fbdata != null)
        {
            BlockState camoState = fbdata.getCamoState();
            if (!camoState.isAir())
            {
                return getCamoModel(camoState, false).getParticleIcon();
            }
        }
        return baseModel.getParticleIcon();
    }

    @Override
    protected void applyInHandTransformation(PoseStack poseStack, ItemDisplayContext ctx)
    {
        geometry.applyInHandTransformation(poseStack, ctx);
    }

    public void clearCache()
    {
        quadCache.invalidateAll();
        renderTypeCache.invalidateAll();
    }



    public static void captureReinforcementModel(Map<ResourceLocation, BakedModel> models)
    {
        reinforcementModel = models.get(REINFORCEMENT_LOCATION);
    }



    private record CachedRenderTypes(
            ChunkRenderTypeSet camoTypes,
            ChunkRenderTypeSet additionalTypes,
            ChunkRenderTypeSet overlayTypes,
            ChunkRenderTypeSet allTypes
    )
    {
        public CachedRenderTypes(ChunkRenderTypeSet camoTypes, ChunkRenderTypeSet additionalTypes, ChunkRenderTypeSet overlayTypes)
        {
            this(camoTypes, additionalTypes, overlayTypes, ChunkRenderTypeSet.union(camoTypes, additionalTypes, overlayTypes));
        }
    }
}