package io.github.xfacthd.framedblocks.common.compat.jei;

import io.github.xfacthd.framedblocks.api.util.text.I18nUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;

public final class JeiCompat {
    public static final Component MSG_INVALID_RECIPE = I18nUtils.translate("msg", "framing_saw.transfer.invalid_recipe");
    public static final Component MSG_ONLY_SELECT_RECIPE = I18nUtils.translate("msg", "framing_saw.transfer.only_select_recipe");
    public static final Component MSG_SUPPORTS_MOST_CAMOS = I18nUtils.translate("msg", "camo_application.camo.most_supported")
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);

    public static void init() {
        if (ModList.get().isLoaded("jei")) {
            GuardedAccess.init();
        }
    }

    private static final class GuardedAccess {
        public static void init() {
            NeoForge.EVENT_BUS.addListener(FramedJeiPlugin::onRecipesReceived);
        }
    }

    private JeiCompat() { }
}
