package io.github.xfacthd.framedblocks.api.render.debug;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.util.Utils;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
@SuppressWarnings("unused")
public interface DebugRenderers {
    DebugRenderers INSTANCE = Utils.loadService(DebugRenderers.class);

    BlockDebugRenderer<IFramedBlockEntity> connectionPredicate();

    BlockDebugRenderer<IFramedBlockEntity> quadWinding();

    BlockDebugRenderer<FramedDoubleBlockEntity> doubleBlockPart();
}
