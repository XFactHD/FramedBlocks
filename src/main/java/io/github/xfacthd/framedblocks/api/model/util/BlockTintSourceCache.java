package io.github.xfacthd.framedblocks.api.model.util;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Function;

/// Super-interface for objects holding a blockstate for which to cache lazily resolved tint sources.
public interface BlockTintSourceCache {
    /// Returns the cached list of tint sources, resolving them via the given function if absent.
    ///
    /// @param resolver The resolver for retrieving the tint sources of a given blockstate
    /// @return the cached list of tint sources
    List<BlockTintSource> resolveTintSources(Function<BlockState, List<BlockTintSource>> resolver);
}
