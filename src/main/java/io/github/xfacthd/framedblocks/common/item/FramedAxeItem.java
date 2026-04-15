package io.github.xfacthd.framedblocks.common.item;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.FramedToolType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Consumer;

// Special single-purpose axe used for breaking framed blocks with their camo retained regardless of whether said camo is trivially droppable
public final class FramedAxeItem extends FramedToolItem {
    private static final int DURABILITY = 1200; // Close to diamond tools
    private static final float BREAK_SPEED = 12F; // Identical to gold tools
    public static final Component TOOLTIP_RETAIN_CAMO = Utils.translate("desc", "framed_axe.retain_camo")
            .withStyle(ChatFormatting.ITALIC);

    public FramedAxeItem(FramedToolType type, Properties props) {
        super(type, props
                .durability(DURABILITY)
                .repairable(ItemTags.IRON_TOOL_MATERIALS)
                .component(DataComponents.TOOL, buildToolComponent())
                .component(FBContent.DC_TYPE_RETAIN_CAMO.value(), Unit.INSTANCE)
        );
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> appender, TooltipFlag flag) {
        appender.accept(TOOLTIP_RETAIN_CAMO);
    }

    private static Tool buildToolComponent() {
        HolderSet.Direct<Block> blocks = HolderSet.direct(
                BuiltInRegistries.BLOCK
                        .listElements()
                        .filter(block -> block.value() instanceof IFramedBlock)
                        .toList()
        );
        return new Tool(List.of(Tool.Rule.minesAndDrops(blocks, BREAK_SPEED)), 1F, 1, true);
    }
}
