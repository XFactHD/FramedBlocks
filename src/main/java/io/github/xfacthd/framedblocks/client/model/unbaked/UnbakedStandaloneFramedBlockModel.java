package io.github.xfacthd.framedblocks.client.model.unbaked;

import com.google.common.collect.Maps;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import io.github.xfacthd.framedblocks.api.model.standalone.CachingModel;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneModelFactory;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneWrapperKey;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.neoforged.neoforge.client.model.standalone.UnbakedStandaloneModel;
import org.slf4j.Logger;

import java.io.Reader;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class UnbakedStandaloneFramedBlockModel<T extends CachingModel> implements UnbakedStandaloneModel<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<StandaloneWrapperKey<?>, CachingModel> BAKED_MODELS = new ConcurrentHashMap<>();
    private static final FileToIdConverter BLOCKSTATE_LISTER = FileToIdConverter.json(StandaloneWrapperKey.STANDALONE_DEFINITION_FOLDER);

    private final StandaloneWrapperKey<T> wrapperKey;
    private final StandaloneModelFactory<T> modelFactory;
    private final Map<BlockState, BlockStateModel.UnbakedRoot> unbakedModels;

    @SuppressWarnings("unchecked")
    public UnbakedStandaloneFramedBlockModel(StandaloneWrapperKey<?> wrapperKey, StandaloneModelFactory<?> modelFactory) {
        this.wrapperKey = (StandaloneWrapperKey<T>) wrapperKey;
        this.modelFactory = (StandaloneModelFactory<T>) modelFactory;
        this.unbakedModels = loadModelDefinition(wrapperKey);
    }

    @Override
    public T bake(ModelBaker baker) {
        Function<BlockState, BlockStateModel> modelProvider;
        if (unbakedModels.isEmpty()) {
            BlockStateModel missing = baker.compute(ModelUtils.MISSING_MODEL_KEY);
            modelProvider = _ -> missing;
        } else {
            modelProvider = state -> unbakedModels.get(state).bake(state, baker);
        }
        List<BlockState> states = wrapperKey.block().value().getStateDefinition().getPossibleStates();
        Map<BlockState, BlockStateModel> bakedModels = Maps.toMap(states, modelProvider::apply);

        T model = modelFactory.create(bakedModels);
        BAKED_MODELS.put(wrapperKey, model);
        return model;
    }

    @Override
    public void resolveDependencies(Resolver resolver) {
        unbakedModels.values().forEach(model -> model.resolveDependencies(resolver));
    }

    private static Map<BlockState, BlockStateModel.UnbakedRoot> loadModelDefinition(StandaloneWrapperKey<?> wrapperKey) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        Identifier file = wrapperKey.definitionFile();
        List<Resource> resources = resourceManager.getResourceStack(BLOCKSTATE_LISTER.idToFile(file));
        if (resources.isEmpty()) {
            LOGGER.error("Specified standalone model definition file '{}' does not exist", file);
            return Map.of();
        }

        List<LoadedDefinition> definitions = new ArrayList<>(resources.size());
        for (Resource resource : resources) {
            try (Reader reader = resource.openAsReader()) {
                BlockStateModelDispatcher definition = BlockStateModelDispatcher.CODEC
                        .parse(JsonOps.INSTANCE, StrictJsonParser.parse(reader))
                        .getOrThrow(JsonParseException::new);
                definitions.add(new LoadedDefinition(resource.sourcePackId(), definition));
            } catch (Exception e) {
                LOGGER.error("Failed to load standalone model definition {} from pack {}", file, resource.sourcePackId(), e);
            }
        }

        StateDefinition<Block, BlockState> stateDefinition = wrapperKey.block().value().getStateDefinition();
        Map<BlockState, BlockStateModel.UnbakedRoot> models = new IdentityHashMap<>(stateDefinition.getPossibleStates().size());
        for (LoadedDefinition definition : definitions) {
            models.putAll(definition.contents.instantiate(stateDefinition, () -> file + "/" + definition.source));
        }
        return models;
    }

    public static void clearCaches() {
        BAKED_MODELS.values().forEach(CachingModel::clearCache);
    }

    private record LoadedDefinition(String source, BlockStateModelDispatcher contents) {}
}
