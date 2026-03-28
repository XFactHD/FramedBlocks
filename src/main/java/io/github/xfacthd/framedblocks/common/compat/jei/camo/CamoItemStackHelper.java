package io.github.xfacthd.framedblocks.common.compat.jei.camo;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerHelper;
import io.github.xfacthd.framedblocks.api.camo.CamoCraftingHandler;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.util.ConfigView;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class CamoItemStackHelper {
    public static @Nullable CamoContainerFactory<?> getCamoContainerFactory(ItemStack itemStack) {
        CamoContainerFactory<?> factory = CamoContainerHelper.findCamoFactory(itemStack);
        if (factory == null) {
            return null;
        }
        CamoCraftingHandler<?> craftingHandler = factory.getCraftingHandler();
        if (craftingHandler == null || !craftingHandler.canApply(itemStack, ConfigView.Server.INSTANCE.shouldConsumeCamoItem())) {
            return null;
        }
        return factory;
    }

    public static @Nullable IFramedBlock getFramedBlock(ItemStack itemStack) {
        if (itemStack.getItem() instanceof BlockItem item && item.getBlock() instanceof IFramedBlock framedBlock) {
            return framedBlock;
        }
        return null;
    }

    public static boolean isDoubleFramedBlock(ItemStack itemStack) {
        IFramedBlock framedBlock = getFramedBlock(itemStack);
        return framedBlock != null && isDoubleFramedBlock(framedBlock);
    }

    public static boolean isDoubleFramedBlock(IFramedBlock framedBlock) {
        return framedBlock.getBlockType().consumesTwoCamosInCamoApplicationRecipe();
    }

    public static boolean isEmptyFramedBlock(ItemStack itemStack) {
        IFramedBlock framedBlock = getFramedBlock(itemStack);
        if (framedBlock == null) {
            return false;
        }
        CamoList camos = itemStack.getOrDefault(FBContent.DC_TYPE_CAMO_LIST, CamoList.EMPTY);
        return camos.isEmptyOrContentsEmpty();
    }

    public static List<ItemStack> dropCamo(ItemStack itemStack) {
        CamoList camos = itemStack.getOrDefault(FBContent.DC_TYPE_CAMO_LIST, CamoList.EMPTY);
        if (camos.isEmptyOrContentsEmpty()) {
            return List.of();
        }

        List<ItemStack> results = new ArrayList<>();
        for (CamoContainer<?, ?> camoContainer : camos) {
            if (!camoContainer.canTriviallyConvertToItemStack()) {
                return List.of();
            }
            ItemStack dropped = CamoContainerHelper.dropCamo(camoContainer);
            results.add(dropped);
        }
        return results;
    }

    private CamoItemStackHelper() { }
}
