package io.github.xfacthd.framedblocks.client.model.overlaygen;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.model.ExtendedBlockStateModelPart;
import io.github.xfacthd.framedblocks.api.model.geometry.OverlayPartGenerator;
import io.github.xfacthd.framedblocks.client.model.FramedBlockStateModelPart;
import io.github.xfacthd.framedblocks.client.model.quadmap.QuadMapBuilderInternal;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

public final class OverlayModelPartGenerator implements OverlayPartGenerator {
    private final ObjectList<ExtendedBlockStateModelPart> staticParts;
    private final boolean forceEmissive;
    private final TriState ambientOcclusion;
    private final ObjectList<ExtendedBlockStateModelPart> generatedParts = new ObjectArrayList<>();
    private boolean flushed = false;

    public OverlayModelPartGenerator(ObjectList<ExtendedBlockStateModelPart> staticParts, boolean forceEmissive, TriState ambientOcclusion) {
        this.staticParts = staticParts;
        this.forceEmissive = forceEmissive;
        this.ambientOcclusion = ambientOcclusion;
    }

    @Override
    public void generate(@Nullable Direction[] cullFaces, MaterialGetter materialGetter, Material.Baked primaryMaterial, NormalFilter normalFilter, @Nullable BlockState shaderState) {
        Preconditions.checkState(!flushed, "OverlayPartGenerator was already flushed");

        QuadMapBuilderInternal quadMap = QuadMapBuilderInternal.create();
        int flags = 0;
        for (ExtendedBlockStateModelPart part : staticParts) {
            flags |= part.materialFlags();
        }
        boolean forceTranslucent = (flags & BakedQuad.FLAG_TRANSLUCENT) != 0;
        boolean hasQuads = false;
        for (BlockStateModelPart part : staticParts) {
            for (Direction side : cullFaces) {
                ArrayList<BakedQuad> outQuads = quadMap.getOrCreate(side);
                OverlayQuadGenerator.generate(part.getQuads(side), outQuads, materialGetter, normalFilter, forceTranslucent, forceEmissive);
                hasQuads |= !outQuads.isEmpty();
            }
        }
        if (hasQuads) {
            generatedParts.add(new FramedBlockStateModelPart(quadMap.build(), ambientOcclusion, primaryMaterial, shaderState));
        }
    }

    public void flush() {
        flushed = true;
        staticParts.addAll(generatedParts);
    }
}
