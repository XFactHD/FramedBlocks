package io.github.xfacthd.framedblocks.api.internal;

import com.mojang.datafixers.util.Either;
import io.github.xfacthd.framedblocks.api.camo.resource.ResourceCamoContent;
import io.github.xfacthd.framedblocks.api.camo.resource.ResourceCamoContentClientHandler;
import io.github.xfacthd.framedblocks.api.model.CachingModel;
import io.github.xfacthd.framedblocks.api.model.ExtendedBlockStateModelPart;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.item.ItemModelDataProvider;
import io.github.xfacthd.framedblocks.api.model.item.block.BlockItemModelProvider;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneModelFactory;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneWrapperKey;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.MaterialLookup;
import io.github.xfacthd.framedblocks.api.model.wrapping.ModelFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import io.github.xfacthd.framedblocks.api.render.outline.OutlineRenderer;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.resource.Resource;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public interface InternalClientAPI {
    InternalClientAPI INSTANCE = Utils.loadService(InternalClientAPI.class);

    void registerModelWrapper(Holder<Block> block, GeometryFactory geometryFactory, StateMerger stateMerger);

    void registerDoubleModelWrapper(Holder<Block> block, StateMerger stateMerger);

    void registerSpecialModelWrapper(Holder<Block> block, ModelFactory modelFactory, StateMerger stateMerger);

    void registerCopyingModelWrapper(Holder<Block> block, Holder<Block> srcBlock, StateMerger stateMerger);

    void registerEmptyModelWrapper(Holder<Block> block);

    <T> void registerStandaloneModelWrapper(
            StandaloneWrapperKey<T> wrapperKey,
            GeometryFactory blockGeometryFactory,
            StandaloneModelFactory<T> modelFactory,
            StateMerger stateMerger
    );

    void overrideBlockModelFactory(Holder<Block> block, Function<BlockState, BlockModel.Unbaked> blockModelFactory);

    void enqueueClientTask(int delay, Runnable task);

    ItemModel.Unbaked createFramedBlockItemModel(
            Block block,
            BlockItemModelProvider modelProvider,
            Either<Identifier, ItemTransforms> modelOrXform,
            boolean requiresData,
            Optional<ItemModelDataProvider> dataProvider
    );

    ExtendedBlockStateModelPart makeBlockModelPart(QuadMapBuilder quadMap, TriState partAO, Material.Baked particleMaterial, @Nullable BlockState shaderState);

    BlockStateModelDispatcher createFramedBlockDefinition(
            Either<BlockStateModelDispatcher, SingleVariant.Unbaked> wrapped,
            Map<String, SingleVariant.Unbaked> auxModels,
            Optional<StandaloneWrapperKey<?>> wrapperKey
    );

    Supplier<BlockStateModel> createBlockItemModelProviderForGeometry(BlockState state, BlockState srcState, GeometryFactory geometry, ModelBaker baker);

    OutlineRenderer<?> createModelBasedOutlineRenderer(Block block);

    MaterialLookup getRuntimeMaterialLookup();

    void registerLoadedCachingModel(CachingModel model);

    <R extends Resource, C extends ResourceCamoContent<R, C>> ResourceCamoContentClientHandler.ResourceModelBaker<R, C> createResourceModelBaker(
            ResourceCamoContentClientHandler<R, C> clientHandler
    );
}
