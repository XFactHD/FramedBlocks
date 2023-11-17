package xfacthd.framedblocks.cmdtests.tests;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.MutableInt;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.cmdtests.SpecialTestCommand;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class RecipeCollisions
{
    public static final String NAME = "RecipeCollisions";
    private static final String MSG_PREFIX = "[" + NAME + "] ";
    private static final String PROGRESS_MSG = MSG_PREFIX + "%,d";
    private static final String RESULT_MSG = MSG_PREFIX + "Tested %,d combinations in %dms. ";

    @SuppressWarnings("unchecked")
    public static void checkForRecipeCollisions(CommandContext<CommandSourceStack> ctx, Consumer<Component> msgQueueAppender)
    {
        Level level = ctx.getSource().getLevel();
        Player player = Objects.requireNonNull(ctx.getSource().getPlayer());

        Stopwatch watch = Stopwatch.createStarted();

        RecipeManager recipeManager = level.getRecipeManager();
        List<RecipeHolder<CraftingRecipe>> recipes = recipeManager.getRecipeIds()
                .filter(id -> id.getNamespace().equals(FramedConstants.MOD_ID))
                .map(recipeManager::byKey)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(h -> h.value() instanceof CraftingRecipe)
                .map(h -> (RecipeHolder<CraftingRecipe>) h)
                .toList();

        TransientCraftingContainer container = new TransientCraftingContainer(player.containerMenu, 3, 3);
        NonNullList<ItemStack> items = Objects.requireNonNull(Utils.getPrivateValue(TransientCraftingContainer.class, container, "items"));

        Multimap<ResourceLocation, ResourceLocation> collisions = ArrayListMultimap.create();
        MutableInt combinations = new MutableInt(0);
        recipes.forEach(holder ->
        {
            CraftingRecipe recipe = holder.value();
            List<Ingredient> ingredients = recipe.getIngredients();

            for (int x = 0; x < 3; x++)
            {
                for (int y = 0; y < 3; y++)
                {
                    int destIdx = x + (y * 3);
                    int srcIdx = destIdx;
                    if (recipe instanceof ShapedRecipe shaped)
                    {
                        if (x >= shaped.getRecipeWidth())
                        {
                            items.set(destIdx, ItemStack.EMPTY);
                            continue;
                        }

                        srcIdx = x + (y * shaped.getRecipeWidth());
                    }

                    if (srcIdx < ingredients.size() && !ingredients.get(srcIdx).isEmpty())
                    {
                        items.set(destIdx, ingredients.get(srcIdx).getItems()[0]);
                    }
                    else
                    {
                        items.set(destIdx, ItemStack.EMPTY);
                    }
                }
            }

            recipes.stream()
                    .filter(other -> other.value() != recipe)
                    .forEach(other ->
                    {
                        if (other.value().matches(container, level))
                        {
                            collisions.put(holder.id(), other.id());
                        }

                        int value = combinations.incrementAndGet();
                        if (value % 1000000 == 0)
                        {
                            msgQueueAppender.accept(Component.literal(PROGRESS_MSG.formatted(value)));
                        }
                    });
        });

        watch.stop();
        long time = watch.elapsed(TimeUnit.MILLISECONDS);

        Component resultMsg = Component.literal("No collisions found");
        ChatFormatting color = ChatFormatting.DARK_GREEN;

        if (!collisions.isEmpty())
        {
            StringBuilder testResult = new StringBuilder("Found the following recipe collisions:");
            collisions.forEach((r1, r2) ->
                    testResult.append(String.format("\n\t%s collides with %s", r1, r2))
            );

            Component exportMsg = SpecialTestCommand.writeResultToFile("recipecollisions", testResult.toString());
            resultMsg = Component.literal("Found %d collisions. ".formatted(collisions.size())).append(exportMsg);
            color = ChatFormatting.DARK_RED;
        }

        resultMsg = Component.literal(RESULT_MSG.formatted(combinations.intValue(), time))
                .withStyle(color)
                .append(resultMsg);
        msgQueueAppender.accept(resultMsg);
    }



    private RecipeCollisions() { }
}
