package xfacthd.framedblocks.common.compat.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.block.blockentity.IFramedDoubleBlockEntity;

class FramedBlockComponentProvider implements IBlockComponentProvider
{
    static final FramedBlockComponentProvider INSTANCE = new FramedBlockComponentProvider();

    protected FramedBlockComponentProvider() { }

    @Override
    @Nullable
    public IElement getIcon(BlockAccessor accessor, IPluginConfig config, IElement currentIcon)
    {
        if (!(accessor.getBlockState().getBlock() instanceof IFramedBlock block)) return null;
        if (!block.shouldRenderAsBlockInJadeTooltip()) return null;
        if (!(accessor.getBlockEntity() instanceof FramedBlockEntity blockEntity)) return null;

        return new FramedBlockElement(accessor.getBlockState(), blockEntity);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config)
    {
        if (accessor.getBlockEntity() instanceof FramedBlockEntity fbe)
        {
            if (fbe.getBlockType().isDoubleBlock() && fbe instanceof IFramedDoubleBlockEntity fdbe)
            {
                tooltip.add(Component.translatable(JadeCompat.LABEL_CAMO_ONE, fbe.getCamo().getContent().getCamoName()));
                tooltip.add(Component.translatable(JadeCompat.LABEL_CAMO_TWO, fdbe.getCamoTwo().getContent().getCamoName()));
            }
            else
            {
                tooltip.add(Component.translatable(JadeCompat.LABEL_CAMO, fbe.getCamo().getContent().getCamoName()));
            }
        }
    }

    @Override
    public ResourceLocation getUid()
    {
        return JadeCompat.ID_FRAMED_BLOCK;
    }
}
