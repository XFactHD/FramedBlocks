package io.github.xfacthd.framedblocks.client.model.baked;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.block.IBlockType;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.cache.StateCache;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerHelper;
import io.github.xfacthd.framedblocks.api.camo.CamoContent;
import io.github.xfacthd.framedblocks.api.camo.block.BlockCamoContent;
import io.github.xfacthd.framedblocks.api.model.AbstractFramedBlockStateModel;
import io.github.xfacthd.framedblocks.api.model.CachingModel;
import io.github.xfacthd.framedblocks.api.model.ExtendedBlockStateModelPart;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.ModelDataEntry;
import io.github.xfacthd.framedblocks.api.model.geometry.DefaultAO;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.geometry.QuadListModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.DelegateBlockStateModelPart;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConTexMode;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.client.model.ReinforcementModel;
import io.github.xfacthd.framedblocks.client.model.overlaygen.BlockOverlayGenerator;
import io.github.xfacthd.framedblocks.client.model.overlaygen.OverlayModelPartGenerator;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.Optionull;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class FramedBlockStateModel extends AbstractFramedBlockStateModel implements CachingModel {
    private static final FramedBlockData DEFAULT_DATA = FramedBlockData.EMPTY;
    private static final int FLAG_NO_CAMO_ALT_MODEL = 0b001;
    private static final int FLAG_NO_CAMO_REINFORCED = 0b010;
    private static final int FLAG_NO_CAMO_SOLID_BG = 0b100;
    private static final BlockCamoContent[] NO_CAMO_CONTENTS = makeNoCamoContents();
    private static final BlockStateModel[] NO_CAMO_MODELS = new BlockStateModel[NO_CAMO_CONTENTS.length];

    private final Map<Object, PartCacheEntry> partCache = new ConcurrentHashMap<>();
    private final Geometry geometry;
    private final boolean supportsCt;
    private final ConTexMode minCtMode;
    private final boolean forceUngeneratedBaseModel;
    private final boolean useBaseModel;
    private final boolean useSolidBase;
    private final boolean uncachedAdditionalParts;
    private final boolean useCamoStateForCamoModelQueries;
    private final StateCache stateCache;
    private final ReinforcementModel reinforcement;

    public FramedBlockStateModel(GeometryFactory.Context ctx, Geometry geometry, ReinforcementModel reinforcement, boolean standaloneWithCt) {
        super(ctx.baseModel(), ctx.state());
        this.geometry = geometry;
        IBlockType type = ((IFramedBlock) state.getBlock()).getBlockType();
        this.supportsCt = type.supportsConnectedTextures() || standaloneWithCt;
        this.minCtMode = standaloneWithCt ? ConTexMode.FULL_FACE : type.getMinimumConTexMode();
        this.forceUngeneratedBaseModel = geometry.forceUngeneratedBaseModel();
        this.useBaseModel = geometry.useBaseModel();
        this.useSolidBase = geometry.useSolidNoCamoModel();
        this.uncachedAdditionalParts = geometry.hasAdditionalUncachedParts();
        this.useCamoStateForCamoModelQueries = standaloneWithCt;
        this.stateCache = state.framedblocks$getCache();
        this.reinforcement = reinforcement;

        Preconditions.checkState(
                this.useBaseModel || !this.forceUngeneratedBaseModel,
                "Geometry::useBaseModel() must return true when Geometry::forceUngeneratedBaseModel() returns true"
        );
        Preconditions.checkState(
                !this.useSolidBase || !this.useBaseModel,
                "Geometry#useSolidNoCamoModel() and Geometry#useBaseModel() cannot both return true"
        );

        CachingModel.register(this);
    }

    @Override
    @SuppressWarnings("ForLoopReplaceableByForEach")
    public int collectParts(BlockAndTintGetter level, BlockPos pos, BlockState unusedState, RandomSource random, List<BlockStateModelPart> partsOut, int miscTintOffset) {
        BlockState state = this.state;
        ModelData extraData = level.getModelData(pos);
        AbstractFramedBlockData blockData = Objects.requireNonNullElse(extraData.get(AbstractFramedBlockData.PROPERTY), DEFAULT_DATA);
        FramedBlockData partData = blockData.unwrap(state);
        CamoContent<?> camoContent = partData.getCamoContent();

        boolean empty = camoContent.isEmpty();
        BlockStateModel camoModel;
        int camoTintOffset;
        int overlayTintOffset;
        int localMiscTintOffset;
        ConTexMode cfgCtMode = ConTexMode.NONE;
        boolean mayUseCt = !empty && supportsCt && (cfgCtMode = ClientConfig.VIEW.getConTexMode()) != ConTexMode.NONE;
        boolean reinforce = empty && useBaseModel && partData.isReinforced();
        DefaultAO defaultAO = geometry.computeDefaultAmbientOcclusion(partData, extraData);
        boolean camoEmissive;
        boolean forceEmissive = partData.isEmissive();
        BlockOverlay blockOverlay = partData.getBlockOverlay();
        long seed = state.getSeed(pos);

        if (empty) {
            int noCamoIdx = getNoCamoModelSourceIndex(partData);
            camoContent = NO_CAMO_CONTENTS[noCamoIdx];
            camoModel = useBaseModel ? geometry.getBaseModel(delegate, partData.isSecondPart()) : NO_CAMO_MODELS[noCamoIdx];
            camoTintOffset = 0;
            overlayTintOffset = 0;
            camoEmissive = false;
        } else {
            camoModel = CamoContainerHelper.Client.getOrCreateModel(camoContent);
            camoTintOffset = blockData.getCamoTintIndexOffset(partData.isSecondPart());
            overlayTintOffset = blockData.getPostCamoTintIndexOffset();
            camoEmissive = camoContent.isEmissive();
        }
        localMiscTintOffset = overlayTintOffset;

        PartConsumerImpl partConsumer = null;
        int uncachedFaceMask = partData.computeFaceMask(stateCache, false);
        if (uncachedFaceMask != 0 || uncachedAdditionalParts) {
            partConsumer = new PartConsumerImpl(partsOut, uncachedFaceMask, defaultAO, camoEmissive, forceEmissive);
        }
        int prevOutSize = partsOut.size();
        if (uncachedFaceMask != 0) {
            random.setSeed(seed);
            partConsumer.setTintIndexOffset(camoTintOffset);
            collectCamoParts(partConsumer, camoModel, level, pos, random, partData, camoContent, true, null, mayUseCt);
        }
        if (blockOverlay != null) {
            if (partsOut.size() > prevOutSize) {
                BlockOverlayGenerator.generateUncached(state, blockOverlay, partsOut.subList(prevOutSize, partsOut.size()), partsOut, forceEmissive, overlayTintOffset);
            }
            if (blockOverlay.tintSource() != null) {
                localMiscTintOffset++;
            }
        }
        localMiscTintOffset = Math.max(localMiscTintOffset, miscTintOffset);
        if (!empty || !forceUngeneratedBaseModel) {
            random.setSeed(seed);
            Object ctCtx;
            if (mayUseCt && cfgCtMode.atleast(ConTexMode.FULL_EDGE) && cfgCtMode.atleast(minCtMode)) {
                BlockState geoKeyState = useCamoStateForCamoModelQueries ? camoContent.getAppearanceState() : state;
                ctCtx = camoModel.createGeometryKey(level, pos, geoKeyState, random);
            } else {
                ctCtx = null;
            }
            random.setSeed(seed);
            Object userKeyData = geometry.computeCacheKeyUserData(level, pos, random, extraData);
            // Remove CT context from key if the context is the source model itself and therefore a "dumb" model
            Object key = createCacheKey(partData, camoContent, ctCtx == camoModel ? null : ctCtx, userKeyData, camoTintOffset, overlayTintOffset, localMiscTintOffset);
            PartCacheEntry cacheEntry = partCache.get(key);
            if (cacheEntry == null) {
                try {
                    cacheEntry = buildPartCache(
                            camoModel,
                            level,
                            pos,
                            random,
                            seed,
                            partData,
                            camoContent,
                            userKeyData,
                            reinforce,
                            camoEmissive,
                            defaultAO,
                            ctCtx != null,
                            camoTintOffset,
                            overlayTintOffset,
                            localMiscTintOffset
                    );
                } catch (Throwable t) {
                    throw new RuntimeException(String.format(
                            Locale.ROOT,
                            "Encountered an unexpected error while computing cached model parts for %s on %s",
                            partData.getCamoContainer(),
                            state
                    ), t);
                }
                partCache.put(key, cacheEntry);
            }
            List<ExtendedBlockStateModelPart> cachedParts = cacheEntry.parts;
            if (!cachedParts.isEmpty()) {
                int cachedFaceMask = partData.computeFaceMask(stateCache, true);
                for (int i = 0; i < cachedParts.size(); i++) {
                    partsOut.add(new CullableBlockStateModelPart(cachedParts.get(i), cachedFaceMask));
                }
            }
            localMiscTintOffset = cacheEntry.tintOffset;
        }
        if (uncachedAdditionalParts) {
            random.setSeed(seed);
            partConsumer.setTintIndexOffset(localMiscTintOffset);
            partConsumer.setCountTintIndices(true);
            geometry.collectAdditionalPartsUncached(partConsumer, level, pos, random, partData, extraData);
            localMiscTintOffset = partConsumer.getMaxTintIndex() + 1;
            partConsumer.setCountTintIndices(false);
        }
        if (reinforce && uncachedFaceMask != 0) {
            BlockStateModelPart reinforcementPart = reinforcement.getFiltered(uncachedFaceMask, defaultAO.apply(TriState.DEFAULT));
            partConsumer.accept(reinforcementPart, ReinforcementModel.SHADER_STATE, false, false, true, ReinforcementModel.SHADER_STATE, null);
        }
        return localMiscTintOffset;
    }

    static boolean isSideHidden(int cullMask, @Nullable Direction side) {
        return side != null && (cullMask & (1 << side.ordinal())) == 0;
    }

    private static Object createCacheKey(
            FramedBlockData fbData,
            CamoContent<?> camo,
            @Nullable Object ctCtx,
            @Nullable Object userKeyData,
            int camoTintOffset,
            int overlayTintOffset,
            int miscTintOffset
    ) {
        BlockOverlay overlay = fbData.getBlockOverlay();
        Object queryData = Optionull.map(fbData.getQueryData(), ModelDataEntry::data);
        boolean secondPart = fbData.isSecondPart();
        boolean emissive = fbData.isEmissive();
        if (overlay != null || ctCtx != null || queryData != null || userKeyData != null || (secondPart && (camoTintOffset > 0 || miscTintOffset > 0)) || emissive) {
            BlockState outerState = overlay != null ? fbData.getOuterState() : null;
            // Assume that neither camos nor arbitrary geometry are stupid enough to need so many tint "layers" that any of the offsets are > 255
            int packedTintOffsets = (camoTintOffset & 0xFF) | ((overlayTintOffset & 0xFF) << 8) | ((miscTintOffset & 0xFF) << 16);
            return new CompoundPartCacheKey(outerState, camo, overlay, ctCtx, queryData, secondPart, emissive, packedTintOffsets, userKeyData);
        }
        // Avoid allocating a wrapping key object if possible
        return camo;
    }

    private PartCacheEntry buildPartCache(
            BlockStateModel camoModel,
            BlockAndTintGetter level,
            BlockPos pos,
            RandomSource random,
            long seed,
            FramedBlockData fbData,
            CamoContent<?> camo,
            @Nullable Object cacheKeyUserData,
            boolean reinforce,
            boolean camoEmissive,
            DefaultAO defaultAO,
            boolean supportDynamicCamoGeometry,
            int camoTintOffset,
            int overlayTintOffset,
            int miscTintOffset
    ) {
        ObjectList<ExtendedBlockStateModelPart> parts = new ObjectArrayList<>();
        boolean forceEmissive = fbData.isEmissive();
        boolean secondPart = fbData.isSecondPart();
        int cullMask = DEFAULT_DATA.computeFaceMask(stateCache, true);
        PartConsumerImpl partConsumer = new PartConsumerImpl(parts, cullMask, defaultAO, camoEmissive, forceEmissive);
        boolean xformAll = geometry.transformAllQuads();

        QuadListModifier modifier = (quadMap, quads, side) -> {
            for (BakedQuad quad : quads) {
                if (quad.direction() != side) {
                    // Discard quads whose normal dir does not match their cull-face, they will crash downstream quad modifiers and
                    // are usually inward facing and therefore unusable.
                    continue;
                }
                geometry.transformQuad(quadMap, quad, fbData, cacheKeyUserData);
            }
            quads.clear();
        };

        random.setSeed(seed);
        partConsumer.setTintIndexOffset(camoTintOffset);
        collectCamoParts(partConsumer, camoModel, level, pos, random, fbData, camo, !xformAll, modifier, supportDynamicCamoGeometry);
        if (reinforce) {
            BlockStateModelPart srcPart = reinforcement.getFiltered(xformAll ? 0b00111111 : cullMask, defaultAO.apply(TriState.DEFAULT));
            partConsumer.accept(srcPart, ReinforcementModel.SHADER_STATE, false, true, !xformAll, ReinforcementModel.SHADER_STATE, modifier);
        }
        BlockOverlay overlay = fbData.getBlockOverlay();
        if (!parts.isEmpty() && overlay != null) {
            BlockOverlayGenerator.generateCached(fbData.getOuterState(), state, secondPart, overlay, parts, forceEmissive, overlayTintOffset);
        }
        random.setSeed(seed);
        partConsumer.setTintIndexOffset(miscTintOffset);
        partConsumer.setCountTintIndices(true);
        geometry.collectAdditionalPartsCached(partConsumer, level, pos, random, fbData, cacheKeyUserData);
        int tintIndexOffset = partConsumer.getMaxTintIndex() + 1;
        partConsumer.setCountTintIndices(false);

        if (!parts.isEmpty() && geometry.hasGeneratedOverlay(fbData, cacheKeyUserData)) {
            OverlayModelPartGenerator overlayGenerator = new OverlayModelPartGenerator(parts, forceEmissive, defaultAO.apply(TriState.DEFAULT));
            random.setSeed(seed);
            geometry.generateOverlayParts(overlayGenerator, random, cacheKeyUserData);
            overlayGenerator.flush();
        }

        return new PartCacheEntry(parts, tintIndexOffset);
    }

    private void collectCamoParts(
            PartConsumerImpl partConsumer,
            BlockStateModel camoModel,
            BlockAndTintGetter level,
            BlockPos pos,
            RandomSource random,
            FramedBlockData partData,
            CamoContent<?> camo,
            boolean cullNonNull,
            @Nullable QuadListModifier modifier,
            boolean supportDynamicGeometry
    ) {
        if (!supportDynamicGeometry) {
            level = BlockAndTintGetter.EMPTY;
            pos = BlockPos.ZERO;
        }
        ModelDataEntry<?> queryData = partData.getQueryData();
        if (queryData != null) {
            level = new DataAppendingDelegateLevel(level, pos, queryData);
        }
        BlockState queryState = useCamoStateForCamoModelQueries ? camo.getAppearanceState() : state;
        partConsumer.acceptCamo(camoModel, level, pos, random, queryState, camo.getAsBlockState(), cullNonNull, modifier);
    }

    private int getNoCamoModelSourceIndex(FramedBlockData fbData) {
        int idx = 0;
        if (fbData.isSecondPart()) {
            idx |= FLAG_NO_CAMO_ALT_MODEL;
        }
        if (fbData.isReinforced()) {
            idx |= FLAG_NO_CAMO_REINFORCED;
        }
        if (ClientConfig.VIEW.getSolidFrameMode().useSolidFrame(useSolidBase)) {
            idx |= FLAG_NO_CAMO_SOLID_BG;
        }
        return idx;
    }

    private static BlockCamoContent[] makeNoCamoContents() {
        BlockCamoContent[] contents = new BlockCamoContent[1 << 3];
        for (int i = 0; i < contents.length; i++) {
            BlockState stateOut = FBContent.BLOCK_FRAMED_CUBE.value().defaultBlockState();
            if ((i & FLAG_NO_CAMO_ALT_MODEL) != 0) {
                stateOut = stateOut.setValue(PropertyHolder.ALT, true);
            }
            if ((i & FLAG_NO_CAMO_REINFORCED) != 0) {
                stateOut = stateOut.setValue(PropertyHolder.REINFORCED, true);
            }
            if ((i & FLAG_NO_CAMO_SOLID_BG) != 0) {
                stateOut = stateOut.setValue(PropertyHolder.SOLID_BG, true);
            }
            contents[i] = new BlockCamoContent(stateOut);
        }
        return contents;
    }

    @Override
    public int materialFlags(BlockAndTintGetter level, BlockPos pos, BlockState unusedState) {
        int flags = 0;
        ModelData modelData = level.getModelData(pos);
        FramedBlockData blockData = AbstractFramedBlockData.getOrDefault(modelData, state, DEFAULT_DATA);
        CamoContent<?> camoContent = blockData.getCamoContent();
        if (!camoContent.isEmpty()) {
            flags |= CamoContainerHelper.Client.getOrCreateModel(camoContent).materialFlags(level, pos, camoContent.getAsBlockState());
        } else if (useBaseModel) {
            flags |= geometry.getBaseModel(delegate, blockData.isSecondPart()).materialFlags(level, pos, state);
        }
        BlockOverlay overlay = blockData.getBlockOverlay();
        if (overlay != null && overlay.translucent()) {
            flags |= BakedQuad.FLAG_TRANSLUCENT;
        }
        return flags | geometry.getMaterialFlags(level, pos, modelData, blockData);
    }

    @Override
    public void clearCache() {
        partCache.clear();
    }

    public static void collectCubeBaseModels(Map<BlockState, BlockStateModel> models) {
        for (int i = 0; i < NO_CAMO_CONTENTS.length; i++) {
            BlockStateModel model = models.get(NO_CAMO_CONTENTS[i].getState());
            if (model instanceof AbstractFramedBlockStateModel framedModel) {
                model = framedModel.getBaseModel();
            }
            NO_CAMO_MODELS[i] = model;
        }
    }

    private record CompoundPartCacheKey(
            @Nullable BlockState outerState,
            CamoContent<?> camo,
            @Nullable BlockOverlay overlay,
            @Nullable Object ctContext,
            @Nullable Object queryData,
            boolean secondPart,
            boolean emissive,
            int packedTintOffsets,
            @Nullable Object userData
    ) { }

    private record CullableBlockStateModelPart(ExtendedBlockStateModelPart wrapped, int cullMask) implements DelegateBlockStateModelPart {
        @Override
        public List<BakedQuad> getQuads(@Nullable Direction side) {
            boolean hidden = (cullMask & (1 << DirUtils.maskNullDirection(side))) == 0;
            return hidden ? Collections.emptyList() : wrapped.getQuads(side);
        }
    }

    private record PartCacheEntry(List<ExtendedBlockStateModelPart> parts, int tintOffset) { }
}
