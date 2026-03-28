package io.github.xfacthd.framedblocks.client.model.baked;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.model.ExtendedBlockStateModelPart;
import io.github.xfacthd.framedblocks.api.model.geometry.DefaultAO;
import io.github.xfacthd.framedblocks.api.model.geometry.PartConsumer;
import io.github.xfacthd.framedblocks.api.model.geometry.QuadListModifier;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.model.FramedBlockStateModelPart;
import io.github.xfacthd.framedblocks.client.model.quadmap.QuadMapBuilderInternal;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class PartConsumerImpl implements PartConsumer {
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final @Nullable Direction[] DIRECTIONS_WITH_NULL = Arrays.copyOfRange(DIRECTIONS, 0, 7);

    private final List<BlockStateModelPart> srcParts = new ObjectArrayList<>();
    private final List<? super ExtendedBlockStateModelPart> destParts;
    private final int cullMask;
    private final DefaultAO defaultAO;
    private final boolean camoEmissive;
    private final boolean forceEmissive;
    private final boolean cutoutLeaves;
    private int tintIndexOffset;
    private int maxTintIndex;
    private boolean countTintIndices = false;

    PartConsumerImpl(List<? super ExtendedBlockStateModelPart> destParts, int cullMask, DefaultAO defaultAO, boolean camoEmissive, boolean forceEmissive) {
        this.destParts = destParts;
        this.cullMask = cullMask;
        this.defaultAO = defaultAO;
        this.camoEmissive = camoEmissive;
        this.forceEmissive = forceEmissive;
        this.cutoutLeaves = Minecraft.getInstance().options.cutoutLeaves().get();
    }

    @Override
    public void acceptAll(
            BlockStateModel model,
            BlockAndTintGetter level,
            BlockPos pos,
            RandomSource random,
            BlockState state,
            boolean includeNull,
            boolean reclaimFromNull,
            boolean cullNonNull,
            @Nullable BlockState shaderState,
            @Nullable QuadListModifier modifier
    ) {
        model.collectParts(level, pos, state, random, srcParts);
        for (BlockStateModelPart part : srcParts) {
            accept(part, state, includeNull, reclaimFromNull, cullNonNull, false, shaderState, modifier);
        }
        srcParts.clear();
    }

    void acceptCamo(
            BlockStateModel model,
            BlockAndTintGetter level,
            BlockPos pos,
            RandomSource random,
            BlockState framedState,
            BlockState camoState,
            boolean cullNonNull,
            @Nullable QuadListModifier modifier
    ) {
        model.collectParts(level, pos, framedState, random, srcParts);
        for (BlockStateModelPart part : srcParts) {
            accept(part, camoState, false, true, cullNonNull, true, camoState, modifier);
        }
        srcParts.clear();
    }

    @Override
    public void accept(
            BlockStateModelPart part,
            BlockState partState,
            boolean includeNull,
            boolean reclaimFromNull,
            boolean cullNonNull,
            @Nullable BlockState shaderState,
            @Nullable QuadListModifier modifier
    ) {
        accept(part, partState, includeNull, reclaimFromNull, cullNonNull, false, shaderState, modifier);
    }

    private void accept(
            BlockStateModelPart part,
            BlockState partState,
            boolean includeNull,
            boolean reclaimFromNull,
            boolean cullNonNull,
            boolean camoPart,
            @Nullable BlockState shaderState,
            @Nullable QuadListModifier modifier
    ) {
        Preconditions.checkArgument(!(includeNull && reclaimFromNull), "Cannot both include null faces and reclaim cullable faces from them");

        QuadMapBuilderInternal quadMap = QuadMapBuilderInternal.create();
        boolean forcedOpaque = camoPart && ModelBlockRenderer.forceOpaque(cutoutLeaves, partState);
        boolean forceEmissive = this.forceEmissive;
        boolean emissive = forceEmissive || (camoPart && this.camoEmissive);
        boolean moveTintIndex = tintIndexOffset > 0;
        boolean hasListModifier = modifier != null;
        boolean hasPostModifiers = emissive || moveTintIndex || forcedOpaque;
        boolean countTintIndices = this.countTintIndices;
        boolean canInsertDirect = !(hasListModifier || hasPostModifiers || countTintIndices);
        boolean hasAnyQuads = false;
        for (Direction side : DIRECTIONS_WITH_NULL) {
            boolean nullSide = side == null;
            if (nullSide && !includeNull) {
                continue;
            }
            if (!nullSide && cullNonNull && FramedBlockStateModel.isSideHidden(cullMask, side)) {
                continue;
            }

            List<BakedQuad> srcQuads = part.getQuads(side);
            if (!nullSide && srcQuads.isEmpty() && reclaimFromNull) {
                srcQuads = ModelUtils.getFilteredNullQuads(part, side);
            }
            if (srcQuads.isEmpty()) {
                continue;
            }
            if (canInsertDirect) {
                quadMap.set(side, srcQuads);
                hasAnyQuads = true;
                continue;
            }
            if (hasListModifier) {
                ArrayList<BakedQuad> quads = new ArrayList<>(srcQuads);
                modifier.modify(quadMap, quads, side);
                // Copy to final destination at the end in case the modifier wants to iterate or clear the list
                Utils.copyAll(quads, quadMap.getOrCreate(side));
                hasAnyQuads |= !quadMap.isEmpty();
            } else {
                Utils.copyAll(srcQuads, quadMap.getOrCreate(side));
                hasAnyQuads = true;
            }
        }
        if (!hasAnyQuads) {
            return;
        }
        if (hasPostModifiers) {
            for (Direction side : DIRECTIONS_WITH_NULL) {
                ArrayList<BakedQuad> quads = quadMap.tryGet(side);
                if (quads != null && !quads.isEmpty()) {
                    //noinspection Java8ListReplaceAll
                    for (int i = 0; i < quads.size(); i++) {
                        quads.set(i, postProcessQuad(quads.get(i), emissive, forceEmissive, forcedOpaque));
                    }
                }
            }
        }
        if (countTintIndices) {
            int maxTintIndex = this.maxTintIndex;
            for (Direction side : DIRECTIONS_WITH_NULL) {
                ArrayList<BakedQuad> quads = quadMap.tryGet(side);
                if (quads != null && !quads.isEmpty()) {
                    for (BakedQuad quad : quads) {
                        maxTintIndex = Math.max(maxTintIndex, quad.materialInfo().tintIndex());
                    }
                }
            }
            this.maxTintIndex = maxTintIndex;
        }
        destParts.add(new FramedBlockStateModelPart(quadMap.build(), defaultAO.apply(part.ambientOcclusion()), part.particleMaterial(), shaderState));
    }

    void setTintIndexOffset(int tintIndexOffset) {
        this.tintIndexOffset = tintIndexOffset;
        this.maxTintIndex = tintIndexOffset - 1;
    }

    void setCountTintIndices(boolean countTintIndices) {
        this.countTintIndices = countTintIndices;
    }

    int getMaxTintIndex() {
        return maxTintIndex;
    }

    private BakedQuad postProcessQuad(BakedQuad quad, boolean emissive, boolean forceEmissive, boolean forceOpaque) {
        BakedQuad.MaterialInfo materialInfo = quad.materialInfo();
        ChunkSectionLayer layer = forceOpaque ? ChunkSectionLayer.SOLID : materialInfo.layer();
        int tintIndex = materialInfo.isTinted() ? materialInfo.tintIndex() + tintIndexOffset : materialInfo.tintIndex();
        boolean shade = !forceEmissive && materialInfo.shade();
        int lightEmission = emissive ? LightEngine.MAX_LEVEL : materialInfo.lightEmission();
        boolean ao = !forceEmissive && materialInfo.ambientOcclusion();
        if (layer != materialInfo.layer() || tintIndex != materialInfo.tintIndex() || shade != materialInfo.shade() || lightEmission != materialInfo.lightEmission() || ao != materialInfo.ambientOcclusion()) {
            materialInfo = new BakedQuad.MaterialInfo(materialInfo.sprite(), layer, materialInfo.itemRenderType(), tintIndex, shade, lightEmission, ao);
        }
        return new BakedQuad(
                quad.position0(),
                quad.position1(),
                quad.position2(),
                quad.position3(),
                quad.packedUV0(),
                quad.packedUV1(),
                quad.packedUV2(),
                quad.packedUV3(),
                quad.direction(),
                materialInfo,
                quad.bakedNormals(),
                quad.bakedColors()
        );
    }
}
