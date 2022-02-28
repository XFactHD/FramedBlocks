package xfacthd.framedblocks.client.util;

import net.minecraft.item.ItemModelsProperties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;

public class BlueprintPropertyOverride
{
    public static final ResourceLocation HAS_DATA = new ResourceLocation(FramedBlocks.MODID, "blueprint_override");

    public static void register()
    {
        ItemModelsProperties.register(
                FBContent.itemFramedBlueprint.get(),
                HAS_DATA,
                (stack, level, entity) ->
                {
                    CompoundNBT tag = stack.getTagElement("blueprint_data");
                    return tag != null && !tag.isEmpty() ? 1 : 0;
                }
        );
    }
}