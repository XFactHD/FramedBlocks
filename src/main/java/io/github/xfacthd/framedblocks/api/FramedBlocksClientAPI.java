package io.github.xfacthd.framedblocks.api;

import io.github.xfacthd.framedblocks.api.util.Utils;
import org.jetbrains.annotations.ApiStatus;

/// The primary client API entrypoint of FramedBlocks.
@ApiStatus.NonExtendable
@SuppressWarnings({ "unused" })
public interface FramedBlocksClientAPI {
    FramedBlocksClientAPI INSTANCE = Utils.loadService(FramedBlocksClientAPI.class);
}
