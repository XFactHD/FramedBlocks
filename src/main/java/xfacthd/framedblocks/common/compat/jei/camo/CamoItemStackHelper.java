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

import java.util.stream.Stream;

public final class CamoItemStackHelper
{
    private CamoItemStackHelper()
    {
    }


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

    public static Stream<ItemStack> dropCamo(ItemStack itemStack)
    {
        CamoList camos = itemStack.get(FBContent.DC_TYPE_CAMO_LIST);
        if (camos != null && !camos.isEmpty())
        {
            return camos.stream()
                    .filter(CamoContainer::canTriviallyConvertToItemStack)
                    .map(CamoContainerHelper::dropCamo);
        }
        return Stream.of();
    }
}
