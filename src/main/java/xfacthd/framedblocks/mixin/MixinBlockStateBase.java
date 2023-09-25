package xfacthd.framedblocks.mixin;

import com.google.common.base.Preconditions;
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
    protected abstract BlockState asState();

    @Override
    public void framedblocks$initCache(StateCache cache)
    {
        Preconditions.checkState(
                asState().getBlock() instanceof IFramedBlock,
                "IStateCacheAccessor#initCache() must only be called on blocks implementing IFramedBlock"
        );
        framedblocks$cache = cache;
    }

    @Override
    public StateCache framedblocks$getCache()
    {
        return Objects.requireNonNull(framedblocks$cache, "IStateCacheAccessor#framedblocks$getCache() called too early");
    }
}
