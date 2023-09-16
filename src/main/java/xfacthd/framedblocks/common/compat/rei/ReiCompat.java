package xfacthd.framedblocks.common.compat.rei;

import me.shedaniel.rei.api.client.config.ConfigObject;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.util.RecipeViewer;

public final class ReiCompat
{
    public static final ResourceLocation SAW_ID = Utils.rl("framing_saw");

    private static boolean loadedClient = false;

    public static void init()
    {
        if (FMLEnvironment.dist.isClient() && ModList.get().isLoaded("roughlyenoughitems"))
        {
            GuardedAccess.init();
            loadedClient = true;
        }
    }

    public static boolean isLoaded()
    {
        return loadedClient;
    }

    public static RecipeViewer.LookupTarget isShowRecipePressed(int keyCode, int scanCode)
    {
        if (loadedClient)
        {
            return GuardedAccess.isShowRecipePressed(keyCode, scanCode);
        }
        return null;
    }

    public static boolean handleShowRecipeRequest(ItemStack result, RecipeViewer.LookupTarget target)
    {
        if (loadedClient)
        {
            return GuardedAccess.handleButtonRecipeRequest(result, target);
        }
        return false;
    }



    static final class GuardedAccess
    {
        public static void init()
        {

        }

        public static RecipeViewer.LookupTarget isShowRecipePressed(int keyCode, int scanCode)
        {
            ConfigObject cfg = ConfigObject.getInstance();
            if (cfg.getRecipeKeybind().matchesKey(keyCode, scanCode))
            {
                return RecipeViewer.LookupTarget.RECIPE;
            }
            if (cfg.getUsageKeybind().matchesKey(keyCode, scanCode))
            {
                return RecipeViewer.LookupTarget.USAGE;
            }
            return null;
        }

        private static boolean handleButtonRecipeRequest(ItemStack result, RecipeViewer.LookupTarget target)
        {
            ViewSearchBuilder builder = ViewSearchBuilder.builder();
            switch (target)
            {
                case RECIPE -> builder.addRecipesFor(EntryStacks.of(result));
                case USAGE -> builder.addUsagesFor(EntryStacks.of(result));
            }
            return builder.open();
        }



        private GuardedAccess() { }
    }



    private ReiCompat() { }
}
