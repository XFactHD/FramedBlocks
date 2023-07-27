package xfacthd.framedblocks.client.render.item;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;

public final class BlueprintPropertyOverride
{
    public static final ResourceLocation HAS_DATA = Utils.rl("blueprint_override");

    private BlueprintPropertyOverride() { }

    public static void register()
    {
        ItemProperties.register(
                FBContent.ITEM_FRAMED_BLUEPRINT.get(),
                HAS_DATA,
                (stack, level, entity, seed) ->
                {
                    CompoundTag tag = stack.getTagElement("blueprint_data");
                    return tag != null && !tag.isEmpty() ? 1 : 0;
                }
        );
    }
}