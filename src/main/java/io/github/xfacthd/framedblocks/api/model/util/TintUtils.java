package io.github.xfacthd.framedblocks.api.model.util;

import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.block.overlay.TintSource;
import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.fluid.FluidTintSource;

import java.util.List;
import java.util.function.Function;

/// Provides various helpers for working with tint colors.
public final class TintUtils {
    private static final Function<BlockState, List<BlockTintSource>> BLOCK_TINT_SOURCE_RESOLVER = state -> Minecraft.getInstance().getBlockColors().getTintSources(state);
    private static final BlockTintSource UNTINTED = BlockTintSources.constant(-1);

    /// {@return the list of block tint sources resolved by the given cache}
    ///
    /// @param sourceCache The cache to resolve through
    public static List<BlockTintSource> getTintSources(BlockTintSourceCache sourceCache) {
        return sourceCache.resolveTintSources(BLOCK_TINT_SOURCE_RESOLVER);
    }

    /// {@return the tint color of the given fluid in the given context}
    ///
    /// @param level The level the fluid is being rendered in
    /// @param pos   The position the fluid is being rendered at
    /// @param fluid The fluid being rendered
    public static int getFluidColor(BlockAndLightGetter level, BlockPos pos, FluidState fluid) {
        FluidTintSource tintSource = ModelUtils.getFluidModel(fluid).fluidTintSource();
        return tintSource != null ? tintSource.colorInWorld(fluid, fluid.createLegacyBlock(), ClientUtils.asTintGetter(level), pos) : -1;
    }

    /// {@return the block tint source backing the given overlay tint source}
    ///
    /// @param tintSource The overlay tint source to resolve
    public static BlockTintSource getOverlayTintSource(TintSource tintSource) {
        return tintSource.resolveTintSource(BLOCK_TINT_SOURCE_RESOLVER).orElse(UNTINTED);
    }

    /// {@return the out-of-level tint value of the given block overlay}
    ///
    /// @param overlay The block overlay to get the tint from
    public static int getOverlayDefaultTint(BlockOverlay overlay) {
        TintSource tintSource = overlay.tintSource();
        if (tintSource != null) {
            return getOverlayTintSource(tintSource).color(tintSource.defaultBlockState());
        }
        return -1;
    }

    private TintUtils() { }
}
