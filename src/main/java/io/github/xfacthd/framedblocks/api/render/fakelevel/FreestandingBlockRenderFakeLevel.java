package io.github.xfacthd.framedblocks.api.render.fakelevel;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.neoforged.neoforge.model.data.ModelData;

/// Freestanding implementation of a block render fake level for use in contexts where a real
/// level is either unavailable or is intentionally not used. Anchored at (0,0,0) by default.
public non-sealed interface FreestandingBlockRenderFakeLevel extends BlockRenderFakeLevel {
    @Override
    default BlockPos pos() {
        return BlockPos.ZERO;
    }

    @Override
    default LevelLightEngine getLightEngine() {
        return LevelLightEngine.EMPTY;
    }

    @Override
    default CardinalLighting cardinalLighting() {
        return CardinalLighting.DEFAULT;
    }

    @Override
    default int getBlockTint(BlockPos pos, ColorResolver resolver) {
        return -1;
    }

    @Override
    default int getHeight() {
        return 1;
    }

    @Override
    default int getMinY() {
        return pos().getY();
    }

    /// Simple implementation of a freestanding fake level.
    ///
    /// @param state     The blockstate to provide at the anchor position
    /// @param modelData The model data to provide at the anchor position
    record Simple(BlockState state, ModelData modelData) implements FreestandingBlockRenderFakeLevel { }
}
