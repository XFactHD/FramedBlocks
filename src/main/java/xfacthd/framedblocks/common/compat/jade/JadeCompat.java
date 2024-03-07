package xfacthd.framedblocks.common.compat.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import xfacthd.framedblocks.api.util.Utils;

public final class JadeCompat
{
    public static final ResourceLocation ID_ITEM_FRAME = Utils.rl("framed_item_frame");

    public static Component configTranslation(ResourceLocation id)
    {
        return Component.translatable("config.jade.plugin_%s.%s".formatted(id.getNamespace(), id.getPath()));
    }



    private JadeCompat() { }
}
