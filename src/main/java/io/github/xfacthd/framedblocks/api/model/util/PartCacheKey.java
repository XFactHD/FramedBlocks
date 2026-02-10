package io.github.xfacthd.framedblocks.api.model.util;

import io.github.xfacthd.framedblocks.api.camo.CamoContent;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.Internal
public interface PartCacheKey
{
    CamoContent<?> camo();

    @Nullable
    Object userData();
}
