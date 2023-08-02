package xfacthd.framedblocks.client.util;

import net.minecraft.world.item.ItemStack;
import xfacthd.framedblocks.common.compat.emi.EmiCompat;
import xfacthd.framedblocks.common.compat.jei.JeiCompat;

public enum RecipeViewer
{
    JEI(JeiCompat::isShowRecipePressed, JeiCompat::handleShowRecipeRequest),
    //REI(ReiCompat::isShowRecipePressed, ReiCompat::handleShowRecipeRequest), // TODO: implement REI compat
    EMI(EmiCompat::isShowRecipePressed, EmiCompat::handleShowRecipeRequest);

    private final ShowRecipeKeyTest showKeyTest;
    private final RecipeShower recipeShower;

    RecipeViewer(ShowRecipeKeyTest showKeyTest, RecipeShower recipeShower)
    {
        this.showKeyTest = showKeyTest;
        this.recipeShower = recipeShower;
    }

    public boolean isShowRecipePressed(int keyCode, int scanCode)
    {
        return showKeyTest.isShowRecipePressed(keyCode, scanCode);
    }

    public boolean handleShowRecipeRequest(ItemStack resultStack)
    {
        return recipeShower.handleShowRecipeRequest(resultStack);
    }



    public static RecipeViewer get()
    {
        // EMI actively disables JEI, so it has to be preferred in case both are installed
        if (EmiCompat.isLoaded())
        {
            return EMI;
        }
        if (JeiCompat.isLoaded())
        {
            return JEI;
        }
        //if (ReiCompat.isLoaded())
        //{
        //    return REI;
        //}
        return null;
    }



    private interface ShowRecipeKeyTest
    {
        boolean isShowRecipePressed(int keyCode, int scanCode);
    }

    private interface RecipeShower
    {
        boolean handleShowRecipeRequest(ItemStack resultStack);
    }
}
