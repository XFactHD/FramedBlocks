package io.github.xfacthd.framedblocks.api.util;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.transfer.item.ItemResource;

/// A predicate capable of being tested against both an item resource and an item stack.
public interface ItemPredicate {
    /// {@return whether this predicate matches the given resource}
    ///
    /// @param resource The resource to test against
    boolean test(ItemResource resource);

    /// {@return whether this predicate matches the given stack}
    ///
    /// @param stack The stack to test against
    boolean test(ItemStack stack);

    /// {@return an item predicate testing against the given holder}
    ///
    /// @param item The holder to test against
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

    /// {@return an item predicate testing against the given item tag}
    ///
    /// @param tag The item tag to test against
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
