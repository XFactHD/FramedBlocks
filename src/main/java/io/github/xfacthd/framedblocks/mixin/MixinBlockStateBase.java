package io.github.xfacthd.framedblocks.mixin;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.cache.StateCache;
import io.github.xfacthd.framedblocks.api.internal.duck.StateCacheAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class MixinBlockStateBase implements StateCacheAccessor {
    @Unique
    @Nullable
    private StateCache framedblocks$cache = null;

    @Shadow
    protected abstract BlockState asState();

    @Override
    public void framedblocks$initCache(StateCache cache) {
        Preconditions.checkState(
                asState().getBlock() instanceof IFramedBlock,
                "IStateCacheAccessor#initCache() must only be called on blocks implementing IFramedBlock"
        );
        framedblocks$cache = cache;
    }

    @Override
    public StateCache framedblocks$getCache() {
        return Objects.requireNonNull(framedblocks$cache, "IStateCacheAccessor#framedblocks$getCache() called too early");
    }
}
