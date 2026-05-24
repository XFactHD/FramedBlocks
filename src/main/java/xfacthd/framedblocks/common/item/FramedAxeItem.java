package xfacthd.framedblocks.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.FramedToolType;

import java.util.List;

// Special single-purpose axe used for breaking framed blocks with their camo retained regardless of whether said camo is trivially droppable
public final class FramedAxeItem extends FramedToolItem
{
    private static final int DURABILITY = 1200; // Close to diamond tools
    private static final float BREAK_SPEED = 12F; // Identical to gold tools
    public static final Component TOOLTIP_RETAIN_CAMO = Utils.translate("desc", "framed_axe.retain_camo")
            .withStyle(ChatFormatting.ITALIC);

    public FramedAxeItem(FramedToolType type)
    {
        super(type, new Properties()
                .durability(DURABILITY)
                .component(DataComponents.TOOL, buildToolComponent())
                .component(FBContent.DC_TYPE_RETAIN_CAMO.value(), Unit.INSTANCE)
        );
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate)
    {
        return stack.is(Tags.Items.INGOTS_IRON);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag)
    {
        lines.add(TOOLTIP_RETAIN_CAMO);
    }

    private static Tool buildToolComponent()
    {
        List<Block> blocks = BuiltInRegistries.BLOCK
                .stream()
                .filter(block -> block instanceof IFramedBlock)
                .toList();
        return new Tool(List.of(Tool.Rule.minesAndDrops(blocks, BREAK_SPEED)), 1F, 1);
    }
}
