package xfacthd.framedblocks.cmdtests.tests;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.FramedToolType;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class RecipePresent
{
    public static final String NAME = "RecipePresence";
    private static final String RESULT_MSG = "[" + NAME + "] Collected %,d recipes in %dms. ";
    private static final Lazy<Set<ItemLike>> EXCLUDED = Lazy.of(() -> Set.of(
            FBContent.blockFramedDoubleSlab.get().asItem(),
            FBContent.blockFramedDoublePanel.get().asItem()
    ));

    public static int checkForRecipePresence(CommandContext<CommandSourceStack> ctx)
    {
        Level level = ctx.getSource().getLevel();

        Stopwatch watch = Stopwatch.createStarted();
        MutableInt count = new MutableInt(0);

        RecipeManager recipeManager = level.getRecipeManager();
        Set<ItemLike> results = recipeManager.getRecipeIds()
                .filter(id -> id.getNamespace().equals(FramedConstants.MOD_ID))
                .map(recipeManager::byKey)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(CraftingRecipe.class::isInstance)
                .map(CraftingRecipe.class::cast)
                .peek(r -> count.increment())
                .map(CraftingRecipe::getResultItem)
                .map(ItemStack::getItem)
                .map(ItemLike.class::cast)
                .collect(Collectors.toSet());

        Set<ItemLike> diff = Sets.difference(collectAllItems(), results);
        watch.stop();
        long time = watch.elapsed(TimeUnit.MILLISECONDS);

        Component resultMsg = new TextComponent("No missing recipes found");
        ChatFormatting color = ChatFormatting.DARK_GREEN;

        int size = diff.size();
        if (size > 0)
        {
            StringBuilder testResult = new StringBuilder("Found the following items without recipe:");
            diff.forEach(item -> testResult.append(String.format("\n\t%s", item)));

            Component exportMsg = SpecialTestCommand.writeResultToFile("recipepresent", testResult.toString());
            resultMsg = new TextComponent("Found %d missing recipes. ".formatted(size)).append(exportMsg);
            color = ChatFormatting.DARK_RED;
        }

        resultMsg = new TextComponent(RESULT_MSG.formatted(count.intValue(), time))
                .withStyle(color)
                .append(resultMsg);
        ctx.getSource().sendSuccess(resultMsg, true);

        return Command.SINGLE_SUCCESS;
    }

    private static Set<ItemLike> collectAllItems()
    {
        Set<ItemLike> blockItems = Arrays.stream(BlockType.values())
                .filter(BlockType::hasBlockItem)
                .map(FBContent::byType)
                .map(Block::asItem)
                .map(ItemLike.class::cast)
                .collect(Collectors.toSet());

        Set<ItemLike> toolItems = Arrays.stream(FramedToolType.values())
                .map(FBContent::toolByType)
                .map(ItemLike.class::cast)
                .collect(Collectors.toSet());

        Set<ItemLike> allItems = new HashSet<>(blockItems);
        allItems.addAll(toolItems);
        allItems.removeAll(EXCLUDED.get());
        return allItems;
    }
}
