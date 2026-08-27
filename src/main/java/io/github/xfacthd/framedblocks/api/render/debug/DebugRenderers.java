package io.github.xfacthd.framedblocks.api.render.debug;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

/// Provides access to the built-in debug renderers.
@ApiStatus.NonExtendable
@SuppressWarnings("unused")
public interface DebugRenderers {
    DebugRenderers INSTANCE = Utils.loadService(DebugRenderers.class);

    /// {@return the debug renderer for {@link ConnectionPredicate}s}
    BlockDebugRenderer<IFramedBlockEntity> connectionPredicate();

    /// {@return the debug renderer for quad winding}
    BlockDebugRenderer<IFramedBlockEntity> quadWinding();

    /// {@return the debug renderer for {@link FramedBlockEntity#hitSecondary(BlockHitResult, Vec3, Vec3)}}
    ///
    /// @deprecated Promoted to user-facing feature
    @Deprecated(forRemoval = true, since = "26.1")
    BlockDebugRenderer<FramedDoubleBlockEntity> doubleBlockPart();
}
