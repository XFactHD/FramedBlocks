package io.github.xfacthd.framedblocks.client.model.overlaygen;

import io.github.xfacthd.framedblocks.api.block.cache.StateCache;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.model.ExtendedBlockModelPart;
import io.github.xfacthd.framedblocks.client.model.FramedBlockModelPart;
import io.github.xfacthd.framedblocks.client.model.QuadMapImpl;
import io.github.xfacthd.framedblocks.client.util.CacheCleaner;
import net.minecraft.Optionull;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import net.neoforged.neoforge.client.model.quad.BakedNormals;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class BlockOverlayGenerator
{
    private static final Map<BlockOverlayCacheKey, ExtendedBlockModelPart> GEOMETRY_CACHE = new ConcurrentHashMap<>();

    public static void generateUncached(
            BlockState state,
            Holder<BlockOverlay> overlay,
            List<BlockModelPart> sourceParts,
            List<? super ExtendedBlockModelPart> outParts,
            boolean emissive
    )
    {
        generate(null, state, false, overlay, sourceParts, outParts, emissive, true);
    }

    @SuppressWarnings({ "unchecked" })
    public static void generateCached(
            @Nullable BlockState outerState,
            BlockState partState,
            boolean secondPart,
            Holder<BlockOverlay> overlay,
            List<? super ExtendedBlockModelPart> parts,
            boolean emissive
    )
    {
        generate(outerState, partState, secondPart, overlay, (List<BlockModelPart>) parts, parts, emissive, false);
    }

    private static void generate(
            @Nullable BlockState outerState,
            BlockState partState,
            boolean secondPart,
            Holder<BlockOverlay> overlay,
            List<BlockModelPart> sourceParts,
            List<? super ExtendedBlockModelPart> outParts,
            boolean emissive,
            boolean fastPath
    )
    {
        BlockOverlayCacheKey key = BlockOverlayCacheKey.compute(outerState, partState, secondPart, overlay.value(), sourceParts, emissive, fastPath);
        if (key != null)
        {
            outParts.add(GEOMETRY_CACHE.computeIfAbsent(key, BlockOverlayGenerator::generateOverlayPart));
        }
    }

    // FIXME: prism shapes (Prism Corner, Prism Corner Slope Panel) produce skewed textures
    private static ExtendedBlockModelPart generateOverlayPart(BlockOverlayCacheKey key)
    {
        QuadMapImpl quads = new QuadMapImpl();

        StateCache stateCache = Optionull.mapOrDefault(key.outerState(), BlockState::framedblocks$getCache, StateCache.EMPTY);
        boolean secondPart = key.secondPart();
        BlockOverlay overlay = key.overlay();
        boolean emissive = key.emissive();
        BlockOverlayMetaCache.Entry metadata = BlockOverlayMetaCache.get(overlay, key.partState());
        int tintIndex = overlay.tintSource() != null ? BlockOverlay.OVERLAY_TINT_INDEX : -1;

        for (BlockOverlayCacheKey.Bounds bounds : key.bounds())
        {
            Direction dir = bounds.normalDir();
            if (metadata.solidFaces().contains(dir))
            {
                if (bounds.cullFace() != null || stateCache.supportsSolidOverlay(dir, secondPart))
                {
                    // TODO: filter out faces which have an "occluding" face above them (i.e. all rungs of the ladder except the top one for solid overlay on UP)
                    generateSolidFaceOverlay(quads, bounds, metadata.solidSprite(), emissive, tintIndex);
                }
            }
            else if (metadata.edgesByFace().containsKey(dir) && metadata.edgeSprite() != null)
            {
                generateEdgeOverlay(quads, dir, secondPart, stateCache, bounds, metadata, emissive, tintIndex);
            }
        }

        return new FramedBlockModelPart(quads.build(), key.ambientOcclusion(), metadata.solidSprite(), key.chunkLayer(), null);
    }

    private static void generateSolidFaceOverlay(QuadMapImpl quads, BlockOverlayCacheKey.Bounds bounds, TextureAtlasSprite sprite, boolean emissive, int tintIndex)
    {
        ArrayList<BakedQuad> quadList = quads.getOrCreate(bounds.cullFace());
        BakedNormals normals = BakedNormals.of(bounds.normal());
        for (BlockOverlayCacheKey.QuadBounds quadBounds : bounds.quadBounds())
        {
            quadList.add(OverlayQuadGenerator.generateOverlayQuad(quadBounds, bounds.normalDir(), normals, sprite, emissive, tintIndex));
        }
    }

    // TODO: implement support for tilted edges
    private static void generateEdgeOverlay(
            QuadMapImpl quads,
            Direction side,
            boolean secondPart,
            StateCache stateCache,
            BlockOverlayCacheKey.Bounds bounds,
            BlockOverlayMetaCache.Entry metadata,
            boolean emissive,
            int tintIndex
    )
    {
        ArrayList<BakedQuad> quadList = quads.getOrCreate(bounds.cullFace());
        Direction face = bounds.normalDir();
        BakedNormals normals = BakedNormals.of(bounds.normal());
        BlockOverlayCacheKey.SurfaceBounds surfaceBounds = bounds.surfaceBounds();
        TextureAtlasSprite sprite = Objects.requireNonNull(metadata.edgeSprite());
        Set<Direction> edges = metadata.edgesByFace().get(side);
        float edgeHeight = metadata.edgeHeight();

        QuadBakingVertexConsumer baker = new QuadBakingVertexConsumer();
        float[] uvCoords = new float[8];
        Vector3f normScratch = new Vector3f();

        for (Direction edge : edges)
        {
            float vOff = switch (edge)
            {
                case DOWN -> surfaceBounds.minY();
                case UP -> 1F - surfaceBounds.maxY();
                case NORTH, WEST -> surfaceBounds.minX();
                case SOUTH, EAST -> 1F - surfaceBounds.maxX();
            };

            boolean nullCullFace = bounds.cullFace() == null;
            boolean unaligned = vOff > 0;
            if (nullCullFace || unaligned)
            {
                if (!stateCache.supportsEdgeOverlay(side, edge, secondPart, nullCullFace, unaligned))
                {
                    continue;
                }
            }

            UVInfo uvInfo = UVInfo.get(face, edge);
            for (BlockOverlayCacheKey.QuadBounds quadBounds : bounds.quadBounds())
            {
                float minV = 1F;
                for (int i = 0; i < 4; i++)
                {
                    Vector3fc pos = quadBounds.pos(i);
                    float uSrc = pos.get(uvInfo.uIdx());
                    float vSrc = pos.get(uvInfo.vIdx());
                    float u = uvInfo.uInv() ? (1F - uSrc) : uSrc;
                    float v = (uvInfo.vInv() ? (1F - vSrc) : vSrc) - vOff;

                    uvCoords[i * 2] = u;
                    uvCoords[i * 2 + 1] = v;

                    minV = Math.min(minV, v);
                }
                if (minV > edgeHeight) continue;

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
                    Vector3fc pos = quadBounds.pos(i);
                    baker.addVertex(pos.x(), pos.y(), pos.z());
                    baker.setUv(sprite.getU(uvCoords[i * 2]), sprite.getV(uvCoords[i * 2 + 1]));

                    BakedNormals.unpack(normals.normal(i), normScratch);
                    baker.setNormal(normScratch.x, normScratch.y, normScratch.z).setColor(-1);
                }

                quadList.add(baker.bakeQuad());
            }
        }
    }

    public static void clearCaches(CacheCleaner.Reason reason)
    {
        BlockOverlayMetaCache.clear(reason);
        GEOMETRY_CACHE.clear();
    }

    private BlockOverlayGenerator() { }
}
