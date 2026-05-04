package io.github.xfacthd.framedblocks.client.model;

import io.github.xfacthd.framedblocks.api.model.ExtendedBlockStateModelPart;
import io.github.xfacthd.framedblocks.client.model.quadmap.QuadMap;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * @param quads            The quad lists this part is made up of
 * @param ambientOcclusion Whether AO should be used
 * @param particleMaterial The particle texture used by this part
 * @param shaderState      The {@link BlockState} the framed block or part thereof is pretending to be, for use by shader mods
 */
public record FramedBlockStateModelPart(
        QuadMap quads,
        TriState ambientOcclusion,
        Material.Baked particleMaterial,
        @Nullable BlockState shaderState
) implements ExtendedBlockStateModelPart {
    @Override
    public List<BakedQuad> getQuads(@Nullable Direction side) {
        return quads.get(side);
    }

    @Override
    @Deprecated
    public boolean useAmbientOcclusion() {
        return !ambientOcclusion.isFalse();
    }

    @Override
    @BakedQuad.MaterialFlags
    public int materialFlags() {
        return quads.materialFlags();
    }

    @Override
    public @Nullable BlockState getBlockAppearance() {
        return shaderState;
    }
}
