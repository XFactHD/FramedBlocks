package xfacthd.framedblocks.client.util.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.cache.IStateCacheAccessor;
import xfacthd.framedblocks.api.block.cache.StateCache;

import java.util.*;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class MixinBlockStateBase implements IStateCacheAccessor
{
    @Unique
    private StateCache framedblocks$cache = null;

    @Shadow
    abstract BlockState asState();

    @Override
    public void framedblocks$initCache(Map<Block, List<StateCache>> dedupMap)
    {
        BlockState state = asState();
        if (state.getBlock() instanceof IFramedBlock block)
        {
            StateCache newCache = block.initCache(state);
            List<StateCache> caches = dedupMap.computeIfAbsent(state.getBlock(), $ -> new ArrayList<>());
            framedblocks$cache = caches.stream().filter(newCache::equals).findFirst().orElseGet(() ->
            {
                caches.add(newCache);
                return newCache;
            });
        }
    }

    @Override
    public StateCache framedblocks$getCache()
    {
        return Objects.requireNonNull(framedblocks$cache, "IStateCacheAccessor#framedblocks$getCache() called too early");
    }
}
