package io.github.xfacthd.framedblocks.client.model.wrapping;

import com.google.common.base.Stopwatch;
import com.mojang.logging.LogUtils;
import io.github.xfacthd.framedblocks.api.model.AbstractFramedBlockStateModel;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneWrapperKey;
import io.github.xfacthd.framedblocks.api.model.wrapping.RegisterModelWrappersEvent;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.model.template.GeometryTemplateSpecImpl;
import io.github.xfacthd.framedblocks.client.model.unbaked.UnbakedStandaloneFramedBlockModel;
import io.github.xfacthd.framedblocks.common.config.DevToolsConfig;
import io.github.xfacthd.framedblocks.common.util.MarkdownTable;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterBlockModelsEvent;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class ModelWrappingManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<Block, ModelWrappingHandler> HANDLERS = new IdentityHashMap<>();
    private static final Map<StandaloneWrapperKey<?>, StandaloneModelWrappingHandler<?>> STANDALONE_HANDLERS = new IdentityHashMap<>();
    private static boolean locked = true;
    @Nullable
    private static MarkdownTable stateMergerDebugOutput;

    public static void fireRegistration() {
        Stopwatch stopwatch = Stopwatch.createStarted();

        locked = false;
        boolean debugLogging = DevToolsConfig.VIEW.isStateMergerDebugLoggingEnabled();
        if (debugLogging) {
            LOGGER.info("=============== Model Wrapper Registration Start ===============");
            stateMergerDebugOutput = new MarkdownTable();
            stateMergerDebugOutput.header("Block");
            stateMergerDebugOutput.header("Unhandled properties");
            stateMergerDebugOutput.header("Handled or ignored properties");
        }
        ModLoader.postEvent(new RegisterModelWrappersEvent());
        if (debugLogging) {
            LOGGER.info("StateMerger Debug Info\n{}", stateMergerDebugOutput.print().stripTrailing());
            LOGGER.info("=============== Model Wrapper Registration End =================");
            stateMergerDebugOutput = null;
        }
        locked = true;

        stopwatch.stop();
        LOGGER.debug("Registered model wrappers for {} blocks in {}", HANDLERS.size(), stopwatch);

        stopwatch = Stopwatch.createStarted();
        int specCount = GeometryTemplateSpecImpl.resolveAll();
        stopwatch.stop();
        LOGGER.debug("Prepared {} GeometryTemplateSpec entries for {} blocks in {}", specCount, GeometryTemplateSpecImpl.getBlockCount(), stopwatch);
    }

    public static void onRegisterStandaloneModels(ModelEvent.RegisterStandalone event) {
        STANDALONE_HANDLERS.forEach((wrapperKey, handler) ->
                event.register(wrapperKey.modelKey(), new UnbakedStandaloneFramedBlockModel<>(wrapperKey, handler.getModelFactory()))
        );
    }

    public static void onRegisterBlockModels(RegisterBlockModelsEvent event) {
        HANDLERS.values().forEach(handler -> handler.registerBlockModelFactory(event));
    }

    public static void register(Holder<Block> block, ModelWrappingHandler handler) {
        if (locked) {
            throw new IllegalStateException("ModelWrappingHandler registration is locked");
        }

        ModelWrappingHandler oldHandler = HANDLERS.put(block.value(), handler);
        if (oldHandler != null) {
            throw new IllegalStateException("ModelWrappingHandler for '" + block + "' already registered");
        }

        debugStateMerger(block, handler.getStateMerger());
    }

    public static void register(StandaloneWrapperKey<?> wrapperKey, StandaloneModelWrappingHandler<?> handler) {
        if (locked) {
            throw new IllegalStateException("ModelWrappingHandler registration is locked");
        }

        StandaloneModelWrappingHandler<?> oldHandler = STANDALONE_HANDLERS.put(wrapperKey, handler);
        if (oldHandler != null) {
            throw new IllegalStateException("ModelWrappingHandler for wrapper key '" + wrapperKey + "' already registered");
        }

        StandaloneWrapperKeys.registerKey(wrapperKey);
        debugStateMerger(wrapperKey.block(), wrapperKey.definitionFile().toString(), handler.getStateMerger());
    }

    public static ModelWrappingHandler getHandler(Block block) {
        ModelWrappingHandler handler = HANDLERS.get(block);
        if (handler == null) {
            throw new NullPointerException("No ModelWrappingHandler registered for block '" + block + "'");
        }
        return handler;
    }

    public static StateMerger tryGetStateMerger(Block block) {
        ModelWrappingHandler handler = HANDLERS.get(block);
        if (handler != null) {
            return handler.getStateMerger();
        }

        LOGGER.error("No ModelWrappingHandler registered for block {}, falling back to passthrough StateMerger", block);
        return StateMerger.PASSTHROUGH;
    }

    @SuppressWarnings("unchecked")
    public static <T> StandaloneModelWrappingHandler<T> getHandler(StandaloneWrapperKey<T> wrapperKey) {
        StandaloneModelWrappingHandler<?> handler = STANDALONE_HANDLERS.get(wrapperKey);
        if (handler == null) {
            throw new NullPointerException("No ModelWrappingHandler registered for wrapper key '" + wrapperKey + "'");
        }
        return (StandaloneModelWrappingHandler<T>) handler;
    }

    public static void printWrappingInfo(Map<BlockState, BlockStateModel> models) {
        int stateCount = 0;
        int distinctCount = 0;
        for (Map.Entry<Block, ModelWrappingHandler> entry : HANDLERS.entrySet()) {
            stateCount += entry.getKey().getStateDefinition().getPossibleStates().size();
            distinctCount += entry.getValue().getVisitedStateCount();
        }
        for (Map.Entry<StandaloneWrapperKey<?>, StandaloneModelWrappingHandler<?>> entry : STANDALONE_HANDLERS.entrySet()) {
            stateCount += entry.getKey().block().value().getStateDefinition().getPossibleStates().size();
            distinctCount += entry.getValue().getVisitedStateCount();
        }

        LOGGER.debug(
                "Wrapped {} unique block models ({} total) for {} blocks",
                distinctCount,
                stateCount,
                HANDLERS.size()
        );

        if (!Utils.PRODUCTION) {
            Map<BlockStateModel, Block> nonWrappedModels = new Reference2ObjectOpenHashMap<>();
            for (Block block : HANDLERS.keySet()) {
                List<BlockState> states = block.getStateDefinition().getPossibleStates();
                for (BlockState state : states) {
                    BlockStateModel model = models.get(state);
                    if (!(model instanceof AbstractFramedBlockStateModel)) {
                        nonWrappedModels.put(model, state.getBlock());
                    }
                }
                stateCount += states.size();
            }
            if (!nonWrappedModels.isEmpty()) {
                Set<String> blocks = nonWrappedModels.values()
                        .stream()
                        .distinct()
                        .map(Block::toString)
                        .collect(Collectors.toSet());
                LOGGER.warn(
                        "Found {} unwrapped models for {} blocks:\n\t{}",
                        nonWrappedModels.size(),
                        blocks.size(),
                        String.join("\n\t", blocks)
                );
            }
        }
    }

    private static void debugStateMerger(Holder<Block> block, StateMerger stateMerger) {
        String blockId = Utils.getKeyOrThrow(block).identifier().toString();
        debugStateMerger(block, blockId, stateMerger);
    }

    private static void debugStateMerger(Holder<Block> block, String blockId, StateMerger stateMerger) {
        if (stateMergerDebugOutput == null) {
            return;
        }

        Pattern debugFilterPattern = DevToolsConfig.VIEW.getStateMergerDebugFilter();
        if (debugFilterPattern != null) {
            if (!debugFilterPattern.matcher(blockId).matches()) {
                return;
            }
        }

        Set<Property<?>> props = new HashSet<>(block.value().getStateDefinition().getProperties());
        Set<Property<?>> ignoredProps = stateMerger.getHandledProperties(block);

        props.removeAll(ignoredProps);

        stateMergerDebugOutput
                .cell(blockId)
                .cell(propsToString(props))
                .cell(propsToString(ignoredProps))
                .newRow();
    }

    private static String propsToString(Collection<Property<?>> properties) {
        return properties.stream()
                .map(Property::getName)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    private ModelWrappingManager() { }
}
