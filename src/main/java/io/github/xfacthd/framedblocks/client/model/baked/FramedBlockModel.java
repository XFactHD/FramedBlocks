package io.github.xfacthd.framedblocks.client.model.baked;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.block.IBlockType;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.cache.StateCache;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerHelper;
import io.github.xfacthd.framedblocks.api.camo.CamoContent;
import io.github.xfacthd.framedblocks.api.camo.block.BlockCamoContent;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import io.github.xfacthd.framedblocks.api.model.AbstractFramedBlockModel;
import io.github.xfacthd.framedblocks.api.model.ExtendedBlockModelPart;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.geometry.DefaultAO;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.geometry.PartConsumer;
import io.github.xfacthd.framedblocks.api.model.geometry.QuadListModifier;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.model.util.QuadUtils;
import io.github.xfacthd.framedblocks.api.model.wrapping.DelegateBlockModelPart;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConTexMode;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.model.QuadMapImpl;
import io.github.xfacthd.framedblocks.client.model.ReinforcementModel;
import io.github.xfacthd.framedblocks.client.model.overlaygen.OverlayModelPartGenerator;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

public final class FramedBlockModel extends AbstractFramedBlockModel
{
    private static final FramedBlockData DEFAULT_DATA = new FramedBlockData(EmptyCamoContainer.EMPTY, false);
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final @Nullable Direction[] DIRECTIONS_WITH_NULL = Arrays.copyOfRange(DIRECTIONS, 0, 7);
    private static final int FLAG_NO_CAMO_ALT_MODEL = 0b001;
    private static final int FLAG_NO_CAMO_REINFORCED = 0b010;
    private static final int FLAG_NO_CAMO_SOLID_BG = 0b100;
    private static final BlockCamoContent[] NO_CAMO_CONTENTS = makeNoCamoContents();
    private static final BlockStateModel[] NO_CAMO_MODELS = new BlockStateModel[NO_CAMO_CONTENTS.length];
    private static final UnaryOperator<BakedQuad> EMISSIVE_PROCESSOR = quad -> QuadUtils.setLightEmission(quad, 15);
    private static final UnaryOperator<BakedQuad> FULL_EMISSIVE_PROCESSOR = quad -> QuadUtils.setLightEmission(quad, 15, false, false);
    private static final UnaryOperator<BakedQuad> TINT_INDEX_INVERTER = QuadUtils::invertTintIndex;

    private final Map<Object, List<ExtendedBlockModelPart>> partCache = new ConcurrentHashMap<>();
    private final BlockState state;
    private final Geometry geometry;
    private final IBlockType type;
    private final boolean forceUngeneratedBaseModel;
    private final boolean useBaseModel;
    private final boolean useSolidBase;
    private final StateCache stateCache;
    private final ReinforcementModel reinforcement;

    public FramedBlockModel(GeometryFactory.Context ctx, Geometry geometry, ReinforcementModel reinforcement)
    {
        super(ctx.baseModel(), ctx.state(), geometry.getItemModelInfo());
        this.state = ctx.state();
        this.geometry = geometry;
        this.type = ((IFramedBlock) state.getBlock()).getBlockType();
        this.forceUngeneratedBaseModel = geometry.forceUngeneratedBaseModel();
        this.useBaseModel = geometry.useBaseModel();
        this.useSolidBase = geometry.useSolidNoCamoModel();
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
    }

