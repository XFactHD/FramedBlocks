package xfacthd.framedblocks.api.model;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Preconditions;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.CtmPredicate;
import xfacthd.framedblocks.api.util.FramedBlockData;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.client.*;

import javax.annotation.Nonnull;
import java.util.*;

@SuppressWarnings("deprecation")
public abstract class FramedBlockModel extends BakedModelProxy
{
    private final Cache<BlockState, QuadTable> quadCache = Caffeine.newBuilder()
            .expireAfterAccess(ModelCache.DEFAULT_CACHE_DURATION)
            .build();
    private final Cache<BlockState, CachedRenderTypes> renderTypeCache = Caffeine.newBuilder()
            .expireAfterAccess(ModelCache.DEFAULT_CACHE_DURATION)
            .build();
    private final BlockState state;
    private final IBlockType type;
    private final ChunkRenderTypeSet baseModelRenderTypes;
    private final boolean cacheFullRenderTypes;
    private final boolean forceUngeneratedBaseModel;
    private final boolean useBaseModel;
    private final boolean transformAllQuads;

    public FramedBlockModel(BlockState state, BakedModel baseModel)
    {
        super(baseModel);
        this.state = state;
        this.type = ((IFramedBlock) state.getBlock()).getBlockType();
        this.baseModelRenderTypes = getBaseModelRenderTypes();
        this.cacheFullRenderTypes = canFullyCacheRenderTypes();
        this.forceUngeneratedBaseModel = forceUngeneratedBaseModel();
        this.useBaseModel = useBaseModel();
        this.transformAllQuads = transformAllQuads(state);
        Preconditions.checkState(
                this.useBaseModel || !this.forceUngeneratedBaseModel,
                "FramedBlockModel::useBaseModel() must return true when FramedBlockModel::forceUngeneratedBaseModel() returns true"
        );
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData extraData, RenderType renderType)
    {
        BlockState camoState = Blocks.AIR.defaultBlockState();

        if (state == null) { state = this.state; }

        FramedBlockData data = extraData.get(FramedBlockData.PROPERTY);
        if (data != null && renderType != null)
        {
            if (side != null && data.isSideHidden(side)) { return Collections.emptyList(); }

            camoState = data.getCamoState();
            if (camoState != null && !camoState.isAir())
            {
                return getCamoQuads(state, camoState, side, rand, extraData, renderType);
            }
        }

        if (renderType == null) { renderType = RenderType.cutout(); }
        if (camoState == null || camoState.isAir())
        {
            return getCamoQuads(state, null, side, rand, extraData, renderType);
        }

        return Collections.emptyList();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand)
    {
        if (state == null) { state = this.state; }
        return getCamoQuads(state, null, side, rand, ModelData.EMPTY, RenderType.cutout());
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data)
    {
        BlockState camoState = Blocks.AIR.defaultBlockState();
        FramedBlockData fbData = data.get(FramedBlockData.PROPERTY);
        if (fbData != null)
        {
            camoState = fbData.getCamoState();
        }

        CachedRenderTypes cachedTypes = getCachedRenderTypes(camoState, rand, data);
        if (cacheFullRenderTypes)
        {
            return cachedTypes.allTypes;
        }

        ChunkRenderTypeSet renderTypes = cachedTypes.camoTypes;
        ChunkRenderTypeSet overlayTypes = getAdditionalRenderTypes(rand, data);
        if (!overlayTypes.isEmpty())
        {
            renderTypes = ChunkRenderTypeSet.union(renderTypes, overlayTypes);
        }
        return renderTypes;
    }

    private CachedRenderTypes getCachedRenderTypes(BlockState camoState, RandomSource rand, ModelData data)
    {
        return renderTypeCache.get(camoState, key -> buildRenderTypeCache(key, rand, data));
    }

    private CachedRenderTypes buildRenderTypeCache(BlockState camoState, RandomSource rand, ModelData data)
    {
        ChunkRenderTypeSet camoTypes = baseModelRenderTypes;
        if (!camoState.isAir())
        {
            camoTypes = ChunkRenderTypeSet.union(
                ModelCache.getRenderTypes(camoState, rand, ModelData.EMPTY),
                ModelCache.getCamoRenderTypes(camoState, rand, data)
            );
        }
        return new CachedRenderTypes(camoTypes, cacheFullRenderTypes ? getAdditionalRenderTypes(rand, data) : ChunkRenderTypeSet.none());
    }

