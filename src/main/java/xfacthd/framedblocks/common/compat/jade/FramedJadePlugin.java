package xfacthd.framedblocks.common.compat.jade;

import snownee.jade.api.*;
import xfacthd.framedblocks.common.block.interactive.FramedItemFrameBlock;

@WailaPlugin
public final class FramedJadePlugin implements IWailaPlugin
{
    @Override
    public void registerClient(IWailaClientRegistration registration)
    {
        registration.registerBlockComponent(new FramedItemFrameComponentProvider(), FramedItemFrameBlock.class);
    }
}
