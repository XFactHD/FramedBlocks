package xfacthd.framedblocks.api.block.cache;

import org.jetbrains.annotations.ApiStatus;

public interface IStateCacheAccessor
{
    @ApiStatus.Internal
    void framedblocks$initCache(StateCache cache);

    StateCache framedblocks$getCache();
}
