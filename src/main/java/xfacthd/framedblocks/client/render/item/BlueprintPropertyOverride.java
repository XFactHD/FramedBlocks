package xfacthd.framedblocks.client.render.item;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import xfacthd.framedblocks.api.blueprint.BlueprintData;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;

public final class BlueprintPropertyOverride
{
    public static final ResourceLocation HAS_DATA = Utils.rl("blueprint_override");

    private BlueprintPropertyOverride() { }

    public static void register()
    {
        ItemProperties.register(
                FBContent.ITEM_FRAMED_BLUEPRINT.value(),
                HAS_DATA,
                (stack, level, entity, seed) ->
                {
                    BlueprintData blueprintData = stack.getOrDefault(FBContent.DC_TYPE_BLUEPRINT_DATA, BlueprintData.EMPTY);
                    return blueprintData.isEmpty() ? 0 : 1;
                }
        );
    }
}