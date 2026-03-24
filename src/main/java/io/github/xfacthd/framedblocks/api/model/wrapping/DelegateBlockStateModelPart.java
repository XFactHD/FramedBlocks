package io.github.xfacthd.framedblocks.api.model.wrapping;

import io.github.xfacthd.framedblocks.api.model.ExtendedBlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface DelegateBlockStateModelPart extends ExtendedBlockStateModelPart
{
    ExtendedBlockStateModelPart wrapped();

    @Override
    default List<BakedQuad> getQuads(@Nullable Direction side)
    {
        return wrapped().getQuads(side);
    }

    @Override
    @SuppressWarnings("deprecation")
    default boolean useAmbientOcclusion()
    {
        return wrapped().useAmbientOcclusion();
    }

    @Override
    default TriState ambientOcclusion()
    {
        return wrapped().ambientOcclusion();
    }

    @Override
    default Material.Baked particleMaterial()
    {
        return wrapped().particleMaterial();
    }

    @Override
    default int materialFlags()
    {
        return wrapped().materialFlags();
    }

    @Nullable
    @Override
    default BlockState getBlockAppearance()
    {
        return wrapped().getBlockAppearance();
    }
}