    private List<BakedQuad> getCamoQuads(BlockState state, BlockState camoState, Direction side, RandomSource rand, ModelData extraData, RenderType renderType)
    {
        ModelData camoData = ModelData.EMPTY;
        BakedModel model;
        boolean noProcessing;
        boolean noCamo = camoState == null;
        boolean camoInRenderType;

        if (noCamo)
        {
            camoState = FramedBlocksAPI.getInstance().defaultModelState();
            camoInRenderType = baseModelRenderTypes.contains(renderType);
            noProcessing = (camoInRenderType && forceUngeneratedBaseModel) || type.getCtmPredicate().test(state, side);
            model = getCamoModel(camoState, useBaseModel);
        }
        else
        {
            noProcessing = type.getCtmPredicate().test(state, side);
            model = getCamoModel(camoState, false);
            camoData = noProcessing ? ModelUtils.getCamoModelData(model, camoState, extraData) : ModelData.EMPTY;
            camoInRenderType = getCachedRenderTypes(camoState, rand, camoData).camoTypes.contains(renderType);
        }

        if (noProcessing)
        {
            ChunkRenderTypeSet addLayers = getAdditionalRenderTypes(rand, extraData);
            boolean additionalQuads = addLayers.contains(renderType);
            if (!camoInRenderType && !additionalQuads) { return Collections.emptyList(); }

            List<BakedQuad> quads = new ArrayList<>();

            if (camoInRenderType)
            {
                quads.addAll(model.getQuads(camoState, side, rand, camoData, renderType));
            }

            if (additionalQuads)
            {
                getAdditionalQuads(quads, side, state, rand, extraData, renderType);
            }

            return quads;
        }
        else
        {
            ModelData finalCamoData = camoData;
            return quadCache.get(
                    camoState,
                    key -> buildQuadCache(state, key, rand, extraData, finalCamoData, noCamo)
            ).getQuads(renderType, side);
        }
    }

    /**
     * Builds a {@link RenderType} -> {@link Direction} -> {@link List<BakedQuad>} table with all render types used by this model
     */
    private QuadTable buildQuadCache(BlockState state, BlockState camoState, RandomSource rand, ModelData data, ModelData camoData, boolean noCamo)
    {
        QuadTable quadTable = new QuadTable();

        ChunkRenderTypeSet camoLayers = noCamo ? baseModelRenderTypes : ModelCache.getRenderTypes(camoState, rand, camoData);

        for (RenderType renderType : getRenderTypes(state, rand, data))
        {
            boolean camoInRenderType = camoLayers.contains(renderType);

            quadTable.put(renderType, makeQuads(
                    state,
                    camoState,
                    rand,
                    data,
                    renderType,
                    camoInRenderType,
                    noCamo
            ));
        }

        return quadTable;
    }

    /**
     * Builds the list of quads per side for a given {@linkplain BlockState camo state} and {@link RenderType}
     */
    private Map<Direction, List<BakedQuad>> makeQuads(BlockState state, BlockState camoState, RandomSource rand, ModelData data, RenderType renderType, boolean camoInRenderType, boolean noCamo)
    {
        Map<Direction, List<BakedQuad>> quadMap = new IdentityHashMap<>();
        quadMap.put(null, new ArrayList<>());
        for (Direction dir : Direction.values()) { quadMap.put(dir, new ArrayList<>()); }

        if (camoInRenderType)
        {
            BakedModel camoModel = getCamoModel(camoState, noCamo && useBaseModel);
            List<BakedQuad> quads = ModelUtils.getAllCullableQuads(camoModel, camoState, rand, renderType)
                            .stream()
                            .filter(q -> transformAllQuads || !type.getCtmPredicate().test(state, q.getDirection()))
                            .toList();

            for (BakedQuad quad : quads)
            {
                transformQuad(quadMap, quad);
            }
            postProcessQuads(quadMap);
        }

        ChunkRenderTypeSet addLayers = getAdditionalRenderTypes(rand, data);
        if (addLayers.contains(renderType))
        {
            getAdditionalQuads(quadMap, state, rand, data, renderType);
        }

        return quadMap;
    }

