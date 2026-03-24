package io.github.xfacthd.framedblocks.api.render.fakelevel;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.neoforged.neoforge.model.data.ModelData;

public non-sealed interface FreestandingBlockRenderFakeLevel extends BlockRenderFakeLevel
{
    @Override
    default BlockPos pos()
    {
        return BlockPos.ZERO;
    }

    @Override
    default LevelLightEngine getLightEngine()
    {
        return LevelLightEngine.EMPTY;
    }

    @Override
    default CardinalLighting cardinalLighting()
    {
        return CardinalLighting.DEFAULT;
    }

    @Override
    default int getBlockTint(BlockPos pos, ColorResolver resolver)
    {
        return -1;
    }

    @Override
    default int getHeight()
    {
        return 1;
    }

    @Override
    default int getMinY()
    {
        return pos().getY();
    }

    record Simple(BlockState state, ModelData modelData) implements FreestandingBlockRenderFakeLevel { }
}
