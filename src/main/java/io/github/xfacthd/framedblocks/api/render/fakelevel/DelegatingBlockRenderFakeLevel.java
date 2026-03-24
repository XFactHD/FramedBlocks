package io.github.xfacthd.framedblocks.api.render.fakelevel;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.neoforged.neoforge.model.data.ModelData;

/// Delegating [BlockAndTintGetter] providing access to a single [BlockState] and associated [ModelData].
public non-sealed interface DelegatingBlockRenderFakeLevel extends BlockRenderFakeLevel
{
    /// Returns the real client level, if available, to delegate lookups of thread-safe data to
    BlockAndTintGetter realLevel();

    @Override
    default LevelLightEngine getLightEngine()
    {
        return realLevel().getLightEngine();
    }

    @Override
    default CardinalLighting cardinalLighting()
    {
        return realLevel().cardinalLighting();
    }

    @Override
    default int getBlockTint(BlockPos pos, ColorResolver resolver)
    {
        if (pos.equals(pos()))
        {
            return realLevel().getBlockTint(pos, resolver);
        }
        return -1;
    }

    @Override
    default int getHeight()
    {
        return realLevel().getHeight();
    }

    @Override
    default int getMinY()
    {
        return realLevel().getMinY();
    }
}
