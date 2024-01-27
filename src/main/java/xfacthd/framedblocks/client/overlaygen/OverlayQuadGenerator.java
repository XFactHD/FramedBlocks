package xfacthd.framedblocks.client.overlaygen;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.joml.Vector3f;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.common.config.ClientConfig;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public final class OverlayQuadGenerator
{
    private static final Map<OverlayCacheKey, BakedQuad> OVERLAY_CACHE = new ConcurrentHashMap<>();

    public static List<BakedQuad> generate(List<BakedQuad> srcQuads, TextureAtlasSprite sprite, Predicate<BakedQuad> filter)
    {
        List<BakedQuad> outQuads = new ArrayList<>(srcQuads.size());
        Set<OverlayCacheKey> uniqueKeys = new HashSet<>(srcQuads.size());
        for (BakedQuad quad : srcQuads)
        {
            if (!filter.test(quad)) continue;

            OverlayCacheKey key = buildCacheKey(quad, sprite);
            if (uniqueKeys.add(key))
            {
                outQuads.add(OVERLAY_CACHE.computeIfAbsent(key, OverlayQuadGenerator::generateOverlayQuad));
            }
        }
        return outQuads;
    }

    private static BakedQuad generateOverlayQuad(OverlayCacheKey key)
    {
        QuadBakingVertexConsumer.Buffered baker = new QuadBakingVertexConsumer.Buffered();

        TextureAtlasSprite sprite = key.sprite();
        float shrinkRatio = sprite.uvShrinkRatio();
        UVInfo uvInfo = UVInfo.get(key.face());
        Vector3f scratch = new Vector3f();

        baker.setDirection(key.face());
        baker.setSprite(sprite);
        baker.setHasAmbientOcclusion(true);
        baker.setShade(true);

        for (int i = 0; i < 4; i++)
        {
            key.pos(i, scratch);
            baker.vertex(scratch.x, scratch.y, scratch.z);

            float uSrc = scratch.get(uvInfo.uIdx());
            float vSrc = scratch.get(uvInfo.vIdx());
            float u = Mth.lerp(shrinkRatio, uvInfo.uInv() ? (1F - uSrc) : uSrc, .5F);
            float v = Mth.lerp(shrinkRatio, uvInfo.vInv() ? (1F - vSrc) : vSrc, .5F);
            if (ClientConfig.VIEW.useDiscreteUVSteps())
            {
                if (!Mth.equal(uSrc, 0F) && !Mth.equal(uSrc, 1F))
                {
                    u = Math.round(u * ModelUtils.UV_SUBSTEP_COUNT) / ModelUtils.UV_SUBSTEP_COUNT;
                }
                if (!Mth.equal(vSrc, 0F) && !Mth.equal(vSrc, 1F))
                {
                    v = Math.round(v * ModelUtils.UV_SUBSTEP_COUNT) / ModelUtils.UV_SUBSTEP_COUNT;
                }
            }
            baker.uv(sprite.getU(u), sprite.getV(v));

            key.normal(i, scratch);
            baker.normal(scratch.x, scratch.y, scratch.z).color(1F, 1F, 1F, 1F).endVertex();
        }

        return baker.getQuad();
    }

    private static OverlayCacheKey buildCacheKey(BakedQuad quad, TextureAtlasSprite sprite)
    {
        int[] vertexData = quad.getVertices();
        int[] keyData = new int[(3 + 1) * 4];
        for (int i = 0; i < 4; i++)
        {
            int srcPos = i * IQuadTransformer.STRIDE;
            int destPos = i * 4;
            System.arraycopy(vertexData, srcPos + IQuadTransformer.POSITION, keyData, destPos, 3);
            keyData[destPos + 3] = vertexData[srcPos + IQuadTransformer.NORMAL];
        }
        return new OverlayCacheKey(quad.getDirection(), keyData, sprite);
    }

    public static void onResourceReload(@SuppressWarnings("unused") ResourceManager resourceManager)
    {
        OVERLAY_CACHE.clear();
    }



    private OverlayQuadGenerator() { }
}
