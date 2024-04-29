package xfacthd.framedblocks.common.compat.ae2;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import xfacthd.framedblocks.api.util.Utils;

import java.util.function.BiConsumer;

final class FramingSawPatternEncoding
{
    public static final String KEY_INPUT = "input";
    public static final String KEY_RESULT = "result";
    private static final String KEY_ADDITIVES = "additives";

    public static ItemStack getItem(CompoundTag tag, String key)
    {
        return BuiltInRegistries.ITEM.get(new ResourceLocation(tag.getString(key))).getDefaultInstance();
    }

    public static ItemStack tryGetItem(CompoundTag tag, String key, BiConsumer<AEKey, Long> tooltip)
    {
        if (!tag.contains(key)) return ItemStack.EMPTY;

        String value = tag.getString(key);
        ResourceLocation itemId = ResourceLocation.tryParse(value);
        if (itemId == null) return ItemStack.EMPTY;

        Item item = BuiltInRegistries.ITEM.get(itemId);
        if (item != Items.AIR)
        {
            tooltip.accept(AEItemKey.of(item), 1L);
            return item.getDefaultInstance();
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack[] getAdditives(CompoundTag tag, int count)
    {
        ListTag list = tag.getList(KEY_ADDITIVES, Tag.TAG_COMPOUND);
        ItemStack[] additives = new ItemStack[count];
        for (int i = 0; i < additives.length; i++)
        {
            // FIXME: adapt to whatever AE2 uses to store encoded patterns
            //additives[i] = ItemStack.of(list.getCompound(i));
        }
        return additives;
    }

    public static void tryGetAdditives(CompoundTag tag, BiConsumer<AEKey, Long> tooltip)
    {
        if (!tag.contains(KEY_ADDITIVES, Tag.TAG_LIST)) return;

        ListTag list = tag.getList(KEY_ADDITIVES, Tag.TAG_COMPOUND);
        if (list.isEmpty()) return;

        for (int i = 0; i < list.size(); i++)
        {
            // FIXME: adapt to whatever AE2 uses to store encoded patterns
            /*ItemStack stack = ItemStack.of(list.getCompound(i));
            if (stack.isEmpty()) continue;

            tooltip.accept(AEItemKey.of(stack), (long) stack.getCount());*/
        }
    }

    public static void encodeFramingSawPattern(Level level, ItemStack stack, ItemStack input, ItemStack[] additives, ItemStack output)
    {
        // FIXME: adapt to whatever AE2 uses to store encoded patterns
        /*tag.putString(KEY_INPUT, Utils.getKeyOrThrow(input.getItemHolder()).location().toString());
        if (additives.length > 0)
        {
            ListTag list = new ListTag();
            for (ItemStack additive : additives)
            {
                list.add(additive.save(new CompoundTag()));
            }
            tag.put(KEY_ADDITIVES, list);
        }
        tag.putString(KEY_RESULT, Utils.getKeyOrThrow(output.getItemHolder()).location().toString());*/
    }



    private FramingSawPatternEncoding() { }
}
