package xfacthd.framedblocks.cmdtests.tests;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.Lazy;
import org.apache.commons.lang3.mutable.MutableInt;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.cmdtests.SpecialTestCommand;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.FramedToolType;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class RecipePresent
{
    public static final String NAME = "RecipePresence";
    private static final String RESULT_MSG = "[" + NAME + "] Collected %,d crafting recipes and %,d saw recipes (%,d disabled) in %dms. ";
    private static final Lazy<Set<ItemLike>> EXCLUDED = Lazy.of(() -> Set.of(
            FBContent.blockFramedDoubleSlab.get().asItem(),
            FBContent.blockFramedDoublePanel.get().asItem()
    ));

    public static int checkForRecipePresence(CommandContext<CommandSourceStack> ctx)
    {
        Level level = ctx.getSource().getLevel();

        Stopwatch watch = Stopwatch.createStarted();
        MutableInt craftCount = new MutableInt(0);
        MutableInt sawCount = new MutableInt(0);
        MutableInt sawDisabledCount = new MutableInt(0);

        RecipeManager recipeManager = level.getRecipeManager();
        List<? extends Recipe<?>> fbRecipes = recipeManager.getRecipeIds()
                .filter(id -> id.getNamespace().equals(FramedConstants.MOD_ID))
                .map(recipeManager::byKey)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        Set<ItemLike> craftResults = fbRecipes.stream()
                .filter(CraftingRecipe.class::isInstance)
                .map(CraftingRecipe.class::cast)
                .peek(r -> craftCount.increment())
                .map(r -> r.getResultItem(level.registryAccess()))
                .map(ItemStack::getItem)
                .map(ItemLike.class::cast)
                .collect(Collectors.toSet());

        Set<ItemLike> sawResults = fbRecipes.stream()
                .filter(FramingSawRecipe.class::isInstance)
                .map(FramingSawRecipe.class::cast)
                .peek(r ->
                {
                    sawCount.increment();
                    if (r.isDisabled())
                    {
                        sawDisabledCount.increment();
                    }
                })
                .map(FramingSawRecipe::getResult)
                .map(ItemStack::getItem)
                .map(ItemLike.class::cast)
                .collect(Collectors.toSet());

        Set<ItemLike> craftDiff = Sets.difference(collectAllItems(), craftResults);
        Set<ItemLike> sawDiff = Sets.difference(collectBlockTypedItems(), sawResults);
        watch.stop();
        long time = watch.elapsed(TimeUnit.MILLISECONDS);

        Component resultMsg = Component.literal("No missing recipes found");
        ChatFormatting color = ChatFormatting.DARK_GREEN;

        StringBuilder testResult = new StringBuilder();

        int craftSize = craftDiff.size();
        if (craftSize > 0)
        {
            testResult.append("Found the following items without crafting recipe:");
            craftDiff.forEach(item -> testResult.append(String.format("\n\t%s", item)));
        }

        int sawSize = sawDiff.size();
        if (sawSize > 0)
        {
            if (!testResult.isEmpty())
            {
                testResult.append("\n\n");
            }
            testResult.append("Found the following items without recipe:");
            sawDiff.forEach(item -> testResult.append(String.format("\n\t%s", item)));
        }

        if (!testResult.isEmpty())
        {
            Component exportMsg = SpecialTestCommand.writeResultToFile("recipepresent", testResult.toString());
            resultMsg = Component.literal(
                    "Found %d missing crafting recipes and %d missing saw recipes. ".formatted(craftSize, sawSize)
            ).append(exportMsg);
            color = ChatFormatting.DARK_RED;
        }

        resultMsg = Component.literal(RESULT_MSG.formatted(
                    craftCount.intValue(), sawCount.intValue(), sawDisabledCount.intValue(), time
                ))
                .withStyle(color)
                .append(resultMsg);

        Component resultMsgFinal = resultMsg;
        ctx.getSource().sendSuccess(() -> resultMsgFinal, true);

        return Command.SINGLE_SUCCESS;
    }

    private static Set<ItemLike> collectAllItems()
    {
        Set<ItemLike> toolItems = Arrays.stream(FramedToolType.values())
                .map(FBContent::toolByType)
                .map(ItemLike.class::cast)
                .collect(Collectors.toSet());

        Set<ItemLike> allItems = new HashSet<>(collectBlockTypedItems());
        allItems.addAll(toolItems);
        allItems.removeAll(EXCLUDED.get());
        return allItems;
    }

    private static Set<ItemLike> collectBlockTypedItems()
    {
        Set<ItemLike> blockItems = Arrays.stream(BlockType.values())
                .filter(BlockType::hasBlockItem)
                .map(FBContent::byType)
                .map(Block::asItem)
                .map(ItemLike.class::cast)
                .collect(Collectors.toSet());

        blockItems.removeAll(EXCLUDED.get());
        return blockItems;
    }

    private RecipePresent() { }
}
