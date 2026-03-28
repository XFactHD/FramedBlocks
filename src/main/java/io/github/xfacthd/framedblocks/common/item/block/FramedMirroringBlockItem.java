package io.github.xfacthd.framedblocks.common.item.block;

import io.github.xfacthd.framedblocks.api.block.item.FramedBlockItem;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class FramedMirroringBlockItem extends FramedBlockItem {
    public static final Component PLACE_UPSIDE_DOWN = Utils.translate("desc", "slope_slab.place_upside_down")
            .withStyle(ChatFormatting.ITALIC);

    public FramedMirroringBlockItem(Block block, Properties props) {
        super(block, props);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, TooltipDisplay display, Consumer<Component> appender, TooltipFlag flag) {
        appender.accept(PLACE_UPSIDE_DOWN);
        super.appendHoverText(stack, ctx, display, appender, flag);
    }
}
