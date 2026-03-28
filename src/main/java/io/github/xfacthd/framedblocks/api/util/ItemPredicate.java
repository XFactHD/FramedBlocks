package io.github.xfacthd.framedblocks.api.util;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.transfer.item.ItemResource;

public interface ItemPredicate {
    boolean test(ItemResource resource);

    boolean test(ItemStack stack);

    static ItemPredicate ofItem(Holder<? extends ItemLike> item) {
        return new ItemPredicate() {
            @Override
            public boolean test(ItemResource resource) {
                return resource.is(item.value());
            }

            @Override
            public boolean test(ItemStack stack) {
                return stack.is(item.value().asItem());
            }
        };
    }

    static ItemPredicate ofTag(TagKey<Item> tag) {
        return new ItemPredicate() {
            @Override
            public boolean test(ItemResource resource) {
                return resource.is(tag);
            }

            @Override
            public boolean test(ItemStack stack) {
                return stack.is(tag);
            }
        };
    }
}
