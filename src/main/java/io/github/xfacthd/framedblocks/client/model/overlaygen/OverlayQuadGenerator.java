package io.github.xfacthd.framedblocks.client.model.overlaygen;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.joml.Vector3f;

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
            Predicate<Direction> filter
    )
    {
        outQuads.ensureCapacity(outQuads.size() + srcQuads.size());
        Set<OverlayCacheKey> uniqueKeys = new HashSet<>(srcQuads.size());
        for (BakedQuad quad : srcQuads)
        {
            if (!filter.test(quad.direction())) continue;

            TextureAtlasSprite sprite = spriteGetter.apply(quad.direction());
            OverlayCacheKey key = buildCacheKey(quad, sprite);
            if (uniqueKeys.add(key))
            {
                outQuads.add(OVERLAY_CACHE.computeIfAbsent(key, OverlayQuadGenerator::generateOverlayQuad));
            }
        }
    }

    private static BakedQuad generateOverlayQuad(OverlayCacheKey key)
    {
        QuadBakingVertexConsumer baker = new QuadBakingVertexConsumer();

        TextureAtlasSprite sprite = key.sprite();
        UVInfo uvInfo = UVInfo.get(key.face());
        Vector3f scratch = new Vector3f();

        baker.setDirection(key.face());
        baker.setSprite(sprite);
        baker.setHasAmbientOcclusion(true);
        baker.setShade(true);

        for (int i = 0; i < 4; i++)
        {
            key.pos(i, scratch);
            baker.addVertex(scratch.x, scratch.y, scratch.z);

            float uSrc = scratch.get(uvInfo.uIdx());
            float vSrc = scratch.get(uvInfo.vIdx());
            float u = uvInfo.uInv() ? (1F - uSrc) : uSrc;
            float v = uvInfo.vInv() ? (1F - vSrc) : vSrc;
            baker.setUv(sprite.getU(u), sprite.getV(v));

            key.normal(i, scratch);
            baker.setNormal(scratch.x, scratch.y, scratch.z).setColor(-1);
        }

        return baker.bakeQuad();
    }

    private static OverlayCacheKey buildCacheKey(BakedQuad quad, TextureAtlasSprite sprite)
    {
        return new OverlayCacheKey(quad.direction(), quad.position0(), quad.position1(), quad.position2(), quad.position3(), quad.bakedNormals(), sprite);
    }

    public static void clearCaches()
    {
        OVERLAY_CACHE.clear();
    }

    private OverlayQuadGenerator() { }
}
