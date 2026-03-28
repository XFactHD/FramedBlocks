package io.github.xfacthd.framedblocks.common.item;

import io.github.xfacthd.framedblocks.api.util.ConfigView;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public final class PhantomPasteItem extends Item {
    public static final Component FEATURE_DISABLED = Utils.translate("msg", "feature.intangibility.disabled").withColor(CommonColors.SOFT_RED);

    public PhantomPasteItem(Properties props) {
        super(props);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext ctx, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
        if (!ConfigView.Server.INSTANCE.enableIntangibility()) {
            lines.accept(FEATURE_DISABLED);
        }
    }
}