    /**
     * Called for each {@link BakedQuad} of the camo block's model for whose side this block's
     * {@link CtmPredicate#test(BlockState, Direction)} returns {@code false}.
     * @param quadMap The target map to put all final quads into
     * @param quad The source quad. Must not be modified, use {@link ModelUtils#duplicateQuad(BakedQuad)} to
     *             deep-copy the quad before manipulating it
     */
    protected abstract void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad);

    /**
     * Called after all quads have been piped through {@link FramedBlockModel#transformQuad(Map, BakedQuad)}
     * to apply bulk modifications to all quads, like transformation or rotation
     */
    protected void postProcessQuads(Map<Direction, List<BakedQuad>> quadMap) {}

    /**
     * Return true if the base model loaded from JSON should be used when no camo is applied without going
     * through the quad manipulation process
     */
    protected boolean forceUngeneratedBaseModel() { return false; }

    /**
     * Return true if the base model loaded from JSON should be used instead of the Framed Cube model
     * when no camo is applied. Quad manipulation will still be done if
     * {@link FramedBlockModel#forceUngeneratedBaseModel()} returns false
     * @apiNote Must return true if {@link FramedBlockModel#forceUngeneratedBaseModel()} returns true
     */
    protected boolean useBaseModel() { return forceUngeneratedBaseModel(); }

    /**
     * Return true if all quads should be submitted for transformation, even if their cull-face would be filtered
     * by the {@link CtmPredicate}
     */
    protected boolean transformAllQuads(BlockState state) { return false; }

    /**
     * Return the set of {@link RenderType}s used for the base model without camo.
     * @apiNote Must be available when the FramedBlockModel constructor is called
     */
    protected ChunkRenderTypeSet getBaseModelRenderTypes() { return ModelUtils.CUTOUT; }

    /**
     * Return true if the full set of {@link RenderType}s including overlay render types returned by
     * {@link FramedBlockModel#getAdditionalRenderTypes(RandomSource, ModelData)} are only dependent on the
     * {@link BlockState} associated with this model and/or the camo BlockState in the model data and can
     * therefore be cached based on the camo BlockState
     */
    protected boolean canFullyCacheRenderTypes() { return true; }

    /**
     * Return the {@link BakedModel} to use as the camo model for the given camoState
     *
     * @param camoState The {@link BlockState} used as camo
     * @param useBaseModel If true, the {@link BakedModelProxy#baseModel} is requested instead of the model of the given state
     *
     * @apiNote Most models shouldn't need to override this. If the model loaded from JSON should be used when no camo
     * is applied, return true from {@link FramedBlockModel#useBaseModel()}. If the model loaded from JSON should be
     * used without applying any quad modifications when no camo is applied, return true from
     * {@link FramedBlockModel#forceUngeneratedBaseModel()} as well
     */
    protected BakedModel getCamoModel(BlockState camoState, boolean useBaseModel)
    {
        if (useBaseModel)
        {
            return baseModel;
        }
        return ModelCache.getModel(camoState);
    }

    /**
     * Return {@link RenderType}s which contain additional quads (i.e. overlays) or {@link ChunkRenderTypeSet#none()} when no additional
     * render types are present
     */
    protected ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData) { return ChunkRenderTypeSet.none(); }

    /**
     * Add additional quads to faces that return {@code true} from {@code xfacthd.framedblocks.api.util.CtmPredicate#test(BlockState, Direction)}<br>
     * The result of this method will NOT be cached, execution should therefore be as fast as possible
     */
    protected void getAdditionalQuads(List<BakedQuad> quads, Direction side, BlockState state, RandomSource rand, ModelData data, RenderType renderType) {}

    /**
     * Add additional quads to faces that return {@code false} from {@code xfacthd.framedblocks.api.util.CtmPredicate#test(BlockState, Direction)}<br>
     * The result of this method will be cached, processing time is therefore not critical
     */
    protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, RandomSource rand, ModelData data, RenderType renderType) {}

    @Nonnull
    @Override
    public final ModelData getModelData(@Nonnull BlockAndTintGetter level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ModelData tileData)
    {
        FramedBlockData data = tileData.get(FramedBlockData.PROPERTY);
        if (data != null && !data.getCamoState().isAir())
        {
            BlockState camoState = data.getCamoState();
            BakedModel model = ModelCache.getModel(camoState);
            tileData = tileData.derive()
                    .with(FramedBlockData.CAMO_DATA, model.getModelData(level, pos, camoState, tileData))
                    .build();
        }

        //noinspection removal
        return tileData.derive()
                .with(FramedBlockData.LEVEL, level)
                .with(FramedBlockData.POS, pos)
                .build();
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



    private record CachedRenderTypes(ChunkRenderTypeSet camoTypes, ChunkRenderTypeSet overlayTypes, ChunkRenderTypeSet allTypes)
    {
        public CachedRenderTypes(ChunkRenderTypeSet camoTypes, ChunkRenderTypeSet overlayTypes)
        {
            this(camoTypes, overlayTypes, ChunkRenderTypeSet.union(camoTypes, overlayTypes));
        }
    }
}