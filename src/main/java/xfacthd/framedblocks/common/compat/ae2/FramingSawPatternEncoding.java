package xfacthd.framedblocks.common.compat.ae2;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import xfacthd.framedblocks.api.util.Utils;

final class FramingSawPatternEncoding
{
    private static final String KEY_INPUT = "input";
    private static final String KEY_RESULT = "result";
    private static final String KEY_ADDITIVES = "additives";

    public static ItemStack getInput(CompoundTag tag)
    {
        return BuiltInRegistries.ITEM.get(new ResourceLocation(tag.getString(KEY_INPUT))).getDefaultInstance();
    }

    public static ItemStack getResult(CompoundTag tag)
    {
        return BuiltInRegistries.ITEM.get(new ResourceLocation(tag.getString(KEY_RESULT))).getDefaultInstance();
    }

    public static ItemStack[] getAdditives(CompoundTag tag)
    {
        ListTag list = tag.getList(KEY_ADDITIVES, Tag.TAG_COMPOUND);
        ItemStack[] additives = new ItemStack[list.size()];
        for (int i = 0; i < additives.length; i++)
        {
            additives[i] = ItemStack.of(list.getCompound(i));
        }
        return additives;
    }

    public static void encodeFramingSawPattern(CompoundTag tag, ItemStack input, ItemStack[] additives, ItemStack output)
    {
        tag.putString(KEY_INPUT, Utils.getKeyOrThrow(input.getItemHolder()).location().toString());
        if (additives.length > 0)
        {
            ListTag list = new ListTag();
            for (ItemStack additive : additives)
            {
                list.add(additive.save(new CompoundTag()));
            }
            tag.put(KEY_ADDITIVES, list);
        }
        tag.putString(KEY_RESULT, Utils.getKeyOrThrow(output.getItemHolder()).location().toString());
    }



    private FramingSawPatternEncoding() { }
}
