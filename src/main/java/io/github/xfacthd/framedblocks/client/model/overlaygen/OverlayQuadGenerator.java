package io.github.xfacthd.framedblocks.client.model.overlaygen;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.lighting.LightEngine;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import net.neoforged.neoforge.client.model.quad.BakedNormals;
import org.joml.Vector3f;
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
            Function<Direction, TextureAtlasSprite> spriteGetter,
            Predicate<Direction> filter,
            boolean forceEmissive
    )
    {
        outQuads.ensureCapacity(outQuads.size() + srcQuads.size());
        Set<OverlayCacheKey> uniqueKeys = new HashSet<>(srcQuads.size());
        for (BakedQuad quad : srcQuads)
        {
            if (!filter.test(quad.direction())) continue;

            TextureAtlasSprite sprite = spriteGetter.apply(quad.direction());
            OverlayCacheKey key = new OverlayCacheKey(quad, sprite, forceEmissive);
            if (uniqueKeys.add(key))
            {
                outQuads.add(OVERLAY_CACHE.computeIfAbsent(key, OverlayQuadGenerator::generateOverlayQuad));
            }
        }
    }

    private static BakedQuad generateOverlayQuad(OverlayCacheKey key)
    {
        return generateOverlayQuad(key, key.face(), key.normals(), key.sprite(), key.forceEmissive(), -1);
    }

    static BakedQuad generateOverlayQuad(VertexCoordProvider coords, Direction face, BakedNormals normals, TextureAtlasSprite sprite, boolean emissive, int tintIndex)
    {
        QuadBakingVertexConsumer baker = new QuadBakingVertexConsumer();

        UVInfo uvInfo = UVInfo.get(face);
        Vector3f scratch = new Vector3f();

        baker.setDirection(face);
        baker.setSprite(sprite);
        baker.setHasAmbientOcclusion(!emissive);
        baker.setShade(!emissive);
        baker.setTintIndex(tintIndex);
        if (emissive)
        {
            baker.setLightEmission(LightEngine.MAX_LEVEL);
        }

        for (int i = 0; i < 4; i++)
        {
            Vector3fc pos = coords.pos(i);
            baker.addVertex(pos.x(), pos.y(), pos.z());

            float uSrc = pos.get(uvInfo.uIdx());
            float vSrc = pos.get(uvInfo.vIdx());
            float u = uvInfo.uInv() ? (1F - uSrc) : uSrc;
            float v = uvInfo.vInv() ? (1F - vSrc) : vSrc;
            baker.setUv(sprite.getU(u), sprite.getV(v));

            BakedNormals.unpack(normals.normal(i), scratch);
            baker.setNormal(scratch.x, scratch.y, scratch.z).setColor(-1);
        }

        return baker.bakeQuad();
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
