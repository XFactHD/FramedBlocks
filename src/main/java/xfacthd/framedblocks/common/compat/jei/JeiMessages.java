package xfacthd.framedblocks.common.compat.jei;

import net.minecraft.network.chat.Component;
import xfacthd.framedblocks.api.util.Utils;

public final class JeiMessages
{
    public static final Component MSG_INVALID_RECIPE = Utils.translate("msg", "framing_saw.transfer.invalid_recipe");
    public static final Component MSG_TRANSFER_NOT_IMPLEMENTED = Utils.translate("msg", "framing_saw.transfer.not_implemented");
    public static final Component MSG_SUPPORTS_MOST_CAMOS = Utils.translate("msg", "camo_application.camo.most_supported");


    private JeiMessages() { }
}
