package io.github.xfacthd.framedblocks.client.model.template;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import io.github.xfacthd.framedblocks.api.model.template.GeometryTemplateSpec;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

public final class GeometryTemplateManager implements PreparableReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    public static final Identifier LISTENER_ID = Utils.id("geometry_templates");
    private static final StateKey<PendingTemplates> STATE_KEY = new StateKey<>();
    private static final AtomicReference<@Nullable SharedState> SHARED_STATE = new AtomicReference<>();
    private static final FileToIdConverter MODEL_LISTER = FileToIdConverter.json("models");
    private static final FileToIdConverter TEMPLATE_LISTER = FileToIdConverter.json("framed_templates");
    private static final Set<SourceFile.FileId> SOURCE_FILES = new HashSet<>();
    private static Map<Identifier, GeometryTemplate> templates = Map.of();
    private static Set<Identifier> failedTemplates = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void prepareSharedState(SharedState sharedState) {
        sharedState.set(STATE_KEY, new PendingTemplates(new CompletableFuture<>()));
        SHARED_STATE.set(sharedState);
    }

    @Override
    public CompletableFuture<Void> reload(SharedState sharedState, Executor asyncExecutor, PreparationBarrier barrier, Executor applyExecutor) {
        ResourceManager resourceManager = sharedState.resourceManager();
        PendingTemplates pending = sharedState.get(STATE_KEY);
        return CompletableFuture.supplyAsync(() -> prepare(resourceManager), asyncExecutor)
                .whenComplete((map, error) -> {
                    if (map != null) {
                        pending.future.complete(map);
                    } else {
                        pending.future.completeExceptionally(error);
                    }
                })
                .thenCompose(barrier::wait)
                .thenAcceptAsync(GeometryTemplateManager::apply, applyExecutor);
    }

    private static Map<Identifier, GeometryTemplate> prepare(ResourceManager resourceManager) {
        Map<Identifier, GeometryTemplate> templates = new HashMap<>();

        TEMPLATE_LISTER.listMatchingResources(resourceManager).forEach((fileName, resource) ->
                loadTemplate(templates, resource, fileName, TEMPLATE_LISTER.fileToId(fileName))
        );

        Set<SourceFile.FileId> remaining = new HashSet<>(SOURCE_FILES);
        remaining.removeIf(file -> !file.model() || templates.containsKey(file.id()));
        for (SourceFile.FileId sourceFile : remaining) {
            Identifier sourceFileName = MODEL_LISTER.idToFile(sourceFile.id());
            Optional<Resource> resource = resourceManager.getResource(sourceFileName);
            if (resource.isEmpty()) {
                LOGGER.error("Failed to find model source file: {}", sourceFileName);
                continue;
            }
            loadTemplate(templates, resource.get(), sourceFileName, sourceFile.id());
        }

        LOGGER.debug("Loaded {} geometry templates", templates.size());
        return templates;
    }

    private static void loadTemplate(Map<Identifier, GeometryTemplate> templates, Resource resource, Identifier fileName, Identifier id) {
        JsonObject obj;
        try (BufferedReader reader = resource.openAsReader()) {
            obj = GsonHelper.fromJson(GSON, reader, JsonObject.class);
        } catch (Throwable e) {
            LOGGER.error("Failed to read source file: {}", fileName, e);
            return;
        }

        GeometryTemplate.CODEC.parse(JsonOps.INSTANCE, obj)
                .ifSuccess(template -> templates.put(id, template))
                .ifError(error -> LOGGER.error("Failed to parse source file: {} - {}", id, error.message()));
    }

    private static void apply(Map<Identifier, GeometryTemplate> templates) {
        GeometryTemplateManager.templates = templates;
        failedTemplates = Collections.newSetFromMap(new ConcurrentHashMap<>());
        SHARED_STATE.set(null);
    }

    static void registerSourceFiles(Set<SourceFile.FileId> fileIds) {
        SOURCE_FILES.addAll(fileIds);
    }

    static GeometryTemplate getTemplate(Identifier name) {
        GeometryTemplate template;
        SharedState sharedState = SHARED_STATE.get();
        if (sharedState != null) {
            template = sharedState.get(STATE_KEY).get(name);
        } else {
            template = templates.get(name);
        }
        if (template != null) {
            return template;
        }
        if (failedTemplates.add(name)) {
            LOGGER.error("No template loaded with name: {}", name);
        }
        return GeometryTemplate.SINGLE_CUBE;
    }

    record PendingTemplates(CompletableFuture<Map<Identifier, GeometryTemplate>> future) {
        @Nullable GeometryTemplate get(Identifier name) {
            return future.join().get(name);
        }
    }

    public static GeometryFactory createTemplatedGeometryFactory(GeometryTemplateSpec templateSpec) {
        return new TemplatedGeometryFactory((GeometryTemplateSpecImpl) templateSpec);
    }
}
