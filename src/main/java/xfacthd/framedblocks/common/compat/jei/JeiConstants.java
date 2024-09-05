package xfacthd.framedblocks.common.compat.jei;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import xfacthd.framedblocks.api.util.Utils;

public final class JeiConstants
{
    public static final Component MSG_INVALID_RECIPE = Utils.translate("msg", "framing_saw.transfer.invalid_recipe");
    public static final Component MSG_TRANSFER_NOT_IMPLEMENTED = Utils.translate("msg", "framing_saw.transfer.not_implemented");
    public static final Component MSG_SUPPORTS_MOST_CAMOS = Utils.translate("msg", "camo_application.camo.most_supported")
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);

    /**
     * Empty tags to help with serialization of the fake camo application recipes.
     * These gets replaced dynamically for displaying in JEI.
     */
    public static final TagKey<Item> CAMO_BLOCK_EXAMPLES_TAG = Utils.itemTag("jei_camo_block_examples");
    public static final TagKey<Item> ALL_FRAMES_TAG = Utils.itemTag("jei_all_frames");
    public static final TagKey<Item> DOUBLE_FRAMES_TAG = Utils.itemTag("jei_double_frames");



    private JeiConstants() { }
}
