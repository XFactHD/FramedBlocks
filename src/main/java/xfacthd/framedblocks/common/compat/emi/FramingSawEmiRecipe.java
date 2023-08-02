package xfacthd.framedblocks.common.compat.emi;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.client.screen.FramingSawScreen;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeCache;

import java.util.List;

public final class FramingSawEmiRecipe extends BasicEmiRecipe
{
    private static final int WIDTH = 120;
    private static final int HEIGHT = 43;
    private static final int WARNING_X = 38;
    private static final int WARNING_Y = 3;
    private static final int WARNING_SIZE = 16;
    private static final float WARNING_SCALE = .75F;
    private static final int WARNING_DRAW_SIZE = (int) (WARNING_SIZE * WARNING_SCALE);
    private static final EmiTexture TEXTURE_WARNING = new EmiTexture(
            FramingSawScreen.WARNING_ICON, 8, 8, WARNING_DRAW_SIZE, WARNING_DRAW_SIZE, WARNING_SIZE, WARNING_SIZE, 32, 32
    );

    private final FramingSawRecipe recipe;
    // Only enumerate recipes with the framed cube as input when the recipes of an item are requested
    // All other recipes are only supposed to be shown when the recipes accepting the item are requested to prevent clutter
    private final boolean showOnRecipeRequest;
    private final boolean inputWithAdditives;

    private FramingSawEmiRecipe(
            FramingSawRecipe recipe, ResourceLocation id, EmiStack input, List<EmiIngredient> additives, EmiStack output
    )
    {
        super(FramedEmiPlugin.SAW_CATEGORY, id, WIDTH, HEIGHT);
        this.recipe = recipe;
        this.showOnRecipeRequest = input.getItemStack().is(FBContent.BLOCK_FRAMED_CUBE.get().asItem());
        this.inputWithAdditives = FramingSawRecipeCache.get(true).containsAdditive(input.getItemStack().getItem());
        this.inputs.add(input);
        this.inputs.addAll(additives);
        this.outputs.add(output);
        this.catalysts.add(FramedEmiPlugin.SAW_WORKSTATION);
    }

    @Override
    public List<EmiStack> getOutputs()
    {
        return showOnRecipeRequest ? super.getOutputs() : List.of();
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        widgets.addSlot(inputs.get(0), 19, 1);
        for (int i = 0; i < FramingSawRecipe.MAX_ADDITIVE_COUNT; i++)
        {
            int x = 1 + (i * 18);
            int idx = i + 1;
            if (idx < inputs.size())
            {
                widgets.addSlot(inputs.get(idx), x, 24);
            }
            else
            {
                widgets.addTexture(EmiTexture.SLOT, x, 24);
            }
        }
        widgets.addSlot(outputs.get(0), 93, 9).large(true).recipeContext(this);

        widgets.addTexture(EmiTexture.EMPTY_ARROW, 60, 12);
        if (inputWithAdditives)
        {
            widgets.addTexture(TEXTURE_WARNING, WARNING_X, WARNING_Y);
            widgets.add(widgets.addTooltipText(
                    List.of(FramingSawScreen.TOOLTIP_LOOSE_ADDITIVE),
                    WARNING_X, WARNING_Y,
                    WARNING_DRAW_SIZE, WARNING_DRAW_SIZE
            ));
        }
    }

    public IBlockType getResultType()
    {
        return recipe.getResultType();
    }

    public FramingSawRecipe getRecipe()
    {
        return recipe;
    }

    public ItemStack getOutputInternal()
    {
        return outputs.get(0).getItemStack();
    }



    public static FramingSawEmiRecipe make(FramingSawRecipe recipe, EmiStack input, List<EmiIngredient> additives, EmiStack output)
    {
        boolean showOnRecipeRequest = input.getItemStack().is(FBContent.BLOCK_FRAMED_CUBE.get().asItem());
        ResourceLocation id = showOnRecipeRequest ? recipe.getId() : null;
        return new FramingSawEmiRecipe(recipe, id, input, additives, output);
    }
}
