package io.github.xfacthd.framedblocks.selftest.tests;

import com.google.common.collect.Sets;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipe;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.selftest.SelfTestReporter;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class RecipePresence {
    public static void checkRecipePresence(SelfTestReporter reporter, Level level) {
        reporter.startTest("recipe presence");

        MutableInt craftCount = new MutableInt(0);
        MutableInt sawCount = new MutableInt(0);

        RecipeManager recipeManager = ((ServerLevel) level).recipeAccess();
        List<? extends Recipe<?>> fbRecipes = recipeManager.getRecipes()
                .stream()
                .filter(holder -> holder.id().identifier().getNamespace().equals(FramedConstants.MOD_ID))
                .map(RecipeHolder::value)
                .toList();

        Set<ItemLike> craftResults = fbRecipes.stream()
                .filter(CraftingRecipe.class::isInstance)
                .map(CraftingRecipe.class::cast)
                .peek(_ -> craftCount.increment())
                .map(RecipePresence::unpackResult)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(ItemStack::getItem)
                .map(ItemLike.class::cast)
                .collect(Collectors.toSet());

        Set<ItemLike> sawResults = fbRecipes.stream()
                .filter(FramingSawRecipe.class::isInstance)
                .map(FramingSawRecipe.class::cast)
                .peek(_ -> sawCount.increment())
                .map(FramingSawRecipe::getResultStack)
                .map(ItemStack::getItem)
                .map(ItemLike.class::cast)
                .collect(Collectors.toSet());

        Set<ItemLike> blockItems = collectBlockTypedItems();
        Set<ItemLike> craftDiff = Sets.difference(blockItems, craftResults);
        Set<ItemLike> sawDiff = Sets.difference(blockItems, sawResults);
        for (ItemLike item : sawDiff) {
            if (craftDiff.contains(item)) {
                reporter.warn("Block {} is uncraftable", item);
            } else {
                reporter.warn("Block {} has no saw recipe", item);
            }
        }

        Set<ItemLike> miscCraftDiff = Sets.difference(collectMiscItems(), craftResults);
        for (ItemLike item : miscCraftDiff) {
            reporter.warn("Item {} is uncraftable", item);
        }

        reporter.endTest();
    }

    private static Optional<ItemStack> unpackResult(CraftingRecipe recipe) {
        return switch (recipe) {
            case ShapedRecipe shaped -> Optional.of(shaped.result.create());
            case ShapelessRecipe shapeless -> Optional.of(shapeless.result.create());
            default -> Optional.empty();
        };
    }

    private static Set<ItemLike> collectMiscItems() {
        return FBContent.getRegisteredItems()
                .stream()
                .map(Holder::value)
                .filter(item -> !(item instanceof BlockItem blockItem) || !(blockItem.getBlock() instanceof IFramedBlock))
                .collect(Collectors.toSet());
    }

    private static Set<ItemLike> collectBlockTypedItems() {
        return Arrays.stream(BlockType.values())
                .filter(BlockType::hasBlockItem)
                .map(FBContent::byType)
                .map(Block::asItem)
                .map(ItemLike.class::cast)
                .collect(Collectors.toSet());
    }

    private RecipePresence() { }
}
