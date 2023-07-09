package xfacthd.framedblocks.client.util.mixin;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.cache.IStateCacheAccessor;
import xfacthd.framedblocks.api.block.cache.StateCache;

import java.util.Objects;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class MixinBlockStateBase implements IStateCacheAccessor
{
    @Unique
    private StateCache framedblocks$cache = null;

    @Shadow
    abstract BlockState asState();

    @Override
    public void framedblocks$initCache()
    {
        BlockState state = asState();
        if (state.getBlock() instanceof IFramedBlock block)
        {
            framedblocks$cache = block.initCache(state);
        }
    }

    @Override
    public StateCache framedblocks$getCache()
    {
        return Objects.requireNonNull(framedblocks$cache, "IStateCacheAccessor#framedblocks$getCache() called too early");
    }
}
