package xfacthd.framedblocks.common.compat.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import xfacthd.framedblocks.common.FBContent;

@REIPluginClient
public final class FramedReiPlugin implements REIClientPlugin
{
    @Override
    public void registerCategories(CategoryRegistry registry)
    {
        registry.add(new FramingSawRecipeCategory());
        registry.addWorkstations(FramingSawRecipeCategory.SAW_CATEGORY, EntryStacks.of(FBContent.BLOCK_FRAMING_SAW.get()));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry)
    {
        registry.registerDisplayGenerator(FramingSawRecipeCategory.SAW_CATEGORY, new FramingSawDisplayGenerator());
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry)
    {
        registry.register(new FramingSawTransferHandler());
    }
}
