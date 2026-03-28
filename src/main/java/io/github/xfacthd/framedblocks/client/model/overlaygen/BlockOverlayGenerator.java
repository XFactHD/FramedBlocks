package io.github.xfacthd.framedblocks.client.model.overlaygen;

import io.github.xfacthd.framedblocks.api.block.cache.StateCache;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.model.ExtendedBlockStateModelPart;
import io.github.xfacthd.framedblocks.client.model.FramedBlockStateModelPart;
import io.github.xfacthd.framedblocks.client.model.quadmap.QuadMapBuilderInternal;
import io.github.xfacthd.framedblocks.client.util.CacheCleaner;
import net.minecraft.Optionull;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import net.neoforged.neoforge.client.model.quad.BakedNormals;
import net.neoforged.neoforge.client.model.quad.MutableQuad;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class BlockOverlayGenerator {
    private static final Map<BlockOverlayCacheKey, ExtendedBlockStateModelPart> GEOMETRY_CACHE = new ConcurrentHashMap<>();

    public static void generateUncached(
            BlockState state,
            Holder<BlockOverlay> overlay,
            List<BlockStateModelPart> sourceParts,
            List<? super ExtendedBlockStateModelPart> outParts,
            boolean emissive,
            int tintIndex
    ) {
        generate(null, state, false, overlay, sourceParts, outParts, emissive, true, tintIndex);
    }

    @SuppressWarnings({ "unchecked" })
    public static void generateCached(
            @Nullable BlockState outerState,
            BlockState partState,
            boolean secondPart,
            Holder<BlockOverlay> overlay,
            List<? super ExtendedBlockStateModelPart> parts,
            boolean emissive,
            int tintIndex
    ) {
        generate(outerState, partState, secondPart, overlay, (List<BlockStateModelPart>) parts, parts, emissive, false, tintIndex);
    }

    private static void generate(
            @Nullable BlockState outerState,
            BlockState partState,
            boolean secondPart,
            Holder<BlockOverlay> overlay,
            List<BlockStateModelPart> sourceParts,
            List<? super ExtendedBlockStateModelPart> outParts,
            boolean emissive,
            boolean fastPath,
            int tintIndex
    ) {
        BlockOverlayCacheKey key = BlockOverlayCacheKey.compute(outerState, partState, secondPart, overlay.value(), sourceParts, emissive, fastPath, tintIndex);
        if (key != null) {
            ExtendedBlockStateModelPart part = GEOMETRY_CACHE.get(key);
            if (part == null) {
                part = generateOverlayPart(key);
                GEOMETRY_CACHE.put(key, part);
            }
            outParts.add(part);
        }
    }

    // FIXME: prism shapes (Prism Corner, Prism Corner Slope Panel) produce skewed textures
    private static ExtendedBlockStateModelPart generateOverlayPart(BlockOverlayCacheKey key) {
        QuadMapBuilderInternal quads = QuadMapBuilderInternal.create();

        StateCache stateCache = Optionull.mapOrDefault(key.outerState(), BlockState::framedblocks$getCache, StateCache.EMPTY);
        boolean secondPart = key.secondPart();
        BlockOverlay overlay = key.overlay();
        boolean emissive = key.emissive();
        BlockOverlayMetaCache.Entry metadata = BlockOverlayMetaCache.get(overlay, key.partState());
        boolean forceTranslucent = key.forceTranslucent();
        SpriteInfo solidSpriteInfo = metadata.solidSpriteInfo(forceTranslucent);
        SpriteInfo edgeSpriteInfo = metadata.edgeSpriteInfo(forceTranslucent);
        int tintIndex = overlay.tintSource() != null ? key.tintIndex() : -1;

        for (BlockOverlayCacheKey.Bounds bounds : key.bounds()) {
            Direction dir = bounds.normalDir();
            if (metadata.solidFaces().contains(dir)) {
                if (bounds.cullFace() != null || stateCache.supportsSolidOverlay(dir, secondPart)) {
                    // TODO: filter out faces which have an "occluding" face above them (i.e. all rungs of the ladder except the top one for solid overlay on UP)
                    generateSolidFaceOverlay(quads, bounds, solidSpriteInfo, emissive, tintIndex);
                }
            } else if (metadata.edgesByFace().containsKey(dir) && edgeSpriteInfo != null) {
                generateEdgeOverlay(quads, dir, secondPart, stateCache, bounds, edgeSpriteInfo, metadata, emissive, tintIndex);
            }
        }

        return new FramedBlockStateModelPart(quads.build(), key.ambientOcclusion(), metadata.solidMaterial(), null);
    }

    private static void generateSolidFaceOverlay(QuadMapBuilderInternal quads, BlockOverlayCacheKey.Bounds bounds, SpriteInfo spriteInfo, boolean emissive, int tintIndex) {
        ArrayList<BakedQuad> quadList = quads.getOrCreate(bounds.cullFace());
        BakedNormals normals = BakedNormals.of(bounds.normal());
        for (BlockOverlayCacheKey.QuadBounds quadBounds : bounds.quadBounds()) {
            quadList.add(OverlayQuadGenerator.generateOverlayQuad(quadBounds, bounds.normalDir(), normals, spriteInfo.material(), spriteInfo.transparency(), emissive, tintIndex));
        }
    }

    // TODO: implement support for tilted edges
    private static void generateEdgeOverlay(
            QuadMapBuilderInternal quads,
            Direction side,
            boolean secondPart,
            StateCache stateCache,
            BlockOverlayCacheKey.Bounds bounds,
            SpriteInfo spriteInfo,
            BlockOverlayMetaCache.Entry metadata,
            boolean emissive,
            int tintIndex
    ) {
        ArrayList<BakedQuad> quadList = quads.getOrCreate(bounds.cullFace());
        Direction face = bounds.normalDir();
        BakedNormals normals = BakedNormals.of(bounds.normal());
        BlockOverlayCacheKey.SurfaceBounds surfaceBounds = bounds.surfaceBounds();
        TextureAtlasSprite sprite = spriteInfo.material().sprite();
        Set<Direction> edges = metadata.edgesByFace().get(side);
        float edgeHeight = metadata.edgeHeight();

        MutableQuad quad = new MutableQuad();
        float[] uvCoords = new float[8];

        for (Direction edge : edges) {
            float vOff = switch (edge) {
                case DOWN -> surfaceBounds.minY();
                case UP -> 1F - surfaceBounds.maxY();
                case NORTH, WEST -> surfaceBounds.minX();
                case SOUTH, EAST -> 1F - surfaceBounds.maxX();
            };

            boolean nullCullFace = bounds.cullFace() == null;
            boolean unaligned = vOff > 0;
            if (nullCullFace || unaligned) {
                if (!stateCache.supportsEdgeOverlay(side, edge, secondPart, nullCullFace, unaligned)) {
                    continue;
                }
            }

            EdgeUVs edgeUVs = EdgeUVs.get(face, edge);
            for (BlockOverlayCacheKey.QuadBounds quadBounds : bounds.quadBounds()) {
                float minV = 1F;
                for (int i = 0; i < 4; i++) {
                    Vector3fc pos = quadBounds.pos(i);
                    float uSrc = pos.get(edgeUVs.uIdx());
                    float vSrc = pos.get(edgeUVs.vIdx());
                    float u = edgeUVs.uInv() ? (1F - uSrc) : uSrc;
                    float v = (edgeUVs.vInv() ? (1F - vSrc) : vSrc) - vOff;

                    uvCoords[i * 2] = u;
                    uvCoords[i * 2 + 1] = v;

                    minV = Math.min(minV, v);
                }
                if (minV > edgeHeight) {
                    continue;
                }

                quad.setDirection(face);
                quad.setSprite(spriteInfo.material(), spriteInfo.transparency());
                quad.setAmbientOcclusion(!emissive);
                quad.setShade(!emissive);
                quad.setTintIndex(tintIndex);
                if (emissive) {
                    quad.setLightEmission(LightEngine.MAX_LEVEL);
                }
                for (int i = 0; i < 4; i++) {
                    Vector3fc pos = quadBounds.pos(i);
                    quad.setPosition(i, pos);
                    quad.setUv(i, sprite.getU(uvCoords[i * 2]), sprite.getV(uvCoords[i * 2 + 1]));
                }
                quad.setNormal(normals);

                quadList.add(quad.toBakedQuad());
            }
        }
    }

    public static void clearCaches(CacheCleaner.Reason reason) {
        BlockOverlayMetaCache.clear(reason);
        GEOMETRY_CACHE.clear();
    }

    private BlockOverlayGenerator() { }
}
