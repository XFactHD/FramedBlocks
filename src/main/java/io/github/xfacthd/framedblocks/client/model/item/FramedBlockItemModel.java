package io.github.xfacthd.framedblocks.client.model.item;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerHelper;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.model.CachingModel;
import io.github.xfacthd.framedblocks.api.model.item.ItemModelDataProvider;
import io.github.xfacthd.framedblocks.api.model.item.block.BlockItemModelProvider;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.model.util.TintUtils;
import io.github.xfacthd.framedblocks.api.render.fakelevel.FreestandingBlockRenderFakeLevel;
import io.github.xfacthd.framedblocks.api.util.ConfigView;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public final class FramedBlockItemModel implements ItemModel, CachingModel {
    private static final RandomSource RANDOM = RandomSource.create();
    private static final Direction[] DIRECTIONS = Arrays.copyOf(Direction.values(), 7);
    private static final Identifier ERROR_MODEL_LOCATION = Utils.id("item/error");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Set<Object> ERRORED_MODELS = new HashSet<>();

    private final Map<Object, ModelEntry> itemModelCache = new Object2ObjectOpenHashMap<>();
    private final BlockState state;
    private final Supplier<BlockStateModel> modelSupplier;
    private final ItemTransforms itemTransforms;
    private final boolean requiresData;
    private final ItemModelDataProvider dataProvider;
    private final ItemModel errorModel;
    private final Supplier<Vector3fc[]> extents;
    private final List<BlockStateModelPart> partScratchList = new ObjectArrayList<>();

    private FramedBlockItemModel(
            BlockState state,
            Supplier<BlockStateModel> modelSupplier,
            ItemTransforms itemTransforms,
            boolean requiresData,
            ItemModelDataProvider dataProvider,
            ItemModel errorModel
    ) {
        this.state = state;
        this.modelSupplier = Lazy.of(modelSupplier);
        this.itemTransforms = itemTransforms;
        this.requiresData = requiresData;
        this.dataProvider = dataProvider;
        this.errorModel = errorModel;
        this.extents = Lazy.of(() -> {
            ModelEntry modelEntry = getOrCreateModelEntry(ItemStack.EMPTY, CamoList.EMPTY, null);
            return CuboidItemModelWrapper.computeExtents(modelEntry.quads);
        });
        CachingModel.register(this);
    }

    @Override
    public void update(
            ItemStackRenderState renderState,
            ItemStack stack,
            ItemModelResolver resolver,
            ItemDisplayContext ctx,
            @Nullable ClientLevel level,
            @Nullable ItemOwner owner,
            int seed
    ) {
        boolean showCamo = ConfigView.Client.INSTANCE.shouldRenderItemModelsWithCamo();
        CamoList camos = showCamo ? stack.getOrDefault(FramedConstants.Objects.DC_TYPE_CAMO_LIST, CamoList.EMPTY) : CamoList.EMPTY;
        Holder<BlockOverlay> overlay = showCamo ? stack.get(FramedConstants.Objects.DC_TYPE_BLOCK_OVERLAY) : null;

        ModelEntry modelEntry;
        try {
            modelEntry = getOrCreateModelEntry(stack, camos, overlay);
        } catch (Throwable t) {
            if (ERRORED_MODELS.add(this)) {
                LOGGER.error("Encountered an error while computing item model for {}", stack.getItem(), t);
            }
            errorModel.update(renderState, stack, resolver, ctx, level, owner, seed);
            return;
        }

        renderState.appendModelIdentityElement(this);
        if (!modelEntry.camos.isEmpty()) {
            renderState.appendModelIdentityElement(modelEntry.camos);
        }
        if (modelEntry.overlay != null) {
            renderState.appendModelIdentityElement(modelEntry.overlay);
        }
        if (modelEntry.userData != null) {
            renderState.appendModelIdentityElement(modelEntry.userData);
        }
        if (modelEntry.animated) {
            renderState.setAnimated();
        }

        ItemStackRenderState.LayerRenderState layer = renderState.newLayer();
        layer.setExtents(extents);
        layer.prepareQuadList().addAll(modelEntry.quads);
        modelEntry.properties.applyToLayer(layer, ctx);
        if (!modelEntry.tints.isEmpty()) {
            layer.tintLayers().addAll(modelEntry.tints);
        }
    }

    private ModelEntry getOrCreateModelEntry(ItemStack stack, CamoList camos, @Nullable Holder<BlockOverlay> overlay) {
        Object userData = dataProvider.computeCacheKey(stack);
        Object cacheKey = userData != null || overlay != null ? new CompoundCacheKey(camos, overlay, userData) : camos;
        ModelEntry modelEntry = itemModelCache.get(cacheKey);
        if (modelEntry == null) {
            BlockStateModel model = modelSupplier.get();
            ModelData data = requiresData || !camos.isEmpty() ? dataProvider.buildItemModelData(state, camos, overlay) : ModelData.EMPTY;
            BlockAndTintGetter level = new FreestandingBlockRenderFakeLevel.Simple(state, data);

            ArrayList<BakedQuad> allQuads = new ArrayList<>();
            boolean animated = false;

            RANDOM.setSeed(42);
            model.collectParts(level, BlockPos.ZERO, state, RANDOM, partScratchList);
            for (BlockStateModelPart modelPart : partScratchList) {
                animated |= (modelPart.materialFlags() & BakedQuad.FLAG_ANIMATED) != 0;
                for (Direction face : DIRECTIONS) {
                    RANDOM.setSeed(42);
                    Utils.copyAll(modelPart.getQuads(face), allQuads);
                }
            }
            partScratchList.clear();

            int tintCount = camos.stream().mapToInt(CamoContainerHelper.Client::getTintCount).sum();
            IntArrayList tints = new IntArrayList(tintCount);
            if (tintCount > 0) {
                for (CamoContainer<?, ?> camo : camos) {
                    CamoContainerHelper.Client.collectTintValues(camo, stack, tints);
                }
            }
            if (overlay != null && overlay.value().tintSource() != null) {
                tints.add(TintUtils.getOverlayDefaultTint(overlay.value()));
            }
            dataProvider.appendTintValues(stack, tints);

            ModelRenderProperties renderProps = new ModelRenderProperties(true, model.particleMaterial(level, BlockPos.ZERO, state), itemTransforms);
            modelEntry = new ModelEntry(allQuads, renderProps, camos, tints.isEmpty() ? IntLists.emptyList() : tints, overlay, userData, animated);
            itemModelCache.put(cacheKey, modelEntry);
        }
        return modelEntry;
    }

    public ItemTransforms getItemTransforms() {
        return itemTransforms;
    }

    @Override
    public void clearCache() {
        itemModelCache.clear();
    }

    private record CompoundCacheKey(CamoList camos, @Nullable Holder<BlockOverlay> overlay, @Nullable Object userData) { }

    private record ModelEntry(
            List<BakedQuad> quads,
            ModelRenderProperties properties,
            CamoList camos,
            IntList tints,
            @Nullable Holder<BlockOverlay> overlay,
            @Nullable Object userData,
            boolean animated
    ) { }

    public record Unbaked(
            Block block,
            BlockItemModelProvider modelProvider,
            Either<Identifier, ItemTransforms> modelOrXform,
            boolean requiresData,
            Optional<ItemModelDataProvider> dataProvider
    ) implements ItemModel.Unbaked {
        public static final Identifier ID = Utils.id("block");
        public static final MapCodec<FramedBlockItemModel.Unbaked> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").validate(FramedBlockItemModel.Unbaked::validateBlock).forGetter(FramedBlockItemModel.Unbaked::block),
                BlockItemModelProviders.CODEC.optionalFieldOf("model_provider", BlockItemModelProvider.DEFAULT).forGetter(FramedBlockItemModel.Unbaked::modelProvider),
                Codec.mapEither(
                        Identifier.CODEC.fieldOf("base_model"),
                        ItemTransformsCodec.XFORMS_CODEC.fieldOf("transforms")
                ).forGetter(Unbaked::modelOrXform),
                Codec.BOOL.optionalFieldOf("requires_data", false).forGetter(Unbaked::requiresData),
                ItemModelDataProviders.CODEC.optionalFieldOf("data_provider").forGetter(Unbaked::dataProvider)
        ).apply(inst, FramedBlockItemModel.Unbaked::new));
        private static final ModelBaker.SharedOperationKey<ItemModel> ERROR_MODEL_KEY = ModelUtils.makeSharedOpsKey(baker -> {
            ResolvedModel model = baker.getModel(ERROR_MODEL_LOCATION);
            TextureSlots textureslots = model.getTopTextureSlots();
            QuadCollection quads = model.bakeTopGeometry(textureslots, baker, BlockModelRotation.IDENTITY);
            ModelRenderProperties renderProps = ModelRenderProperties.fromResolvedModel(baker, model, model.getTopTextureSlots());
            return new CuboidItemModelWrapper(List.of(), quads, renderProps, new Matrix4f());
        });

        public Unbaked {
            Preconditions.checkArgument(block instanceof IFramedBlock, "Expected IFramedBlock, got %s", block);
        }

        @Override
        public FramedBlockItemModel bake(BakingContext context, Matrix4fc transformation) {
            BlockState state = Objects.requireNonNull(((IFramedBlock) block).getItemModelSource());
            Supplier<BlockStateModel> modelSupplier = modelProvider.create(state, context.blockModelBaker());
            ItemTransforms transforms = modelOrXform.map(
                    model -> context.blockModelBaker().getModel(model).getTopTransforms(),
                    Function.identity()
            );
            ItemModel errorModel = context.blockModelBaker().compute(ERROR_MODEL_KEY);
            ItemModelDataProvider dataProvider = this.dataProvider.orElseGet(this::getDefaultDataProvider);
            return new FramedBlockItemModel(state, modelSupplier, transforms, requiresData, dataProvider, errorModel);
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            modelOrXform.ifLeft(resolver::markDependency);
            resolver.markDependency(ERROR_MODEL_LOCATION);
        }

        @Override
        public MapCodec<Unbaked> type() {
            return CODEC;
        }

        private ItemModelDataProvider getDefaultDataProvider() {
            if (((IFramedBlock) block).getBlockType().isDoubleBlock()) {
                return ItemModelDataProvider.DOUBLE_BLOCK;
            } else {
                return ItemModelDataProvider.DEFAULT;
            }
        }

        private static DataResult<Block> validateBlock(Block block) {
            if (block instanceof IFramedBlock) {
                return DataResult.success(block);
            }
            return DataResult.error(() -> "Expected IFramedBlock, got " + block);
        }
    }
}
