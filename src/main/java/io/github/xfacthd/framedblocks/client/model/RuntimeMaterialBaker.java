package io.github.xfacthd.framedblocks.client.model;

import io.github.xfacthd.framedblocks.api.model.wrapping.MaterialLookup;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.util.CacheCleaner;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class RuntimeMaterialBaker extends MaterialBaker implements MaterialLookup {
    public static final Identifier LISTENER_ID = Utils.id("runtime_material_baker");
    @Nullable
    private static RuntimeMaterialBaker instance;

    private final SpriteLoader.Preparations blockAtlas;

    public static RuntimeMaterialBaker getInstance() {
        return Objects.requireNonNull(instance, "RuntimeMaterialBaker not ready!");
    }

    private RuntimeMaterialBaker(SpriteLoader.Preparations blockAtlas) {
        super(blockAtlas.missing());
        this.blockAtlas = blockAtlas;
    }

    @Override
    public Material.Baked getMaterial(Material material) {
        return get(material, () -> "");
    }

    @Override
    public Material.Baked get(Material material, ModelDebugName name) {
        if (!material.sprite().equals(MissingTextureAtlasSprite.getLocation())) {
            Material.Baked baked = bakedMaterials.computeIfAbsent(material, bakerFunction);
            if (baked != null) {
                return baked;
            }
        }
        return replacementForMissingMaterial(material);
    }

    @Override
    protected Material.@Nullable Baked bake(Material material) {
        return bakeForAtlas(material, blockAtlas);
    }

    @Override
    public Material.Baked reportMissingReference(String ref, ModelDebugName modelName) {
        return missingSprite;
    }

    public static CompletableFuture<Void> reload(
            PreparableReloadListener.SharedState currentReload,
            @SuppressWarnings("unused") Executor taskExecutor,
            PreparableReloadListener.PreparationBarrier preparationBarrier,
            Executor reloadExecutor
    ) {
        return currentReload.get(AtlasManager.PENDING_STITCH)
                .get(AtlasIds.BLOCKS)
                .thenCompose(preparationBarrier::wait)
                .thenAcceptAsync(RuntimeMaterialBaker::reload, reloadExecutor);
    }

    public static void clear(CacheCleaner.Reason reason) {
        if (reason == CacheCleaner.Reason.MANUAL && instance != null) {
            instance.bakedMaterials.clear();
        }
    }

    private static void reload(SpriteLoader.Preparations atlas) {
        instance = new RuntimeMaterialBaker(atlas);
    }
}
