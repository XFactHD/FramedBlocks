package xfacthd.framedblocks.common.compat.jade;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IDisplayHelper;
import snownee.jade.api.ui.IElement;
import snownee.jade.impl.ui.ItemStackElement;
import xfacthd.framedblocks.common.blockentity.special.FramedItemFrameBlockEntity;

final class FramedItemFrameComponentProvider implements IBlockComponentProvider
{
    static final FramedItemFrameComponentProvider INSTANCE = new FramedItemFrameComponentProvider();

    private FramedItemFrameComponentProvider() { }

    @Override
    @Nullable
    public IElement getIcon(BlockAccessor accessor, IPluginConfig config, IElement currentIcon)
    {
        if (accessor.getBlockEntity() instanceof FramedItemFrameBlockEntity be && be.hasItem())
        {
            return ItemStackElement.of(be.getItem());
        }
        return null;
    }

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
