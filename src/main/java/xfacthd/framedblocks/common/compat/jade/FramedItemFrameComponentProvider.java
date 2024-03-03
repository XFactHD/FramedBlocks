package xfacthd.framedblocks.common.compat.jade;

import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IDisplayHelper;
import xfacthd.framedblocks.common.blockentity.special.FramedItemFrameBlockEntity;

final class FramedItemFrameComponentProvider implements IBlockComponentProvider
{
    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig config)
    {
        if (blockAccessor.getBlockEntity() instanceof FramedItemFrameBlockEntity be && be.hasItem())
        {
            tooltip.add(IDisplayHelper.get().stripColor(be.getItem().getHoverName()));
        }
    }

    @Override
    public ResourceLocation getUid()
    {
        return JadeCompat.ID_ITEM_FRAME;
    }
}
