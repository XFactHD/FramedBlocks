package xfacthd.framedblocks.api.model.cache;

import net.minecraft.world.level.block.state.BlockState;

/**
 * @param state The {@link BlockState} of the camo applied to the block
 * @param ctCtx The connected textures context data used by the camo model, may be null
 */
public record SimpleQuadCacheKey(BlockState state, Object ctCtx) implements QuadCacheKey { }
