package io.github.xfacthd.framedblocks.api.model.util;

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

public final class TintUtils
{
    private static final Function<BlockState, List<BlockTintSource>> BLOCK_TINT_SOURCE_RESOLVER = state -> Minecraft.getInstance().getBlockColors().getTintSources(state);
    private static final BlockTintSource UNTINTED = BlockTintSources.constant(-1);

    public static List<BlockTintSource> getTintSources(BlockTintSourceCache sourceCache)
    {
        return sourceCache.resolveTintSources(BLOCK_TINT_SOURCE_RESOLVER);
    }

    public static int getFluidColor(BlockAndLightGetter level, BlockPos pos, FluidState fluid)
    {
        FluidTintSource tintSource = ModelUtils.getFluidModel(fluid).fluidTintSource();
        return tintSource != null ? tintSource.colorInWorld(fluid, fluid.createLegacyBlock(), ClientUtils.asTintGetter(level), pos) : -1;
    }

    public static BlockTintSource getOverlayTintSource(TintSource tintSource)
    {
        return tintSource.resolveTintSource(BLOCK_TINT_SOURCE_RESOLVER).orElse(UNTINTED);
    }

    private TintUtils() { }
}
