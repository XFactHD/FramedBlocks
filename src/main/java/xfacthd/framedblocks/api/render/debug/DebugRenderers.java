package xfacthd.framedblocks.api.render.debug;

import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.internal.InternalClientAPI;

public final class DebugRenderers
{
    public static final BlockDebugRenderer<FramedBlockEntity> CONNECTION_DEBUG_RENDERER;
    public static final BlockDebugRenderer<FramedBlockEntity> QUAD_WINDING_DEBUG_RENDERER;



    private DebugRenderers() { }

    static
    {
        CONNECTION_DEBUG_RENDERER = InternalClientAPI.INSTANCE.getConnectionDebugRenderer();
        QUAD_WINDING_DEBUG_RENDERER = InternalClientAPI.INSTANCE.getQuadWindingDebugRenderer();
    }
}
