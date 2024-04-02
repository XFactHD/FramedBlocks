package xfacthd.framedblocks.api.camo;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public interface TriggerRegistrar
{
    /**
     * Register the given {@link Item} as a valid applicator for the {@link CamoContainerFactory}
     * this registrar is given to
     */
    void registerApplicationItem(Item item);

    /**
     * Register the given {@link Predicate} to dynamically check whether the {@link ItemStack} held by the player
     * is a valid applicator for {@link CamoContainerFactory} this registrar is given to
     */
    void registerApplicationPredicate(Predicate<ItemStack> predicate);

    /**
     * Register the given {@link Item} as a valid removal tool for the {@link CamoContainer}s produced by
     * the {@link CamoContainerFactory} this registrar is given to
     */
    void registerRemovalItem(Item item);

    /**
     * Register the given {@link Predicate} to dynamically check whether the {@link ItemStack} held by the player
     * is a valid removal tool for the {@link CamoContainer}s produced by the {@link CamoContainerFactory} this
     * registrar is given to
     */
    void registerRemovalPredicate(Predicate<ItemStack> predicate);
}
