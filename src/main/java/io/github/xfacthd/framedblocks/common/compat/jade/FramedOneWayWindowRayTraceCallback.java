package io.github.xfacthd.framedblocks.common.compat.jade;

import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedOwnableBlockEntity;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.callback.JadeRayTraceCallback;

final class FramedOneWayWindowRayTraceCallback implements JadeRayTraceCallback {
    private final IWailaClientRegistration registration;

    public FramedOneWayWindowRayTraceCallback(IWailaClientRegistration registration) {
        this.registration = registration;
    }

    @Override
    public @Nullable Accessor<?> onRayTrace(HitResult hitResult, @Nullable Accessor<?> accessor, @Nullable Accessor<?> originalAccessor) {
        if (accessor instanceof BlockAccessor blockAccessor && blockAccessor.getBlockState().is(FBContent.BLOCK_FRAMED_ONE_WAY_WINDOW)) {
            Direction face = blockAccessor.getBlockState().getValue(PropertyHolder.NULLABLE_FACE).toNullableDirection();
            if (face == null || face == blockAccessor.getSide()) {
                return accessor;
            }

            if (!(blockAccessor.getBlockEntity() instanceof FramedOwnableBlockEntity be)) {
                return accessor;
            }
            if (be.getCamo().isEmpty() || (be.getOwner() != null && be.getOwner().equals(accessor.getPlayer().getUUID()))) {
                return accessor;
            }

            BlockState camoState = be.getCamo().getContent().getAsBlockState();
            if (!camoState.isAir()) {
                return registration.blockAccessor()
                        .from(blockAccessor)
                        .blockEntity(() -> null)
                        .blockState(camoState)
                        .build();
            }
        }
        return accessor;
    }
}
