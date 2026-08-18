package io.github.xfacthd.framedblocks.client.apiimpl;

import com.mojang.datafixers.util.Either;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.camo.resource.ResourceCamoContent;
import io.github.xfacthd.framedblocks.api.camo.resource.ResourceCamoContentClientHandler;
import io.github.xfacthd.framedblocks.api.datagen.templates.GeometryTemplateBuilder;
import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;
import io.github.xfacthd.framedblocks.api.model.AbstractFramedBlockStateModel;
import io.github.xfacthd.framedblocks.api.model.CachingModel;
import io.github.xfacthd.framedblocks.api.model.item.ItemModelDataProvider;
import io.github.xfacthd.framedblocks.api.model.item.block.BlockItemModelProvider;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneWrapperKey;
import io.github.xfacthd.framedblocks.api.model.template.GeometryTemplateSpec;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.model.wrapping.AuxModelProvider;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.MaterialLookup;
import io.github.xfacthd.framedblocks.api.render.outline.OutlineRenderer;
import io.github.xfacthd.framedblocks.client.model.ResourceCubeModel;
import io.github.xfacthd.framedblocks.client.model.template.GeometryTemplateBuilderImpl;
import io.github.xfacthd.framedblocks.client.model.ReinforcementModel;
import io.github.xfacthd.framedblocks.client.model.RuntimeMaterialBaker;
import io.github.xfacthd.framedblocks.client.model.baked.FramedBlockStateModel;
import io.github.xfacthd.framedblocks.client.model.item.FramedBlockItemModel;
import io.github.xfacthd.framedblocks.client.model.template.GeometryTemplateSpecImpl;
import io.github.xfacthd.framedblocks.client.model.unbaked.FramedBlockModelDefinition;
import io.github.xfacthd.framedblocks.client.render.particle.BlockAtlasSpriteParticle;
import io.github.xfacthd.framedblocks.client.render.particle.BlockOverlayParticleProvider;
import io.github.xfacthd.framedblocks.client.render.special.ModelBasedOutlineRenderer;
import io.github.xfacthd.framedblocks.client.util.CacheCleaner;
import io.github.xfacthd.framedblocks.client.util.ClientTaskQueue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.resource.Resource;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

public final class InternalClientApiImpl implements InternalClientAPI {
    @Override
    public GeometryTemplateSpec createGeometryTemplateSpec(Holder<Block> block, BiConsumer<BlockState, GeometryTemplateSpec.SpecEntryBuilder> builderOperator) {
        return GeometryTemplateSpecImpl.createImpl(block, builderOperator);
    }

    @Override
    public GeometryTemplateBuilder createGeometryTemplateBuilder() {
        return new GeometryTemplateBuilderImpl();
    }

    @Override
    public void enqueueClientTask(int delay, Runnable task) {
        ClientTaskQueue.enqueueClientTask(delay, task);
    }

    @Override
    public ItemModel.Unbaked createFramedBlockItemModel(
            Block block,
            BlockItemModelProvider modelProvider,
            Either<Identifier, ItemTransforms> modelOrXform,
            boolean requiresData,
            Optional<ItemModelDataProvider> dataProvider
    ) {
        return new FramedBlockItemModel.Unbaked(block, modelProvider, modelOrXform, requiresData, dataProvider);
    }

    @Override
    public BlockStateModelDispatcher createFramedBlockDefinition(
            Either<BlockStateModelDispatcher, SingleVariant.Unbaked> wrapped,
            Map<String, SingleVariant.Unbaked> auxModels,
            Optional<StandaloneWrapperKey<?>> wrapperKey
    ) {
        return new BlockStateModelDispatcher(new FramedBlockModelDefinition(wrapped, auxModels, wrapperKey));
    }

    @Override
    public BlockItemModelProvider createBlockItemModelProviderForGeometry(@Nullable BlockState srcState, GeometryFactory geometry) {
        return (state, baker) -> {
            BlockStateModel baseModel = ModelUtils.getModel(Objects.requireNonNullElse(srcState, state));
            if (baseModel instanceof AbstractFramedBlockStateModel framedModel) {
                baseModel = framedModel.getBaseModel();
            }
            GeometryFactory.Context ctx = new GeometryFactory.Context(state, baseModel, AuxModelProvider.invalid(), MaterialLookup.runtime());
            ReinforcementModel reinforcement = ReinforcementModel.getOrCreate(baker);
            return new FramedBlockStateModel(ctx, geometry.create(ctx), reinforcement, false);
        };
    }

    @Override
    public OutlineRenderer<?> createModelBasedOutlineRenderer(Block block) {
        return new ModelBasedOutlineRenderer(block);
    }

    @Override
    public MaterialLookup getRuntimeMaterialLookup() {
        return RuntimeMaterialBaker.getInstance();
    }

    @Override
    public void registerLoadedCachingModel(CachingModel model) {
        CacheCleaner.registerLoadedCachingModel(model);
    }

    @Override
    public void registerPersistentCachingModel(CachingModel model) {
        CacheCleaner.registerPersistentCachingModel(model);
    }

    @Override
    public <R extends Resource, C extends ResourceCamoContent<R, C>> ResourceCamoContentClientHandler.ResourceModelBaker<R, C> createResourceModelBaker(
            ResourceCamoContentClientHandler<R, C> clientHandler
    ) {
        return new ResourceCubeModel<>(clientHandler);
    }

    @Override
    public Particle createBlockBreakParticle(ClientLevel level, double x, double y, double z, double sx, double sy, double sz, BlockPos pos, BlockState state, int tintColor) {
        TextureAtlasSprite sprite = ModelUtils.getModel(state).particleMaterial(level, pos, state).sprite();
        return new BlockAtlasSpriteParticle(level, x, y, z, sx, sy, sz, pos, sprite, tintColor);
    }

    @Override
    public Particle createBlockOverlayParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, BlockPos pos, BlockOverlay overlay) {
        return BlockOverlayParticleProvider.createParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, pos, overlay);
    }
}
