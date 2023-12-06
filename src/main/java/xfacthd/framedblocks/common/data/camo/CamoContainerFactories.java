package xfacthd.framedblocks.common.data.camo;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.common.FBContent;

import java.util.IdentityHashMap;
import java.util.Map;

public final class CamoContainerFactories
{
    private static final Map<Item, CamoContainerFactory> itemToFactory = new IdentityHashMap<>();

    public static void registerCamoFactories()
    {
        FBContent.CAMO_CONTAINER_FACTORY_REGISTRY
                .entrySet()
                .stream()
                .filter(e -> !e.getKey().location().getNamespace().equals(FramedConstants.MOD_ID))
                .map(Map.Entry::getValue)
                .forEach(factory -> factory.registerTriggerItems(item ->
                {
                    if (itemToFactory.containsKey(item))
                    {
                        throw new IllegalArgumentException(String.format("Item %s is already registered!", item));
                    }
                    itemToFactory.put(item, factory);
                }));
    }

    public static CamoContainerFactory getFactory(ItemStack stack)
    {
        if (itemToFactory.containsKey(stack.getItem()))
        {
            return itemToFactory.get(stack.getItem());
        }
        if (stack.getCapability(Capabilities.FluidHandler.ITEM) != null)
        {
            return FBContent.FACTORY_FLUID.value();
        }
        return FBContent.FACTORY_BLOCK.value();
    }



    private CamoContainerFactories() { }
}
