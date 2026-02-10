package io.github.xfacthd.framedblocks.api.model.util;

import io.github.xfacthd.framedblocks.api.camo.CamoContent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public interface PartCacheKey
{
    CamoContent<?> camo();

    @Nullable
    Object userData();
}
