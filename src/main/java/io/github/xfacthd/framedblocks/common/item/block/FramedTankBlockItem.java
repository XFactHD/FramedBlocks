package io.github.xfacthd.framedblocks.common.item.block;

import io.github.xfacthd.framedblocks.api.block.item.FramedBlockItem;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

import java.util.function.Consumer;

public class FramedTankBlockItem extends FramedBlockItem {
    public static final String TANK_CONTENTS = Utils.translationKey("desc", "block.fluid_tank.contents");
    public static final Component EMPTY_FLUID = Utils.translate("desc", "block.fluid_tank.contents.empty").withStyle(ChatFormatting.ITALIC);

    public FramedTankBlockItem(Block block, Properties props) {
        super(block, props.component(FBContent.DC_TYPE_TANK_CONTENTS, SimpleFluidContent.EMPTY));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, TooltipDisplay display, Consumer<Component> appender, TooltipFlag flag) {
        super.appendHoverText(stack, ctx, display, appender, flag);

        Component name = EMPTY_FLUID;
        SimpleFluidContent content = stack.getOrDefault(FBContent.DC_TYPE_TANK_CONTENTS, SimpleFluidContent.EMPTY);
        if (!content.isEmpty()) {
            name = content.getFluid().getFluidType().getDescription().copy().withStyle(ChatFormatting.WHITE);
        }
        appender.accept(Component.translatable(TANK_CONTENTS, name).withStyle(ChatFormatting.GOLD));
    }
}
