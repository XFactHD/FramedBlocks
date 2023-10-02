package xfacthd.framedblocks.api.model.cache;

import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("unused")
public interface QuadCacheKey
{
    BlockState state();

    Object ctCtx();
}
