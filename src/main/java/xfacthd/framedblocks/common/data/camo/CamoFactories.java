package xfacthd.framedblocks.common.data.camo;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.common.FBContent;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CamoFactories
{
    private static final Map<Item, CamoContainer.Factory> itemToFactory = new HashMap<>();
    private static boolean locked = false;

    public static void registerCamoFactory(Item item, CamoContainer.Factory factory)
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
        if (stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent())
        {
            return FBContent.factoryFluid.get();
        }
        return FBContent.factoryBlock.get();
    }

    @SubscribeEvent
    public static void onLoadComplete(final FMLLoadCompleteEvent event) { locked = true; }
}
