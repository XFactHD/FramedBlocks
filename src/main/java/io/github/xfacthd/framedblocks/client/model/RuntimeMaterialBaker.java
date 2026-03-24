package io.github.xfacthd.framedblocks.client.model;

import io.github.xfacthd.framedblocks.api.model.wrapping.MaterialLookup;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.util.CacheCleaner;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.neoforge.common.util.Lazy;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Function;

public final class RuntimeMaterialBaker implements MaterialBaker, MaterialLookup, PreparableReloadListener
{
    private static final Material MISSING_MATERIAL = new Material(MissingTextureAtlasSprite.getLocation());
    public static final Identifier LISTENER_ID = Utils.id("runtime_material_baker");
    public static final RuntimeMaterialBaker INSTANCE = new RuntimeMaterialBaker();

    private static final Map<Material, Material.Baked> BAKED_MATERIALS = new ConcurrentHashMap<>();
    private static final Lazy<Material.Baked> BAKED_MISSING_MATERIAL = Lazy.of(() -> bake(MISSING_MATERIAL, false));
    private static final Function<Material, Material.Baked> BAKER = material -> bake(material, true);
    @Nullable
    private static Function<Identifier, TextureAtlasSprite> spriteLookup = null;

    private RuntimeMaterialBaker() { }

    public static TextureAtlasSprite getSprite(Identifier id)
    {
        Objects.requireNonNull(spriteLookup, "RuntimeMaterialBaker not ready!");
        return spriteLookup.apply(id);
    }

    @Override
    public Material.Baked getMaterial(Material material)
    {
        return get(material, () -> "");
    }

    @Override
    public Material.Baked get(Material material, ModelDebugName modelName)
    {
        return BAKED_MATERIALS.computeIfAbsent(material, BAKER);
    }

    @Override
    public Material.Baked reportMissingReference(String ref, ModelDebugName modelName)
    {
        return BAKED_MISSING_MATERIAL.get();
    }

    private static Material.Baked bake(Material material, boolean fallbackToMissing)
    {
        TextureAtlasSprite sprite = getSprite(material.sprite());
        if (fallbackToMissing && sprite == BAKED_MISSING_MATERIAL.get().sprite())
        {
            return BAKED_MISSING_MATERIAL.get();
        }
        return new Material.Baked(sprite, material.forceTranslucent());
    }

    @Override
    public CompletableFuture<Void> reload(SharedState currentReload, Executor taskExecutor, PreparationBarrier preparationBarrier, Executor reloadExecutor)
    {
        return currentReload.get(AtlasManager.PENDING_STITCH)
                .get(AtlasIds.BLOCKS)
                .thenCompose(preparationBarrier::wait)
                .thenAcceptAsync(RuntimeMaterialBaker::reload, reloadExecutor);
    }

    public static void clear(CacheCleaner.Reason reason)
    {
        if (reason == CacheCleaner.Reason.MANUAL)
        {
            clear();
        }
    }

    private static void reload(SpriteLoader.Preparations atlas)
    {
        clear();

        Map<Identifier, TextureAtlasSprite> sprites = atlas.regions();
        TextureAtlasSprite missingSprite = atlas.missing();
        spriteLookup = id -> sprites.getOrDefault(id, missingSprite);
    }

    private static void clear()
    {
        BAKED_MATERIALS.clear();
        BAKED_MISSING_MATERIAL.invalidate();
    }
}
