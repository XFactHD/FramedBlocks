package xfacthd.framedblocks.common.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.camo.*;
import xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import xfacthd.framedblocks.api.util.CamoList;
import xfacthd.framedblocks.common.FBContent;

import java.util.*;

public final class CamoApplicationRecipe extends CustomRecipe
{
    private final Ingredient copyTool;

    public CamoApplicationRecipe(CraftingBookCategory category, Ingredient copyTool)
    {
        super(category);
        this.copyTool = copyTool;
    }

    @Override
    public boolean matches(CraftingInput input, Level level)
    {
        if (input.width() != 2 || input.height() != 2 || !copyTool.test(input.getItem(1, 0))) return false;

        ItemStack blockStack = input.getItem(0, 0);
        if (!(blockStack.getItem() instanceof BlockItem item) || !(item.getBlock() instanceof IFramedBlock block))
        {
            return false;
        }

        ItemStack camoOneStack = input.getItem(0, 1);
        boolean camoOne = false;
        if (!camoOneStack.isEmpty())
        {
            CamoContainerFactory<?> factoryOne = CamoContainerHelper.findCamoFactory(camoOneStack);
            if (factoryOne == null || !factoryOne.canApplyInCraftingRecipe(camoOneStack))
            {
                return false;
            }
            camoOne = true;
        }

        ItemStack camoTwoStack = input.getItem(1, 1);
        boolean camoTwo = false;
        if (!camoTwoStack.isEmpty())
        {
            CamoContainerFactory<?> factoryTwo = CamoContainerHelper.findCamoFactory(camoTwoStack);
            if (factoryTwo == null || !factoryTwo.canApplyInCraftingRecipe(camoTwoStack))
            {
                return false;
            }
            camoTwo = true;
        }

        boolean doubleBlock = block.getBlockType().consumesTwoCamosInCamoApplicationRecipe();
        return doubleBlock ? (camoOne || camoTwo) : (camoOne ^ camoTwo);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries)
    {
        if (input.width() != 2 || input.height() != 2 || !copyTool.test(input.getItem(1, 0))) return ItemStack.EMPTY;

        ItemStack blockStack = input.getItem(0, 0);
        if (!(blockStack.getItem() instanceof BlockItem item) || !(item.getBlock() instanceof IFramedBlock block))
        {
            return ItemStack.EMPTY;
        }

        List<CamoContainer<?, ?>> camos = new ArrayList<>(2);

        ItemStack camoOneStack = input.getItem(0, 1);
        if (!camoOneStack.isEmpty())
        {
            CamoContainerFactory<?> factoryOne = CamoContainerHelper.findCamoFactory(camoOneStack);
            if (factoryOne == null || !factoryOne.canApplyInCraftingRecipe(camoOneStack))
            {
                return ItemStack.EMPTY;
            }
            camos.add(factoryOne.applyCamoInCraftingRecipe(camoOneStack));
        }
        else if (block.getBlockType().consumesTwoCamosInCamoApplicationRecipe())
        {
            camos.add(EmptyCamoContainer.EMPTY);
        }

        ItemStack camoTwoStack = input.getItem(1, 1);
        if (!camoTwoStack.isEmpty())
        {
            CamoContainerFactory<?> factoryTwo = CamoContainerHelper.findCamoFactory(camoTwoStack);
            if (factoryTwo == null || !factoryTwo.canApplyInCraftingRecipe(camoTwoStack))
            {
                return ItemStack.EMPTY;
            }
            camos.add(factoryTwo.applyCamoInCraftingRecipe(camoTwoStack));
        }

        ItemStack result = blockStack.copyWithCount(1);
        result.set(FBContent.DC_TYPE_CAMO_LIST, CamoList.of(camos));
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input)
    {
        NonNullList<ItemStack> remaining = super.getRemainingItems(input);
        if (remaining.get(1).isEmpty())
        {
            remaining.set(1, input.getItem(1, 0).copy());
        }

        ItemStack camoOneStack = input.getItem(0, 1);
        if (!camoOneStack.isEmpty())
        {
            CamoContainerFactory<?> factoryOne = CamoContainerHelper.findCamoFactory(camoOneStack);
            remaining.set(2, Objects.requireNonNull(factoryOne).getCraftingRemainder(camoOneStack));
        }

        ItemStack camoTwoStack = input.getItem(1, 1);
        if (!camoTwoStack.isEmpty())
        {
            CamoContainerFactory<?> factoryTwo = CamoContainerHelper.findCamoFactory(camoTwoStack);
            remaining.set(3, Objects.requireNonNull(factoryTwo).getCraftingRemainder(camoTwoStack));
        }

        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return width >= 2 && height >= 2;
    }

    public Ingredient getCopyTool()
    {
        return copyTool;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return FBContent.RECIPE_SERIALIZER_APPLY_CAMO.value();
    }
}
