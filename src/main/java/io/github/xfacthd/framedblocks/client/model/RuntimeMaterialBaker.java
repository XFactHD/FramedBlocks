package io.github.xfacthd.framedblocks.client.model;

import io.github.xfacthd.framedblocks.api.model.wrapping.MaterialLookup;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.util.CacheCleaner;
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

    public static RuntimeMaterialBaker getInstance() {
        return Objects.requireNonNull(instance, "RuntimeMaterialBaker not ready!");
    }

    private RuntimeMaterialBaker(SpriteLoader.Preparations blockAtlas, SpriteLoader.Preparations itemAtlas) {
        super(blockAtlas, itemAtlas);
    }

    @Override
    public Material.Baked getMaterial(Material material) {
        return get(material, () -> "");
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
        AtlasManager.PendingStitchResults pending = currentReload.get(AtlasManager.PENDING_STITCH);
        return pending.get(AtlasIds.BLOCKS)
                .thenCombine(pending.get(AtlasIds.ITEMS), Preparations::new)
                .thenCompose(preparationBarrier::wait)
                .thenAcceptAsync(RuntimeMaterialBaker::reload, reloadExecutor);
    }

    public static void clear(CacheCleaner.Reason reason) {
        if (reason == CacheCleaner.Reason.MANUAL && instance != null) {
            instance.bakedMaterials.clear();
        }
    }

    private static void reload(Preparations preparations) {
        instance = new RuntimeMaterialBaker(preparations.blockAtlas, preparations.itemAtlas);
    }

    private record Preparations(SpriteLoader.Preparations blockAtlas, SpriteLoader.Preparations itemAtlas) { }
}
