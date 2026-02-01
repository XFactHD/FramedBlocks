package io.github.xfacthd.framedblocks.common.item;

import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.FramedToolType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public final class FramedWrenchItem extends FramedToolItem
{
    public static final String LABEL_MODE = Utils.translationKey("label", "framed_wrench.mode");
    public static final Component LABEL_TOGGLE = Utils.translate("label", "framed_wrench.mode.toggle").withStyle(ChatFormatting.ITALIC);

    public FramedWrenchItem(FramedToolType type, Properties props)
    {
        super(type, props.component(FBContent.DC_TYPE_WRENCH_MODE, WrenchRotationMode.PRIMARY));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand)
    {
        if (player.isShiftKeyDown())
        {
            ItemStack stack = player.getItemInHand(hand).copy();
            stack.update(FBContent.DC_TYPE_WRENCH_MODE, WrenchRotationMode.PRIMARY, WrenchRotationMode::getNext);
            return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
        }
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> appender, TooltipFlag flag)
    {
        WrenchRotationMode mode = stack.getOrDefault(FBContent.DC_TYPE_WRENCH_MODE, WrenchRotationMode.PRIMARY);
        appender.accept(Component.translatable(LABEL_MODE, mode.getTranslatedName()));
        appender.accept(LABEL_TOGGLE);
    }
}
