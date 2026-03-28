package io.github.xfacthd.framedblocks.api;

import io.github.xfacthd.framedblocks.api.util.Utils;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
@SuppressWarnings({ "unused" })
public interface FramedBlocksClientAPI {
    FramedBlocksClientAPI INSTANCE = Utils.loadService(FramedBlocksClientAPI.class);
}
