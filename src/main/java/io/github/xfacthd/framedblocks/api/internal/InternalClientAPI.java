package io.github.xfacthd.framedblocks.api.internal;

import com.mojang.datafixers.util.Either;
import io.github.xfacthd.framedblocks.api.block.render.NullCullPredicate;
import io.github.xfacthd.framedblocks.api.model.ExtendedBlockModelPart;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.item.ItemModelInfo;
import io.github.xfacthd.framedblocks.api.model.item.block.BlockItemModelProvider;
import io.github.xfacthd.framedblocks.api.model.item.tint.DynamicItemTintProvider;
import io.github.xfacthd.framedblocks.api.model.standalone.CachingModel;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneModelFactory;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneWrapperKey;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.ModelFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.SingleVariant;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@ApiStatus.Internal
public interface InternalClientAPI
{
    InternalClientAPI INSTANCE = Utils.loadService(InternalClientAPI.class);



    void registerModelWrapper(Holder<Block> block, GeometryFactory geometryFactory, StateMerger stateMerger);

    void registerDoubleModelWrapper(Holder<Block> block, NullCullPredicate nullCullPredicate, ItemModelInfo itemModelInfo, StateMerger stateMerger);

    void registerSpecialModelWrapper(Holder<Block> block, ModelFactory modelFactory, StateMerger stateMerger);

    void registerCopyingModelWrapper(Holder<Block> block, Holder<Block> srcBlock, StateMerger stateMerger);

    <T extends CachingModel> void registerStandaloneModelWrapper(
            StandaloneWrapperKey<T> wrapperKey,
            GeometryFactory blockGeometryFactory,
            StandaloneModelFactory<T> modelFactory,
            StateMerger stateMerger
    );

    void enqueueClientTask(int delay, Runnable task);

    ItemModel.Unbaked createFramedBlockItemModel(Block block, BlockItemModelProvider modelProvider, DynamicItemTintProvider tintProvider, Identifier baseModel);

    ExtendedBlockModelPart makeBlockModelPart(QuadMapBuilder quadMap, TriState partAO, TextureAtlasSprite particleSprite, ChunkSectionLayer chunkLayer, @Nullable BlockState shaderState);

    BlockModelDefinition createFramedBlockDefinition(
            Either<BlockModelDefinition, SingleVariant.Unbaked> wrapped,
            Map<String, SingleVariant.Unbaked> auxModels,
            Optional<StandaloneWrapperKey<?>> wrapperKey
    );

    Supplier<BlockStateModel> createBlockItemModelProviderForGeometry(BlockState state, BlockState srcState, GeometryFactory geometry, ModelBaker baker);
}
