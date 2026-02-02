package xfacthd.framedblocks.common.compat.jade;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.block.blockentity.IFramedDoubleBlockEntity;
import xfacthd.framedblocks.api.camo.CamoContainer;

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
            Level level = accessor.getLevel();
            BlockPos pos = accessor.getPosition();
            Player player = accessor.getPlayer();
            if (fbe.getBlockType().isDoubleBlock() && fbe instanceof IFramedDoubleBlockEntity fdbe)
            {
                appendCamo(tooltip, level, pos, player, JadeCompat.LABEL_CAMO_ONE, fbe.getCamo());
                appendCamo(tooltip, level, pos, player, JadeCompat.LABEL_CAMO_TWO, fdbe.getCamoTwo());
            }
            else
            {
                appendCamo(tooltip, level, pos, player, JadeCompat.LABEL_CAMO, fbe.getCamo());
            }
        }
    }

    private static void appendCamo(ITooltip tooltip, Level level, BlockPos pos, Player player, String prefix, CamoContainer<?, ?> camo)
    {
        tooltip.add(Component.translatable(prefix, camo.getContent().getCamoName()));
        camo.appendJadeTooltip(level, pos, player, line -> tooltip.add(Component.translatable(JadeCompat.DETAIL_PREFIX, line)));
    }

    @Override
    public ResourceLocation getUid()
    {
        return JadeCompat.ID_FRAMED_BLOCK;
    }
}
