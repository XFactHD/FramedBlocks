package xfacthd.framedblocks.common.compat.jei.camo;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import xfacthd.framedblocks.api.util.Utils;

/**
 * Empty tags to help with serialization of the fake recipes.
 * These gets replaced dynamically for displaying in JEI.
 */
public final class JeiCamoTags
{
    private static final TagKey<Item> CAMO_BLOCK_EXAMPLES = Utils.itemTag("jei_camo_block_examples");
    private static final TagKey<Item> ALL_FRAMES = Utils.itemTag("jei_all_frames");
    private static final TagKey<Item> DOUBLE_FRAMES = Utils.itemTag("jei_double_frames");

    private JeiCamoTags()
    {
    }

    public static TagKey<Item> getCamoBlockExamplesTag()
    {
        return CAMO_BLOCK_EXAMPLES;
    }

    public static TagKey<Item> getAllFramesTag()
    {
        return ALL_FRAMES;
    }

    public static TagKey<Item> getDoubleFramesTag()
    {
        return DOUBLE_FRAMES;
    }
}
