package io.github.xfacthd.framedblocks.client.model.overlaygen;

import com.mojang.blaze3d.platform.Transparency;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.lighting.LightEngine;
import net.neoforged.neoforge.client.model.quad.MutableQuad;
import net.neoforged.neoforge.client.model.quad.BakedNormals;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public final class OverlayQuadGenerator
{
    private static final Map<OverlayCacheKey, BakedQuad> OVERLAY_CACHE = new ConcurrentHashMap<>();

    public static void generate(
            List<BakedQuad> srcQuads,
            ArrayList<BakedQuad> outQuads,
            Function<Direction, Material.Baked> spriteGetter,
            Predicate<Direction> filter,
            boolean forceTranslucent,
            boolean forceEmissive
    )
    {
        outQuads.ensureCapacity(outQuads.size() + srcQuads.size());
        Set<OverlayCacheKey> uniqueKeys = new HashSet<>(srcQuads.size());
        for (BakedQuad quad : srcQuads)
        {
            if (!filter.test(quad.direction())) continue;

            Material.Baked spriteInfo = spriteGetter.apply(quad.direction());
            OverlayCacheKey key = new OverlayCacheKey(quad, spriteInfo, forceTranslucent, forceEmissive);
            if (uniqueKeys.add(key))
            {
                outQuads.add(OVERLAY_CACHE.computeIfAbsent(key, OverlayQuadGenerator::generateOverlayQuad));
            }
        }
    }

    private static BakedQuad generateOverlayQuad(OverlayCacheKey key)
    {
        Material.Baked material = key.material();
        boolean forceTranslucent = material.forceTranslucent() || key.forceTranslucent();
        Transparency transparency = forceTranslucent ? Transparency.TRANSLUCENT : material.sprite().transparency();
        return generateOverlayQuad(key, key.face(), key.normals(), material, transparency, key.forceEmissive(), -1);
    }

    static BakedQuad generateOverlayQuad(
            VertexCoordProvider coords,
            Direction face,
            BakedNormals normals,
            Material.Baked material,
            Transparency transparency,
            boolean emissive,
            int tintIndex
    )
    {
        MutableQuad quad = new MutableQuad();

        quad.setSprite(material, transparency);
        quad.setDirection(face);
        quad.setAmbientOcclusion(!emissive);
        quad.setShade(!emissive);
        quad.setTintIndex(tintIndex);
        if (emissive)
        {
            quad.setLightEmission(LightEngine.MAX_LEVEL);
        }
        for (int i = 0; i < 4; i++)
        {
            quad.setPosition(i, coords.pos(i));
        }
        quad.setNormal(normals);
        quad.bakeUvsFromPosition();

        return quad.toBakedQuad();
    }

    public static void clearCaches()
    {
        OVERLAY_CACHE.clear();
    }

    sealed interface VertexCoordProvider permits OverlayCacheKey, BlockOverlayCacheKey.QuadBounds
    {
        Vector3fc pos(int index);
    }

    private OverlayQuadGenerator() { }
}
