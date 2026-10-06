package io.github.xfacthd.framedblocks.common.compat.jade;

import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.api.util.text.I18nUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class JadeCompat {
    public static final Identifier ID_FRAMED_BLOCK = Utils.id("framed_block_generic");
    public static final Identifier ID_ITEM_FRAME = Utils.id("framed_item_frame");
    public static final String LABEL_CAMO = I18nUtils.translationKey("label", "jade.camo.single");
    public static final String LABEL_CAMO_ONE = I18nUtils.translationKey("label", "jade.camo.double.one");
    public static final String LABEL_CAMO_TWO = I18nUtils.translationKey("label", "jade.camo.double.two");
    public static final String DETAIL_PREFIX = I18nUtils.translationKey("label", "jade.camo.details_prefix");

    public static Component configTranslation(Identifier id) {
        return Component.translatable("config.jade.plugin_%s.%s".formatted(id.getNamespace(), id.getPath()));
    }

    private JadeCompat() { }
}
