package io.github.xfacthd.framedblocks.common.compat.jade;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.Element;

final class FramedBlockComponentProvider implements IBlockComponentProvider
{
    static final FramedBlockComponentProvider INSTANCE = new FramedBlockComponentProvider();

    private FramedBlockComponentProvider() { }

    @Override
    @Nullable
    public Element getIcon(BlockAccessor accessor, IPluginConfig config, @Nullable Element currentIcon)
    {
        if (!(accessor.getBlockState().getBlock() instanceof IFramedBlock block)) return null;
        if (!block.shouldRenderAsBlockInJadeTooltip()) return null;
        if (!(accessor.getBlockEntity() instanceof IFramedBlockEntity blockEntity)) return null;

        return new FramedBlockElement(accessor.getBlockState(), blockEntity);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config)
    {
        if (accessor.getBlockEntity() instanceof IFramedBlockEntity fbe)
        {
            Level level = accessor.getLevel();
            BlockPos pos = accessor.getPosition();
            Player player = accessor.getPlayer();
            if (fbe.getBlockType().isDoubleBlock() && fbe instanceof FramedDoubleBlockEntity fdbe)
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
    public Identifier getUid()
    {
        return JadeCompat.ID_FRAMED_BLOCK;
    }
}
