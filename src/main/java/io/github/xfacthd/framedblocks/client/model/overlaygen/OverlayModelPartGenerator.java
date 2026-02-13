package io.github.xfacthd.framedblocks.client.model.overlaygen;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.model.ExtendedBlockModelPart;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.OverlayPartGenerator;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.client.model.QuadMapImpl;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Predicate;

public final class OverlayModelPartGenerator implements OverlayPartGenerator
{
    private final ObjectList<ExtendedBlockModelPart> staticParts;
    private final boolean forceEmissive;
    private final TriState ambientOcclusion;
    private final ObjectList<ExtendedBlockModelPart> generatedParts = new ObjectArrayList<>();
    private boolean flushed = false;

    public OverlayModelPartGenerator(ObjectList<ExtendedBlockModelPart> staticParts, boolean forceEmissive, TriState ambientOcclusion)
    {
        this.staticParts = staticParts;
        this.forceEmissive = forceEmissive;
        this.ambientOcclusion = ambientOcclusion;
    }

    @Override
    public void generate(
            @Nullable Direction[] cullfaces,
            SpriteGetter spriteGetter,
            TextureAtlasSprite primarySprite,
            Predicate<Direction> normalFilter,
            ChunkSectionLayer chunkLayer,
            @Nullable BlockState shaderState
    )
    {
        Preconditions.checkState(!flushed, "OverlayPartGenerator was already flushed");

        QuadMap quadMap = new QuadMapImpl();
        boolean hasQuads = false;
        for (BlockModelPart part : staticParts)
        {
            for (Direction side : cullfaces)
            {
                ArrayList<BakedQuad> outQuads = quadMap.get(side);
                OverlayQuadGenerator.generate(part.getQuads(side), outQuads, spriteGetter, normalFilter, forceEmissive);
                hasQuads |= !outQuads.isEmpty();
            }
        }
        if (hasQuads)
        {
            generatedParts.add(ModelUtils.makeModelPart(quadMap, ambientOcclusion, primarySprite, chunkLayer, shaderState));
        }
    }

    public void flush()
    {
        flushed = true;
        staticParts.addAll(generatedParts);
    }
}
