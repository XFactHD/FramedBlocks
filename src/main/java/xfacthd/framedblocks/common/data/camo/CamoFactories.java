package xfacthd.framedblocks.common.data.camo;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.common.FBContent;

import java.util.HashMap;
import java.util.Map;

public final class CamoFactories
{
    private static final Map<Item, CamoContainer.Factory> itemToFactory = new HashMap<>();
    private static boolean locked = false;

    public static synchronized void registerCamoFactory(Item item, CamoContainer.Factory factory)
    {
        if (locked)
        {
            throw new IllegalStateException("Factory registry is locked!");
        }

        if (itemToFactory.containsKey(item))
        {
            throw new IllegalArgumentException(String.format("Item %s is already registered!", item));
        }

        itemToFactory.put(item, factory);
    }

    public static CamoContainer.Factory getFactory(ItemStack stack)
    {
        if (itemToFactory.containsKey(stack.getItem()))
        {
            return itemToFactory.get(stack.getItem());
        }
        if (stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent())
        {
            return FBContent.factoryFluid.get();
        }
        return FBContent.factoryBlock.get();
    }

    public static void lock() { locked = true; }



    private CamoFactories() { }
}
