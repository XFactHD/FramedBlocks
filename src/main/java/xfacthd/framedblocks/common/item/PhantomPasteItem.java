package xfacthd.framedblocks.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.*;
import xfacthd.framedblocks.api.util.ConfigView;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;

public final class PhantomPasteItem extends Item
{
    public static final Component FEATURE_DISABLED = Utils.translate("msg", "feature.intangibility.disabled").withColor(CommonColors.SOFT_RED);

    public PhantomPasteItem(Properties props)
    {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> lines, TooltipFlag flag)
    {
        if (!ConfigView.Server.INSTANCE.enableIntangibility())
        {
            lines.add(FEATURE_DISABLED);
        }
    }
}
