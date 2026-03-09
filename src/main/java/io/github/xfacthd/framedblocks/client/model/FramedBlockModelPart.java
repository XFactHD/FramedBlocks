package io.github.xfacthd.framedblocks.client.model;

import io.github.xfacthd.framedblocks.api.model.ExtendedBlockModelPart;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * @param quads            The quad lists this part is made up of
 * @param ambientOcclusion Whether AO should be used
 * @param particleIcon     The particle texture used by this part
 * @param chunkLayer       The {@link ChunkSectionLayer} this part should render with
 * @param shaderState      The {@link BlockState} the framed block or part thereof is pretending to be, for use by shader mods
 */
public record FramedBlockModelPart(
        List<BakedQuad>[] quads,
        TriState ambientOcclusion,
        TextureAtlasSprite particleIcon,
        ChunkSectionLayer chunkLayer,
        @Nullable BlockState shaderState
) implements ExtendedBlockModelPart
{
    @Override
    public List<BakedQuad> getQuads(@Nullable Direction side)
    {
        return quads[DirUtils.maskNullDirection(side)];
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return !ambientOcclusion.isFalse();
    }

    @Override
    public ChunkSectionLayer getRenderType(BlockState state)
    {
        return chunkLayer;
    }

    @Nullable
    @Override
    public BlockState getBlockAppearance()
    {
        return shaderState;
    }
}
