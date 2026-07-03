package io.github.xfacthd.framedblocks.api.datagen.models;

import com.mojang.datafixers.util.Either;
import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneWrapperKey;
import io.github.xfacthd.framedblocks.api.model.wrapping.AuxModelProvider;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/// Builder for blockstate files to take over model baking with the wrapping behavior registered for the respective block.
@SuppressWarnings("UnusedReturnValue")
public final class FramedBlockModelDefinitionGenerator implements BlockModelDefinitionGenerator {
    private final Block block;
    private final Either<BlockStateModelDispatcher, SingleVariant.Unbaked> baseModel;
    private final Optional<StandaloneWrapperKey<?>> wrapperKey;
    private final Map<String, SingleVariant.Unbaked> auxModels = new HashMap<>();

    FramedBlockModelDefinitionGenerator(Block block, BlockStateModelDispatcher definition, Optional<StandaloneWrapperKey<?>> wrapperKey) {
        this.block = block;
        this.baseModel = Either.left(definition);
        this.wrapperKey = wrapperKey;
    }

    FramedBlockModelDefinitionGenerator(Block block, SingleVariant.Unbaked variant, Optional<StandaloneWrapperKey<?>> wrapperKey) {
        this.block = block;
        this.baseModel = Either.right(variant);
        this.wrapperKey = wrapperKey;
    }

    /// Add an auxiliary model to be loaded and baked, accessible through the [AuxModelProvider] provided
    /// by [GeometryFactory.Context#auxModels()].
    ///
    /// @param key   The key to use for lookup during geometry construction
    /// @param model The model to load
    /// @return this builder
    public FramedBlockModelDefinitionGenerator addAuxModel(String key, SingleVariant.Unbaked model) {
        auxModels.put(key, model);
        return this;
    }

    @Override
    public Block block() {
        return block;
    }

    @Override
    public BlockStateModelDispatcher create() {
        return InternalClientAPI.INSTANCE.createFramedBlockDefinition(baseModel, auxModels, wrapperKey);
    }

    StandaloneWrapperKey<?> getWrapperKey() {
        return wrapperKey.orElseThrow();
    }
}
