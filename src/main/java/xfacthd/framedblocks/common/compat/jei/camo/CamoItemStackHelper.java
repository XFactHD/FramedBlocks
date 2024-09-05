package xfacthd.framedblocks.common.compat.jei.camo;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.api.camo.CamoContainerHelper;
import xfacthd.framedblocks.api.util.CamoList;
import xfacthd.framedblocks.common.FBContent;

import java.util.ArrayList;
import java.util.List;

public final class CamoItemStackHelper
{
    @Nullable
    public static CamoContainerFactory<?> getCamoContainerFactory(ItemStack itemStack)
    {
        CamoContainerFactory<?> factory = CamoContainerHelper.findCamoFactory(itemStack);
        if (factory == null || !factory.canApplyInCraftingRecipe(itemStack))
        {
            return null;
        }
        return factory;
    }

    @Nullable
    public static IFramedBlock getFramedBlock(ItemStack itemStack)
    {
        if (itemStack.getItem() instanceof BlockItem item && item.getBlock() instanceof IFramedBlock framedBlock)
        {
            return framedBlock;
        }
        return null;
    }

    public static boolean isDoubleFramedBlock(ItemStack itemStack)
    {
        IFramedBlock framedBlock = getFramedBlock(itemStack);
        return framedBlock != null && isDoubleFramedBlock(framedBlock);
    }

    public static boolean isDoubleFramedBlock(IFramedBlock framedBlock)
    {
        return framedBlock.getBlockType().consumesTwoCamosInCamoApplicationRecipe();
    }

    public static boolean isEmptyFramedBlock(ItemStack itemStack)
    {
        IFramedBlock framedBlock = getFramedBlock(itemStack);
        if (framedBlock == null)
        {
            return false;
        }
        CamoList camos = itemStack.getOrDefault(FBContent.DC_TYPE_CAMO_LIST, CamoList.EMPTY);
        return camos.isEmptyOrContentsEmpty();
    }

    public static List<ItemStack> dropCamo(ItemStack itemStack)
    {
        CamoList camos = itemStack.getOrDefault(FBContent.DC_TYPE_CAMO_LIST, CamoList.EMPTY);
        if (camos.isEmptyOrContentsEmpty())
        {
            return List.of();
        }

        List<ItemStack> results = new ArrayList<>();
        for (CamoContainer<?, ?> camoContainer : camos)
        {
            if (!camoContainer.canTriviallyConvertToItemStack())
            {
                return List.of();
            }
            ItemStack dropped = CamoContainerHelper.dropCamo(camoContainer);
            results.add(dropped);
        }
        return results;
    }

    private CamoItemStackHelper() { }
}
