package io.github.xfacthd.framedblocks.common.compat.jade;

import io.github.xfacthd.framedblocks.common.blockentity.special.FramedItemFrameBlockEntity;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.IDisplayHelper;
import snownee.jade.api.ui.JadeUI;

final class FramedItemFrameComponentProvider implements IBlockComponentProvider {
    static final FramedItemFrameComponentProvider INSTANCE = new FramedItemFrameComponentProvider();

    private FramedItemFrameComponentProvider() { }

    @Override
    public @Nullable Element getIcon(BlockAccessor accessor, IPluginConfig config, @Nullable Element currentIcon) {
        if (accessor.getBlockEntity() instanceof FramedItemFrameBlockEntity be && be.hasItem()) {
            return JadeUI.item(be.getItem());
        }
        return null;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig config) {
        if (blockAccessor.getBlockEntity() instanceof FramedItemFrameBlockEntity be && be.hasItem()) {
            tooltip.add(IDisplayHelper.get().stripColor(be.getItem().getHoverName()));
        }
    }

    @Override
    public Identifier getUid() {
        return JadeCompat.ID_ITEM_FRAME;
    }
}
