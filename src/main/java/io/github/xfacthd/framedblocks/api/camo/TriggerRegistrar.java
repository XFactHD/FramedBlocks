package io.github.xfacthd.framedblocks.api.camo;

import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

/// Registrar for camo application and removal items/predicates for a camo container.
public interface TriggerRegistrar {
    /// Default predicate for removal of block camos. Should be preferred over specifically using the hammer for mod compatibility.
    Predicate<ItemStack> DEFAULT_REMOVAL = stack -> stack.is(FramedConstants.Objects.FRAMED_HAMMER) || stack.canPerformAction(FramedConstants.ItemAbilities.ACTION_WRENCH_EMPTY);

    /// Register the given item as a valid applicator for the camo container factory
    /// this registrar is given to.
    ///
    /// @param item The item to register
    void registerApplicationItem(Item item);

    /// Register the given predicate to dynamically check whether the stack held by the player
    /// is a valid applicator for the camo container factory this registrar is given to.
    ///
    /// @param predicate The predicate to register
    void registerApplicationPredicate(Predicate<ItemStack> predicate);

    /// Register the given item as a valid removal tool for the camo containers produced by
    /// the camo container factory this registrar is given to.
    ///
    /// @param item The item to register
    void registerRemovalItem(Item item);

    /// Register the given predicate to dynamically check whether the stack held by the player
    /// is a valid removal tool for the camo containers produced by the camo container factory this
    /// registrar is given to.
    ///
    /// @param predicate The predicate to register
    void registerRemovalPredicate(Predicate<ItemStack> predicate);
}
