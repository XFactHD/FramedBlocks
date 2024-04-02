package xfacthd.framedblocks.api.model.cache;

import xfacthd.framedblocks.api.camo.CamoContent;

@SuppressWarnings("unused")
public interface QuadCacheKey
{
    CamoContent<?> camo();

    Object ctCtx();
}
