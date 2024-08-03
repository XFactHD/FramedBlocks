package xfacthd.framedblocks.api.model.cache;

import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.camo.CamoContent;

@SuppressWarnings("unused")
public interface QuadCacheKey
{
    CamoContent<?> camo();

    @Nullable
    Object ctCtx();
}
