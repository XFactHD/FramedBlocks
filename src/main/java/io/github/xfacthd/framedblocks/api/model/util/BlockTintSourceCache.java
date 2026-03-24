package io.github.xfacthd.framedblocks.api.model.util;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Function;

public interface BlockTintSourceCache
{
    List<BlockTintSource> resolveTintSources(Function<BlockState, List<BlockTintSource>> resolver);
}
