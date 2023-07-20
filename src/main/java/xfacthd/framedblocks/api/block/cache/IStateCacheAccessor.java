package xfacthd.framedblocks.api.block.cache;

import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Map;

public interface IStateCacheAccessor
{
    @ApiStatus.Internal
    void framedblocks$initCache(Map<Block, List<StateCache>> dedupMap);

    StateCache framedblocks$getCache();
}