    @Override
    @SuppressWarnings("ForLoopReplaceableByForEach")
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState unusedState, RandomSource random, List<BlockModelPart> partsOut)
    {
        ModelData extraData = level.getModelData(pos);
        FramedBlockData fbData = AbstractFramedBlockData.getOrDefault(extraData, this.state, DEFAULT_DATA);
        CamoContent<?> camoContent = fbData.getCamoContent();

        boolean empty = camoContent.isEmpty();
        BlockStateModel camoModel;
        boolean needCtCtxUncached = false;
        boolean needCtCtxCached = false;
        boolean reinforce = empty && useBaseModel && fbData.isReinforced();
        DefaultAO defaultAO = geometry.computeDefaultAmbientOcclusion(this.state, extraData);
        boolean camoEmissive;
        boolean forceEmissive = fbData.isEmissive();
        boolean secondPart = fbData.isSecondPart();
        long seed = state.getSeed(pos);

        if (empty)
        {
            int noCamoIdx = getNoCamoModelSourceIndex(fbData);
            camoContent = NO_CAMO_CONTENTS[noCamoIdx];
            camoModel = useBaseModel ? geometry.getBaseModel(delegate, secondPart) : NO_CAMO_MODELS[noCamoIdx];
            camoEmissive = false;
        }
        else
        {
            boolean canCt = type.supportsConnectedTextures();
            needCtCtxUncached = canCt && needCtContext(true, type.getMinimumConTexMode());
            needCtCtxCached = canCt && needCtContext(false, type.getMinimumConTexMode());
            camoModel = CamoContainerHelper.Client.getOrCreateModel(camoContent);
            camoEmissive = camoContent.isEmissive();
        }

        random.setSeed(seed);
        List<BlockModelPart> srcPartsUncached = ModelUtils.collectModelParts(camoModel, level, pos, this.state, random, needCtCtxUncached);
        int uncachedFaceMask = fbData.computeFaceMask(stateCache, false);
        PartConsumer partConsumer = makePartConsumer(partsOut, uncachedFaceMask, defaultAO, camoEmissive, forceEmissive, secondPart);

        BlockState camoState = camoContent.getAsBlockState();
        for (int i = 0; i < srcPartsUncached.size(); i++)
        {
            partConsumer.accept(srcPartsUncached.get(i), camoState, false, true, true, true, camoState, null);
        }
        if (!empty || !forceUngeneratedBaseModel)
        {
            random.setSeed(seed);
            Object ctCtx = needCtCtxCached ? camoModel.createGeometryKey(level, pos, this.state, random) : null;
            random.setSeed(seed);
            Object userKeyData = geometry.computeCacheKeyUserData(level, pos, random, extraData);
            Object key = createCacheKey(camoContent, ctCtx, secondPart, forceEmissive, userKeyData);
            List<ExtendedBlockModelPart> cachedParts = partCache.get(key);
            if (cachedParts == null)
            {
                random.setSeed(seed);
                List<BlockModelPart> srcParts = ModelUtils.collectModelParts(camoModel, level, pos, this.state, random, ctCtx != null);
                cachedParts = buildPartCache(srcParts, level, pos, random, seed, fbData, camoContent, userKeyData, reinforce, camoEmissive, forceEmissive, secondPart, defaultAO);
                partCache.put(key, cachedParts);
            }
            if (!cachedParts.isEmpty())
            {
                int cachedFaceMask = fbData.computeFaceMask(stateCache, true);
                for (int i = 0; i < cachedParts.size(); i++)
                {
                    partsOut.add(new CullableBlockModelPart(cachedParts.get(i), cachedFaceMask));
                }
            }
        }
        random.setSeed(seed);
        geometry.collectAdditionalPartsUncached(partConsumer, level, pos, random, extraData);
        if (reinforce)
        {
            BlockModelPart reinforcementPart = reinforcement.getFiltered(uncachedFaceMask, defaultAO.apply(TriState.DEFAULT));
            partConsumer.accept(reinforcementPart, ReinforcementModel.SHADER_STATE, false, false, true, false, ReinforcementModel.SHADER_STATE, null);
        }
    }

    private static PartConsumer makePartConsumer(
            List<? super ExtendedBlockModelPart> destParts,
            int cullMask,
            DefaultAO defaultAO,
            boolean camoEmissive,
            boolean forceEmissive,
            boolean invertTintIndex
    )
    {
        return (part, partState, includeNull, reclaimFromNull, cullNonNull, camoPart, shaderState, modifier) ->
        {
            Preconditions.checkArgument(!(includeNull && reclaimFromNull), "Cannot both include null faces and reclaim cullable faces from them");

            QuadMapImpl quadMap = new QuadMapImpl();
            boolean hasListModifier = modifier != null;
            boolean hasPostModifiers = (camoPart && camoEmissive) || forceEmissive || invertTintIndex;
            boolean hasAnyModifiers = hasListModifier || hasPostModifiers;
            boolean hasAnyQuads = false;
            for (Direction side : DIRECTIONS_WITH_NULL)
            {
                boolean nullSide = side == null;
                if (nullSide && !includeNull) continue;
                if (!nullSide && cullNonNull && isSideHidden(cullMask, side)) continue;

                List<BakedQuad> srcQuads = part.getQuads(side);
                if (!nullSide && srcQuads.isEmpty() && reclaimFromNull)
                {
                    srcQuads = ModelUtils.getFilteredNullQuads(part, side);
                }
                if (srcQuads.isEmpty())
                {
                    continue;
                }
                if (!hasAnyModifiers)
                {
                    quadMap.set(side, srcQuads);
                    hasAnyQuads = true;
                    continue;
                }
                if (hasListModifier)
                {
                    ArrayList<BakedQuad> quads = new ArrayList<>(srcQuads);
                    modifier.modify(quadMap, quads, side);
                    // Copy to final destination at the end in case the modifier wants to iterate or clear the list
                    Utils.copyAll(quads, quadMap.get(side));
                    hasAnyQuads |= !quadMap.isEmpty();
                }
                else
                {
                    Utils.copyAll(srcQuads, quadMap.get(side));
                    hasAnyQuads = true;
                }
            }
            if (!hasAnyQuads)
            {
                return;
            }
            if (hasPostModifiers)
            {
                for (Direction side : DIRECTIONS_WITH_NULL)
                {
                    ArrayList<BakedQuad> quads = quadMap.tryGet(side);
                    if (quads == null || quads.isEmpty()) continue;

                    if (forceEmissive)
                    {
                        quads.replaceAll(FULL_EMISSIVE_PROCESSOR);
                    }
                    else if (camoPart && camoEmissive)
                    {
                        quads.replaceAll(EMISSIVE_PROCESSOR);
                    }
                    if (invertTintIndex)
                    {
                        quads.replaceAll(TINT_INDEX_INVERTER);
                    }
                }
            }
            destParts.add(ModelUtils.makeModelPart(part, quadMap, partState, defaultAO, shaderState));
        };
    }

    private static boolean isSideHidden(int cullMask, @Nullable Direction side)
    {
        return side != null && (cullMask & (1 << side.ordinal())) == 0;
    }

    private static Object createCacheKey(CamoContent<?> camo, @Nullable Object ctCtx, boolean secondPart, boolean emissive, @Nullable Object userKeyData)
    {
        if (ctCtx != null || userKeyData != null || secondPart || emissive)
        {
            return new CompoundPartCacheKey(camo, ctCtx, secondPart, emissive, userKeyData);
        }
        // Avoid allocating a wrapping key object if possible
        return camo;
    }

    private List<ExtendedBlockModelPart> buildPartCache(
            List<BlockModelPart> srcParts,
            BlockAndTintGetter level,
            BlockPos pos,
            RandomSource random,
            long seed,
            FramedBlockData fbData,
            CamoContent<?> camo,
            @Nullable Object cacheKeyUserData,
            boolean reinforce,
            boolean camoEmissive,
            boolean forceEmissive,
            boolean secondPart,
            DefaultAO defaultAO
    )
    {
        ObjectList<ExtendedBlockModelPart> parts = new ObjectArrayList<>();
        int cullMask = DEFAULT_DATA.computeFaceMask(stateCache, true);
        PartConsumer partConsumer = makePartConsumer(parts, cullMask, defaultAO, camoEmissive, forceEmissive, secondPart);
        boolean xformAll = geometry.transformAllQuads();

        QuadListModifier modifier = (quadMap, quads, side) ->
        {
            for (BakedQuad quad : quads)
            {
                geometry.transformQuad(quadMap, quad, fbData, cacheKeyUserData);
            }
            quads.clear();
        };

        BlockState camoState = camo.getAsBlockState();
        for (BlockModelPart srcPart : srcParts)
        {
            partConsumer.accept(srcPart, camoState, false, true, !xformAll, true, camoState, modifier);
        }
        if (reinforce)
        {
            BlockModelPart srcPart = reinforcement.getFiltered(xformAll ? 0b00111111 : cullMask, defaultAO.apply(TriState.DEFAULT));
            partConsumer.accept(srcPart, ReinforcementModel.SHADER_STATE, false, true, !xformAll, false, ReinforcementModel.SHADER_STATE, modifier);
        }
        random.setSeed(seed);
        geometry.collectAdditionalPartsCached(partConsumer, level, pos, random, fbData, cacheKeyUserData);

        if (!parts.isEmpty() && geometry.hasGeneratedOverlay(fbData, cacheKeyUserData))
        {
            OverlayModelPartGenerator overlayGenerator = new OverlayModelPartGenerator(parts, defaultAO.apply(TriState.DEFAULT));
            random.setSeed(seed);
            geometry.generateOverlayParts(overlayGenerator, random, cacheKeyUserData);
            overlayGenerator.flush();
        }

        return parts;
    }

    private static boolean needCtContext(boolean mayHaveUncachedQuads, ConTexMode minMode)
    {
        ConTexMode cfgMode = ClientConfig.VIEW.getConTexMode();
        if (cfgMode == ConTexMode.NONE)
        {
            return false;
        }
        return mayHaveUncachedQuads || (cfgMode.atleast(ConTexMode.FULL_EDGE) && cfgMode.atleast(minMode));
    }

    private int getNoCamoModelSourceIndex(FramedBlockData fbData)
    {
        int idx = 0;
        if (fbData.isSecondPart()) idx |= FLAG_NO_CAMO_ALT_MODEL;
        if (fbData.isReinforced()) idx |= FLAG_NO_CAMO_REINFORCED;
        if (ClientConfig.VIEW.getSolidFrameMode().useSolidFrame(useSolidBase)) idx |= FLAG_NO_CAMO_SOLID_BG;
        return idx;
    }

    private static BlockCamoContent[] makeNoCamoContents()
    {
        BlockCamoContent[] contents = new BlockCamoContent[1 << 3];
        for (int i = 0; i < contents.length; i++)
        {
            BlockState stateOut = FBContent.BLOCK_FRAMED_CUBE.value().defaultBlockState();
            if ((i & FLAG_NO_CAMO_ALT_MODEL) != 0) stateOut = stateOut.setValue(PropertyHolder.ALT, true);
            if ((i & FLAG_NO_CAMO_REINFORCED) != 0) stateOut = stateOut.setValue(PropertyHolder.REINFORCED, true);
            if ((i & FLAG_NO_CAMO_SOLID_BG) != 0) stateOut = stateOut.setValue(PropertyHolder.SOLID_BG, true);
            contents[i] = new BlockCamoContent(stateOut);
        }
        return contents;
    }

    @Override
    public void clearCache()
    {
        super.clearCache();
        partCache.clear();
    }

    public static void collectCubeBaseModels(Map<BlockState, BlockStateModel> models)
    {
        for (int i = 0; i < NO_CAMO_CONTENTS.length; i++)
        {
            BlockStateModel model = models.get(NO_CAMO_CONTENTS[i].getState());
            if (model instanceof AbstractFramedBlockModel framedModel)
            {
                model = framedModel.getBaseModel();
            }
            NO_CAMO_MODELS[i] = model;
        }
    }

    private record CompoundPartCacheKey(
            CamoContent<?> camo,
            @Nullable Object ctContext,
            boolean secondPart,
            boolean emissive,
            @Nullable Object userData
    ) { }

    private record CullableBlockModelPart(ExtendedBlockModelPart wrapped, int cullMask) implements DelegateBlockModelPart
    {
        @Override
        public List<BakedQuad> getQuads(@Nullable Direction side)
        {
            return isSideHidden(cullMask, side) ? Collections.emptyList() : wrapped.getQuads(side);
        }
    }
}
