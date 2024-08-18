package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.crafting.CamoApplicationRecipe;
import xfacthd.framedblocks.common.datagen.builders.recipe.ExtShapedRecipeBuilder;
import xfacthd.framedblocks.common.datagen.builders.recipe.ExtShapelessRecipeBuilder;

import java.util.concurrent.CompletableFuture;

public final class FramedRecipeProvider extends RecipeProvider
{
    public FramedRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> providerFuture)
    {
        super(output, providerFuture);
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer)
    {
        shapedBuildingBlock(FBContent.BLOCK_FRAMED_CUBE.value(), 4)
                .pattern("PSP")
                .pattern("S S")
                .pattern("PSP")
                .define('P', ItemTags.PLANKS)
                .define('S', Items.STICK)
                .unlockedBy(ItemTags.PLANKS)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_SLOPE.value(), 3)
                .pattern("F ")
                .pattern("FF")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE.value(), 1)
                .pattern("FF")
                .define('F', FBContent.BLOCK_FRAMED_SLOPE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_HALF_SLOPE.value(), 2)
                .requires(FBContent.BLOCK_FRAMED_SLOPE.value())
                .requires(FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_DIVIDED_SLOPE.value())
                .pattern("SS")
                .define('S', FBContent.BLOCK_FRAMED_HALF_SLOPE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_HALF_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_DOUBLE_HALF_SLOPE.value())
                .pattern("S")
                .pattern("S")
                .define('S', FBContent.BLOCK_FRAMED_HALF_SLOPE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_HALF_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_CORNER_SLOPE.value())
                .pattern("HF ")
                .pattern("  F")
                .define('F', FBContent.BLOCK_FRAMED_SLOPE.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE.value())
                .pattern("H F")
                .pattern(" F ")
                .define('F', FBContent.BLOCK_FRAMED_SLOPE.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_DOUBLE_CORNER.value(), 1)
                .pattern("IC")
                .define('C', FBContent.BLOCK_FRAMED_CORNER_SLOPE.value())
                .define('I', FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CORNER_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_PRISM_CORNER.value())
                .pattern("F F")
                .pattern(" F ")
                .define('F', FBContent.BLOCK_FRAMED_SLOPE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_INNER_PRISM_CORNER.value())
                .pattern(" F ")
                .pattern("F F")
                .define('F', FBContent.BLOCK_FRAMED_SLOPE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_DOUBLE_PRISM_CORNER.value(), 1)
                .pattern("IC")
                .define('C', FBContent.BLOCK_FRAMED_PRISM_CORNER.value())
                .define('I', FBContent.BLOCK_FRAMED_INNER_PRISM_CORNER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_PRISM_CORNER)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_THREEWAY_CORNER.value())
                .pattern("F ")
                .pattern("FF")
                .define('F', FBContent.BLOCK_FRAMED_SLOPE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_INNER_THREEWAY_CORNER.value())
                .pattern("FF")
                .pattern("F ")
                .define('F', FBContent.BLOCK_FRAMED_SLOPE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER.value(), 1)
                .pattern("IC")
                .define('C', FBContent.BLOCK_FRAMED_THREEWAY_CORNER.value())
                .define('I', FBContent.BLOCK_FRAMED_INNER_THREEWAY_CORNER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_THREEWAY_CORNER)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_SLOPE_EDGE.value(), 6)
                .pattern("FFF")
                .pattern(" H ")
                .define('F', FBContent.BLOCK_FRAMED_SLOPE.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_EDGE.value())
                .pattern("E")
                .pattern("S")
                .define('E', FBContent.BLOCK_FRAMED_SLOPE_EDGE.value())
                .define('S', FBContent.BLOCK_FRAMED_STAIRS.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE_EDGE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_EDGE.value())
                .pattern("S")
                .pattern("E")
                .define('S', FBContent.BLOCK_FRAMED_SLOPE_EDGE.value())
                .define('E', FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_EDGE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE_EDGE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_STACKED_SLOPE_EDGE.value())
                .pattern("H")
                .pattern("E")
                .pattern("S")
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .define('E', FBContent.BLOCK_FRAMED_SLOPE_EDGE.value())
                .define('S', FBContent.BLOCK_FRAMED_STAIRS.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE_EDGE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_SLAB.value(), 6)
                .pattern("FFF")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_ADJ_DOUBLE_SLAB.value(), 2)
                .pattern("FF")
                .define('F', FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_ADJ_DOUBLE_COPYCAT_SLAB.value(), 2)
                .pattern("FF")
                .define('F', FBContent.BLOCK_FRAMED_COLLAPSIBLE_COPYCAT_BLOCK.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_COLLAPSIBLE_COPYCAT_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_DIVIDED_SLAB.value())
                .pattern("EE")
                .define('E', FBContent.BLOCK_FRAMED_SLAB_EDGE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLAB_EDGE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_SLAB_EDGE.value(), 6)
                .pattern("FFF")
                .define('F', FBContent.BLOCK_FRAMED_SLAB.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLAB)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_SLAB_CORNER.value(), 8)
                .pattern("FF")
                .pattern("FF")
                .define('F', FBContent.BLOCK_FRAMED_SLAB.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLAB)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_PANEL.value(), 6)
                .pattern("F")
                .pattern("F")
                .pattern("F")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_ADJ_DOUBLE_PANEL.value(), 2)
                .pattern("F")
                .pattern("F")
                .define('F', FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_ADJ_DOUBLE_COPYCAT_PANEL.value(), 2)
                .pattern("F")
                .pattern("F")
                .define('F', FBContent.BLOCK_FRAMED_COLLAPSIBLE_COPYCAT_BLOCK.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_COLLAPSIBLE_COPYCAT_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_DIVIDED_PANEL_HOR.value())
                .pattern("E")
                .pattern("E")
                .define('E', FBContent.BLOCK_FRAMED_SLAB_EDGE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLAB_EDGE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_DIVIDED_PANEL_VERT.value())
                .pattern("PP")
                .define('P', FBContent.BLOCK_FRAMED_CORNER_PILLAR.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CORNER_PILLAR)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_CORNER_PILLAR.value(), 4)
                .pattern("F")
                .pattern("F")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_STAIRS.value(), 4)
                .pattern("F  ")
                .pattern("FF ")
                .pattern("FFF")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_DOUBLE_STAIRS.value())
                .pattern("SE")
                .define('S', FBContent.BLOCK_FRAMED_STAIRS.value())
                .define('E', FBContent.BLOCK_FRAMED_SLAB_EDGE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_STAIRS)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_HALF_STAIRS.value(), 2)
                .requires(FBContent.ITEM_FRAMED_HAMMER.value())
                .requires(FBContent.BLOCK_FRAMED_STAIRS.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_STAIRS)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_DIVIDED_STAIRS.value())
                .pattern("SS")
                .define('S', FBContent.BLOCK_FRAMED_HALF_STAIRS.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_HALF_STAIRS)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_DOUBLE_HALF_STAIRS.value())
                .pattern("SC")
                .define('S', FBContent.BLOCK_FRAMED_HALF_STAIRS.value())
                .define('C', FBContent.BLOCK_FRAMED_SLAB_CORNER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_HALF_STAIRS)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_SLOPED_STAIRS.value())
                .pattern("H")
                .pattern("S")
                .define('H', FBContent.BLOCK_FRAMED_HALF_SLOPE.value())
                .define('S', FBContent.BLOCK_FRAMED_SLAB.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLAB)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_SLICED_STAIRS_SLAB.value())
                .pattern("E")
                .pattern("S")
                .define('S', FBContent.BLOCK_FRAMED_SLAB.value())
                .define('E', FBContent.BLOCK_FRAMED_SLAB_EDGE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLAB)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_SLICED_STAIRS_PANEL.value())
                .pattern("PE")
                .define('P', FBContent.BLOCK_FRAMED_PANEL.value())
                .define('E', FBContent.BLOCK_FRAMED_SLAB_EDGE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_VERTICAL_STAIRS.value(), 4)
                .pattern("FFF")
                .pattern("FF ")
                .pattern("F  ")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_STAIRS.value())
                .pattern("SE")
                .define('S', FBContent.BLOCK_FRAMED_VERTICAL_STAIRS.value())
                .define('E', FBContent.BLOCK_FRAMED_CORNER_PILLAR.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_VERTICAL_STAIRS)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS.value(), 2)
                .requires(FBContent.BLOCK_FRAMED_VERTICAL_STAIRS.value())
                .requires(FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_VERTICAL_STAIRS)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_VERTICAL_DIVIDED_STAIRS.value())
                .pattern("S")
                .pattern("S")
                .define('S', FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_STAIRS.value())
                .pattern("SC")
                .define('S', FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS.value())
                .define('C', FBContent.BLOCK_FRAMED_SLAB_CORNER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_VERTICAL_SLICED_STAIRS.value())
                .pattern("PEW")
                .define('P', FBContent.BLOCK_FRAMED_PANEL.value())
                .define('E', FBContent.BLOCK_FRAMED_SLAB_EDGE.value())
                .define('W', FBContent.ITEM_FRAMED_WRENCH.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_VERTICAL_SLOPED_STAIRS.value())
                .pattern("PH")
                .define('H', FBContent.BLOCK_FRAMED_HALF_SLOPE.value())
                .define('P', FBContent.BLOCK_FRAMED_PANEL.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_THREEWAY_CORNER_PILLAR.value())
                .pattern("P")
                .pattern("E")
                .define('P', FBContent.BLOCK_FRAMED_CORNER_PILLAR.value())
                .define('E', FBContent.BLOCK_FRAMED_SLAB_EDGE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CORNER_PILLAR)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER_PILLAR.value())
                .pattern("F")
                .pattern("F")
                .define('F', FBContent.BLOCK_FRAMED_THREEWAY_CORNER_PILLAR.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_THREEWAY_CORNER_PILLAR)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_WALL.value(), 6)
                .pattern("FFF")
                .pattern("FFF")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FENCE.value(), 3)
                .pattern("FSF")
                .pattern("FSF")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('S', Items.STICK)
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FENCE_GATE.value())
                .pattern("SFS")
                .pattern("SFS")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('S', Items.STICK)
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_DOOR.value(), 3)
                .pattern("FF")
                .pattern("FF")
                .pattern("FF")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_IRON_DOOR.value())
                .pattern("IDI")
                .define('D', FBContent.BLOCK_FRAMED_DOOR.value())
                .define('I', Items.IRON_INGOT)
                .unlockedBy(FBContent.BLOCK_FRAMED_DOOR)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_TRAP_DOOR.value())
                .pattern("FFF")
                .pattern("FFF")
                .define('F', FBContent.BLOCK_FRAMED_SLAB.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLAB)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR.value())
                .requires(FBContent.BLOCK_FRAMED_TRAP_DOOR.value())
                .requires(Items.IRON_INGOT)
                .unlockedBy(FBContent.BLOCK_FRAMED_TRAP_DOOR)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_PRESSURE_PLATE.value())
                .pattern("FF")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_STONE_PRESSURE_PLATE.value())
                .pattern("FF")
                .pattern("SS")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('S', Tags.Items.STONES)
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_OBSIDIAN_PRESSURE_PLATE.value())
                .pattern("FF")
                .pattern("OO")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('O', Tags.Items.OBSIDIANS)
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_GOLD_PRESSURE_PLATE.value())
                .pattern("FF")
                .pattern("GG")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('G', Tags.Items.INGOTS_GOLD)
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_IRON_PRESSURE_PLATE.value())
                .pattern("FF")
                .pattern("II")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('I', Tags.Items.INGOTS_IRON)
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_LADDER.value(), 3)
                .pattern("F F")
                .pattern("FSF")
                .pattern("F F")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('S', Items.STICK)
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_BUTTON.value())
                .requires(FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_STONE_BUTTON.value())
                .requires(FBContent.BLOCK_FRAMED_CUBE.value())
                .requires(Tags.Items.STONES)
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_LARGE_BUTTON.value())
                .pattern("BB")
                .pattern("BB")
                .define('B', FBContent.BLOCK_FRAMED_BUTTON.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_BUTTON)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_LARGE_STONE_BUTTON.value())
                .pattern("BB")
                .pattern("BB")
                .define('B', FBContent.BLOCK_FRAMED_STONE_BUTTON.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_STONE_BUTTON)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_LEVER.value())
                .pattern("S")
                .pattern("F")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('S', Items.STICK)
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_SIGN.value(), 3)
                .pattern("FFF")
                .pattern("FFF")
                .pattern(" S ")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('S', Items.STICK)
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_HANGING_SIGN.value(), 6)
                .pattern("C C")
                .pattern("FFF")
                .pattern("FFF")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('C', Items.CHAIN)
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_TORCH.value(), 4)
                .pattern("C")
                .pattern("F")
                .define('C', ItemTags.COALS)
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_SOUL_TORCH.value(), 4)
                .pattern("C")
                .pattern("F")
                .pattern("S")
                .define('C', ItemTags.COALS)
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('S', ItemTags.SOUL_FIRE_BASE_BLOCKS)
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_REDSTONE_TORCH.value(), 4)
                .pattern("R")
                .pattern("F")
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FLOOR.value(), 4)
                .pattern("FFH")
                .define('F', FBContent.BLOCK_FRAMED_SLAB.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLAB)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_WALL_BOARD.value(), 4)
                .pattern("FFH")
                .define('F', FBContent.BLOCK_FRAMED_PANEL.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_PANEL)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_CORNER_STRIP.value(), 16)
                .requires(FBContent.BLOCK_FRAMED_FLOOR.value())
                .requires(FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_FLOOR)
                .save(consumer, Utils.rl("framed_corner_strip_from_framed_floor_board"));

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_CORNER_STRIP.value(), 16)
                .requires(FBContent.BLOCK_FRAMED_WALL_BOARD.value())
                .requires(FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_WALL_BOARD)
                .save(consumer, Utils.rl("framed_corner_strip_from_framed_wall_board"));

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_LATTICE.value(), 3)
                .pattern(" F ")
                .pattern("FFF")
                .pattern(" F ")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_THICK_LATTICE.value(), 2)
                .pattern("HF ")
                .pattern("FFF")
                .pattern(" F ")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_CHEST.value(), 1)
                .pattern("FFF")
                .pattern("F F")
                .pattern("FFF")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_SECRET_STORAGE.value())
                .pattern(" F ")
                .pattern("FCF")
                .pattern(" F ")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('C', FBContent.BLOCK_FRAMED_CHEST.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_BARS.value(), 16)
                .pattern("F F")
                .pattern("FFF")
                .pattern("F F")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_PANE.value(), 12)
                .pattern("FF")
                .pattern("FF")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_HORIZONTAL_PANE.value(), 4)
                .pattern("PP")
                .pattern("PP")
                .define('P', FBContent.BLOCK_FRAMED_PANE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_PANE)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_RAIL_SLOPE.value(), 1)
                .requires(FBContent.BLOCK_FRAMED_SLOPE.value())
                .requires(Items.RAIL)
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_POWERED_RAIL_SLOPE.value(), 1)
                .requires(FBContent.BLOCK_FRAMED_SLOPE.value())
                .requires(Items.POWERED_RAIL)
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_DETECTOR_RAIL_SLOPE.value(), 1)
                .requires(FBContent.BLOCK_FRAMED_SLOPE.value())
                .requires(Items.DETECTOR_RAIL)
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_ACTIVATOR_RAIL_SLOPE.value(), 1)
                .requires(FBContent.BLOCK_FRAMED_SLOPE.value())
                .requires(Items.ACTIVATOR_RAIL)
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FANCY_RAIL.value(), 16)
                .pattern("I I")
                .pattern("IFI")
                .pattern("I I")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL.value(), 6)
                .pattern("G G")
                .pattern("GFG")
                .pattern("GRG")
                .define('G', Tags.Items.INGOTS_GOLD)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL.value(), 6)
                .pattern("IPI")
                .pattern("IFI")
                .pattern("IRI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('P', Items.STONE_PRESSURE_PLATE)
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL.value(), 6)
                .pattern("IFI")
                .pattern("IRI")
                .pattern("IFI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', Items.REDSTONE_TORCH)
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FANCY_RAIL_SLOPE.value())
                .pattern("R")
                .pattern("S")
                .define('R', FBContent.BLOCK_FRAMED_FANCY_RAIL.value())
                .define('S', FBContent.BLOCK_FRAMED_SLOPE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL_SLOPE.value())
                .pattern("R")
                .pattern("S")
                .define('R', FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL.value())
                .define('S', FBContent.BLOCK_FRAMED_SLOPE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL_SLOPE.value())
                .pattern("R")
                .pattern("S")
                .define('R', FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL.value())
                .define('S', FBContent.BLOCK_FRAMED_SLOPE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE.value())
                .pattern("R")
                .pattern("S")
                .define('R', FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL.value())
                .define('S', FBContent.BLOCK_FRAMED_SLOPE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FLOWER_POT.value(), 1)
                .pattern("F F")
                .pattern(" F ")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_PILLAR.value(), 1)
                .requires(FBContent.BLOCK_FRAMED_CORNER_PILLAR.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CORNER_PILLAR)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_CORNER_PILLAR.value(), 1)
                .requires(FBContent.BLOCK_FRAMED_PILLAR.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_PILLAR)
                .save(consumer, Utils.rl("framed_corner_pillar_from_pillar"));

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_HALF_PILLAR.value(), 1)
                .requires(FBContent.BLOCK_FRAMED_SLAB_CORNER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLAB_CORNER)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_SLAB_CORNER.value(), 1)
                .requires(FBContent.BLOCK_FRAMED_HALF_PILLAR.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_HALF_PILLAR)
                .save(consumer, Utils.rl("framed_slab_corner_from_half_pillar"));

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_POST.value(), 1)
                .requires(FBContent.BLOCK_FRAMED_FENCE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_FENCE)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_FENCE.value(), 1)
                .requires(FBContent.BLOCK_FRAMED_POST.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_POST)
                .save(consumer, Utils.rl("framed_fence_from_post"));

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK.value(), 4)
                .pattern("FFF")
                .pattern("FFF")
                .pattern("FFF")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_COLLAPSIBLE_COPYCAT_BLOCK.value(), 4)
                .pattern("FCF")
                .pattern("C C")
                .pattern("FCF")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('C', Tags.Items.INGOTS_COPPER)
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_BOUNCY_CUBE.value())
                .pattern(" S ")
                .pattern("SCS")
                .pattern(" S ")
                .define('S', Tags.Items.SLIME_BALLS)
                .define('C', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_REDSTONE_BLOCK.value())
                .pattern("RRR")
                .pattern("RCR")
                .pattern("RRR")
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('C', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_PRISM.value(), 2)
                .pattern("FFH")
                .define('F', FBContent.BLOCK_FRAMED_SLOPE.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_ELEVATED_INNER_PRISM.value(), 2)
                .pattern("FF")
                .pattern("SS")
                .define('F', FBContent.BLOCK_FRAMED_SLOPE.value())
                .define('S', FBContent.BLOCK_FRAMED_SLAB.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_ELEVATED_INNER_DOUBLE_PRISM.value())
                .pattern("P")
                .pattern("I")
                .define('P', FBContent.BLOCK_FRAMED_PRISM.value())
                .define('I', FBContent.BLOCK_FRAMED_ELEVATED_INNER_PRISM.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_PRISM)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_SLOPED_PRISM.value(), 2)
                .pattern("FF")
                .define('F', FBContent.BLOCK_FRAMED_CORNER_SLOPE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CORNER_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_ELEVATED_INNER_SLOPED_PRISM.value(), 2)
                .pattern("FF")
                .pattern("SS")
                .define('F', FBContent.BLOCK_FRAMED_CORNER_SLOPE.value())
                .define('S', FBContent.BLOCK_FRAMED_SLAB.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CORNER_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_ELEVATED_INNER_DOUBLE_SLOPED_PRISM.value())
                .pattern("P")
                .pattern("I")
                .define('P', FBContent.BLOCK_FRAMED_SLOPED_PRISM.value())
                .define('I', FBContent.BLOCK_FRAMED_ELEVATED_INNER_SLOPED_PRISM.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPED_PRISM)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_SLOPE_SLAB.value(), 6)
                .pattern("FFF")
                .define('F', FBContent.BLOCK_FRAMED_SLOPE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_SLAB.value())
                .pattern("S")
                .pattern("F")
                .define('S', FBContent.BLOCK_FRAMED_SLOPE_SLAB.value())
                .define('F', FBContent.BLOCK_FRAMED_SLAB.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE_SLAB)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_COMPOUND_SLOPE_SLAB.value())
                .pattern("SSH")
                .define('S', FBContent.BLOCK_FRAMED_SLOPE_SLAB.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE_SLAB)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_SLAB.value())
                .pattern("FF")
                .define('F', FBContent.BLOCK_FRAMED_SLOPE_SLAB.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE_SLAB)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_SLAB.value())
                .requires(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_SLAB.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_SLAB)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_SLAB.value())
                .requires(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_SLAB.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_SLAB)
                .save(consumer, Utils.rl("framed_double_slope_slab_from_inverse"));

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB.value())
                .pattern("S")
                .pattern("E")
                .define('S', FBContent.BLOCK_FRAMED_SLOPE_SLAB.value())
                .define('E', FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_SLAB.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE_SLAB)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_STACKED_SLOPE_SLAB.value())
                .pattern("H")
                .pattern("S")
                .pattern("F")
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .define('S', FBContent.BLOCK_FRAMED_SLOPE_SLAB.value())
                .define('F', FBContent.BLOCK_FRAMED_SLAB.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE_SLAB)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER.value())
                .pattern("HF ")
                .pattern("  F")
                .define('F', FBContent.BLOCK_FRAMED_SLOPE_SLAB.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE_SLAB)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER.value())
                .pattern("H F")
                .pattern(" F ")
                .define('F', FBContent.BLOCK_FRAMED_SLOPE_SLAB.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE_SLAB)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_SLOPE_SLAB_CORNER.value())
                .pattern("C")
                .pattern("S")
                .define('C', FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER.value())
                .define('S', FBContent.BLOCK_FRAMED_SLAB.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_SLOPE_SLAB_CORNER.value())
                .pattern("C")
                .pattern("S")
                .define('C', FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER.value())
                .define('S', FBContent.BLOCK_FRAMED_SLAB.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER.value())
                .pattern("C")
                .pattern("I")
                .define('C', FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER.value())
                .define('I', FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_SLAB_CORNER.value())
                .requires(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER.value())
                .requires(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_SLAB_CORNER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_SLAB_CORNER)
                .save(consumer, Utils.rl("framed_flat_double_slope_slab_corner_from_inverse"));

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_DOUBLE_SLOPE_SLAB_CORNER.value())
                .pattern("C")
                .pattern("E")
                .define('C', FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER.value())
                .define('E', FBContent.BLOCK_FRAMED_FLAT_ELEVATED_SLOPE_SLAB_CORNER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_SLOPE_SLAB_CORNER)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_DOUBLE_SLOPE_SLAB_CORNER.value())
                .pattern("C")
                .pattern("E")
                .define('C', FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER.value())
                .define('E', FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_SLOPE_SLAB_CORNER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_SLOPE_SLAB_CORNER)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER.value())
                .pattern("H")
                .pattern("C")
                .pattern("S")
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .define('C', FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER.value())
                .define('S', FBContent.BLOCK_FRAMED_SLAB.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER.value())
                .pattern("H")
                .pattern("C")
                .pattern("S")
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .define('C', FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER.value())
                .define('S', FBContent.BLOCK_FRAMED_SLAB.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_SLOPE_PANEL.value(), 6)
                .pattern("S")
                .pattern("S")
                .pattern("S")
                .define('S', FBContent.BLOCK_FRAMED_SLOPE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_EXTENDED_SLOPE_PANEL.value())
                .pattern("PS")
                .define('P', FBContent.BLOCK_FRAMED_PANEL.value())
                .define('S', FBContent.BLOCK_FRAMED_SLOPE_PANEL.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_COMPOUND_SLOPE_PANEL.value())
                .pattern("PPH")
                .define('P', FBContent.BLOCK_FRAMED_SLOPE_PANEL.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_PANEL.value())
                .pattern("PP")
                .define('P', FBContent.BLOCK_FRAMED_SLOPE_PANEL.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE_PANEL)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_PANEL.value())
                .requires(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_PANEL.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_PANEL)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_PANEL.value())
                .requires(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_PANEL.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_PANEL)
                .save(consumer, Utils.rl("framed_double_slope_panel_from_inverse_double_slope_panel"));

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL.value())
                .pattern("ES")
                .define('S', FBContent.BLOCK_FRAMED_SLOPE_PANEL.value())
                .define('E', FBContent.BLOCK_FRAMED_EXTENDED_SLOPE_PANEL.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_STACKED_SLOPE_PANEL.value())
                .pattern("PSH")
                .define('P', FBContent.BLOCK_FRAMED_PANEL.value())
                .define('S', FBContent.BLOCK_FRAMED_SLOPE_PANEL.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER.value())
                .pattern("HF ")
                .pattern("  F")
                .define('F', FBContent.BLOCK_FRAMED_SLOPE_PANEL.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER.value())
                .pattern("H F")
                .pattern(" F ")
                .define('F', FBContent.BLOCK_FRAMED_SLOPE_PANEL.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_SLOPE_PANEL_CORNER.value())
                .pattern("PC")
                .define('C', FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER.value())
                .define('P', FBContent.BLOCK_FRAMED_PANEL.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_SLOPE_PANEL_CORNER.value())
                .pattern("PC")
                .define('C', FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER.value())
                .define('P', FBContent.BLOCK_FRAMED_PANEL.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER.value())
                .pattern("IC")
                .define('C', FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER.value())
                .define('I', FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_PANEL_CORNER.value())
                .requires(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER.value())
                .requires(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_PANEL_CORNER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_PANEL_CORNER)
                .save(consumer, Utils.rl("framed_flat_double_slope_panel_corner_from_flat_inverse_double_slope_panel_corner"));

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_DOUBLE_SLOPE_PANEL_CORNER.value())
                .pattern("CI")
                .define('C', FBContent.BLOCK_FRAMED_FLAT_EXTENDED_SLOPE_PANEL_CORNER.value())
                .define('I', FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_SLOPE_PANEL_CORNER)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_DOUBLE_SLOPE_PANEL_CORNER.value())
                .pattern("IC")
                .define('I', FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_SLOPE_PANEL_CORNER.value())
                .define('C', FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_SLOPE_PANEL_CORNER)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER.value())
                .pattern("PCH")
                .define('C', FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER.value())
                .define('P', FBContent.BLOCK_FRAMED_PANEL.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER.value())
                .pattern("PCH")
                .define('C', FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER.value())
                .define('P', FBContent.BLOCK_FRAMED_PANEL.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL.value(), 6)
                .pattern("HP")
                .pattern("PP")
                .define('P', FBContent.BLOCK_FRAMED_SLOPE_PANEL.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL.value(), 2)
                .pattern("H  ")
                .pattern(" PP")
                .pattern(" P ")
                .define('P', FBContent.BLOCK_FRAMED_SLOPE_PANEL.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL.value())
                .pattern("PP")
                .pattern("PH")
                .define('P', FBContent.BLOCK_FRAMED_SLOPE_PANEL.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL.value())
                .pattern(" P ")
                .pattern("PP ")
                .pattern("  H")
                .define('P', FBContent.BLOCK_FRAMED_SLOPE_PANEL.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL.value())
                .pattern("HCP")
                .define('C', FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL.value())
                .define('P', FBContent.BLOCK_FRAMED_CORNER_PILLAR.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL.value())
                .pattern("HCS")
                .define('C', FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL.value())
                .define('S', FBContent.BLOCK_FRAMED_VERTICAL_STAIRS.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL.value())
                .pattern("HCI")
                .define('C', FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL.value())
                .define('I', FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL.value())
                .pattern("HCI")
                .define('C', FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL.value())
                .define('I', FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL.value())
                .define('H', FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_CORNER_SLOPE_PANEL.value())
                .pattern("CI")
                .define('C', FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL.value())
                .define('I', FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL.value())
                .pattern("CI")
                .define('C', FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL.value())
                .define('I', FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL.value())
                .pattern("IC")
                .define('I', FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL.value())
                .define('C', FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL.value())
                .pattern("CP")
                .define('C', FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL.value())
                .define('P', FBContent.BLOCK_FRAMED_CORNER_PILLAR.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL.value())
                .pattern("CS")
                .define('C', FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL.value())
                .define('S', FBContent.BLOCK_FRAMED_VERTICAL_STAIRS.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_GLOWING_CUBE.value())
                .pattern(" G ")
                .pattern("GCG")
                .pattern(" G ")
                .define('G', Tags.Items.DUSTS_GLOWSTONE)
                .define('C', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_PYRAMID.value(), 4)
                .pattern("SS")
                .pattern("SS")
                .define('S', FBContent.BLOCK_FRAMED_SLOPE_PANEL.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE_PANEL)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_PYRAMID_SLAB.value(), 4)
                .pattern("SS")
                .pattern("SS")
                .define('S', FBContent.BLOCK_FRAMED_SLOPE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_TARGET.value())
                .pattern("FRF")
                .pattern("RHR")
                .pattern("FRF")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('H', Items.HAY_BLOCK)
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_GATE.value(), 4)
                .pattern("DD")
                .pattern("DD")
                .define('D', FBContent.BLOCK_FRAMED_DOOR.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_DOOR)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_IRON_GATE.value(), 4)
                .pattern("DD")
                .pattern("DD")
                .define('D', FBContent.BLOCK_FRAMED_IRON_DOOR.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_IRON_DOOR)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_ITEM_FRAME.value())
                .pattern("FFF")
                .pattern("FLF")
                .pattern("FFF")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('L', Tags.Items.LEATHERS)
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_GLOWING_ITEM_FRAME.value())
                .requires(FBContent.BLOCK_FRAMED_ITEM_FRAME.value())
                .requires(Items.GLOW_INK_SAC)
                .unlockedBy(FBContent.BLOCK_FRAMED_ITEM_FRAME)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_MINI_CUBE.value(), 1)
                .requires(FBContent.BLOCK_FRAMED_CUBE.value())
                .requires(FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_HALF_PILLAR)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_ONE_WAY_WINDOW.value(), 4)
                .pattern("GFG")
                .pattern("F F")
                .pattern("GFG")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('G', Blocks.TINTED_GLASS)
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_BOOKSHELF.value())
                .pattern("FFF")
                .pattern("BBB")
                .pattern("FFF")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('B', Items.BOOK)
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_CHISELED_BOOKSHELF.value())
                .pattern("FFF")
                .pattern("SSS")
                .pattern("FFF")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('S', FBContent.BLOCK_FRAMED_SLAB.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_CENTERED_SLAB.value())
                .requires(FBContent.BLOCK_FRAMED_SLAB.value())
                .requires(FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLAB)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_SLAB.value())
                .requires(FBContent.BLOCK_FRAMED_CENTERED_SLAB.value())
                .requires(FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CENTERED_SLAB)
                .save(consumer, Utils.rl("framed_slab_from_framed_centered_slab"));

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_CENTERED_PANEL.value())
                .requires(FBContent.BLOCK_FRAMED_PANEL.value())
                .requires(FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_PANEL)
                .save(consumer);

        shapelessBuildingBlock(FBContent.BLOCK_FRAMED_PANEL.value())
                .requires(FBContent.BLOCK_FRAMED_CENTERED_PANEL.value())
                .requires(FBContent.ITEM_FRAMED_HAMMER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CENTERED_PANEL)
                .save(consumer, Utils.rl("framed_panel_from_framed_centered_panel"));

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_MASONRY_CORNER.value())
                .pattern("EE")
                .pattern("EE")
                .define('E', FBContent.BLOCK_FRAMED_SLAB_EDGE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLAB_EDGE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_CHECKERED_CUBE.value())
                .pattern("CCC")
                .pattern("C C")
                .pattern("CCC")
                .define('C', FBContent.BLOCK_FRAMED_SLAB_CORNER.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_SLAB_CORNER)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_CHECKERED_SLAB.value(), 6)
                .pattern("CCC")
                .define('C', FBContent.BLOCK_FRAMED_CHECKERED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CHECKERED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_CHECKERED_PANEL.value(), 6)
                .pattern("C")
                .pattern("C")
                .pattern("C")
                .define('C', FBContent.BLOCK_FRAMED_CHECKERED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CHECKERED_CUBE)
                .save(consumer);

        shapedBuildingBlock(FBContent.BLOCK_FRAMED_TUBE.value(), 4)
                .pattern(" C ")
                .pattern("C C")
                .pattern(" C ")
                .define('C', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);



        shapedRecipe(RecipeCategory.TOOLS, FBContent.BLOCK_FRAMING_SAW.value())
                .pattern(" I ")
                .pattern("FFF")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedRecipe(RecipeCategory.TOOLS, FBContent.BLOCK_POWERED_FRAMING_SAW.value())
                .pattern("RIR")
                .pattern("FFF")
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);



        shapedRecipe(RecipeCategory.TOOLS, FBContent.ITEM_FRAMED_HAMMER.value())
                .pattern(" F ")
                .pattern(" SF")
                .pattern("S  ")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('S', Items.STICK)
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedRecipe(RecipeCategory.TOOLS, FBContent.ITEM_FRAMED_WRENCH.value())
                .pattern("F F")
                .pattern(" S ")
                .pattern(" S ")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('S', Items.STICK)
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedRecipe(RecipeCategory.TOOLS, FBContent.ITEM_FRAMED_BLUEPRINT.value())
                .pattern(" F ")
                .pattern("FPF")
                .pattern(" F ")
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('P', Items.PAPER)
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedRecipe(RecipeCategory.TOOLS, FBContent.ITEM_FRAMED_KEY.value())
                .pattern("SSF")
                .pattern("NN ")
                .define('S', Tags.Items.RODS_WOODEN)
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .define('N', Tags.Items.NUGGETS_IRON)
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedRecipe(RecipeCategory.TOOLS, FBContent.ITEM_FRAMED_SCREWDRIVER.value())
                .pattern("S ")
                .pattern(" F")
                .define('S', Tags.Items.RODS_WOODEN)
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedRecipe(RecipeCategory.MISC, FBContent.ITEM_FRAMED_REINFORCEMENT.value(), 16)
                .pattern("OSO")
                .pattern("SFS")
                .pattern("OSO")
                .define('O', Tags.Items.OBSIDIANS)
                .define('S', Items.STICK)
                .define('F', FBContent.BLOCK_FRAMED_CUBE.value())
                .unlockedBy(FBContent.BLOCK_FRAMED_CUBE)
                .save(consumer);

        shapedRecipe(RecipeCategory.MISC, FBContent.ITEM_PHANTOM_PASTE.value(), 16)
                .pattern("SSS")
                .pattern("SMS")
                .pattern("SSS")
                .define('S', Tags.Items.SLIME_BALLS)
                .define('M', Items.PHANTOM_MEMBRANE)
                .unlockedBy(Items.PHANTOM_MEMBRANE)
                .save(consumer, Utils.rl("phantom_paste_from_phantom_membrane"));

        shapedRecipe(RecipeCategory.MISC, FBContent.ITEM_PHANTOM_PASTE.value(), 8)
                .pattern("SSS")
                .pattern("SCS")
                .pattern("SSS")
                .define('S', Tags.Items.SLIME_BALLS)
                .define('C', Items.CHORUS_FRUIT)
                .unlockedBy(Items.PHANTOM_MEMBRANE)
                .save(consumer, Utils.rl("phantom_paste_from_chorus_fruit"));



        makeRotationRecipe(FBContent.BLOCK_FRAMED_SLAB, FBContent.BLOCK_FRAMED_PANEL, consumer);
        makeRotationRecipe(FBContent.BLOCK_FRAMED_DIVIDED_SLAB, FBContent.BLOCK_FRAMED_DIVIDED_PANEL_HOR, consumer);
        makeRotationRecipe(FBContent.BLOCK_FRAMED_STAIRS, FBContent.BLOCK_FRAMED_VERTICAL_STAIRS, consumer);
        makeRotationRecipe(FBContent.BLOCK_FRAMED_DOUBLE_STAIRS, FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_STAIRS, consumer);
        makeRotationRecipe(FBContent.BLOCK_FRAMED_FLOOR, FBContent.BLOCK_FRAMED_WALL_BOARD, consumer);
        makeRotationRecipe(FBContent.BLOCK_FRAMED_SLOPE_SLAB, FBContent.BLOCK_FRAMED_SLOPE_PANEL, consumer);
        makeRotationRecipe(FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_SLAB, FBContent.BLOCK_FRAMED_EXTENDED_SLOPE_PANEL, consumer);
        makeRotationRecipe(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_SLAB, FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_PANEL, consumer);
        makeRotationRecipe(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_SLAB, FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_PANEL, consumer);
        makeRotationRecipe(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB, FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL, consumer);
        makeRotationRecipe(FBContent.BLOCK_FRAMED_STACKED_SLOPE_SLAB, FBContent.BLOCK_FRAMED_STACKED_SLOPE_PANEL, consumer);
        makeRotationRecipe(FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER, FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER, consumer);
        makeRotationRecipe(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER, FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER, consumer);
        makeRotationRecipe(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_SLOPE_SLAB_CORNER, FBContent.BLOCK_FRAMED_FLAT_EXTENDED_SLOPE_PANEL_CORNER, consumer);
        makeRotationRecipe(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_SLOPE_SLAB_CORNER, FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_SLOPE_PANEL_CORNER, consumer);
        makeRotationRecipe(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER, FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER, consumer);
        makeRotationRecipe(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_SLAB_CORNER, FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_PANEL_CORNER, consumer);
        makeRotationRecipe(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_DOUBLE_SLOPE_SLAB_CORNER, FBContent.BLOCK_FRAMED_FLAT_EXTENDED_DOUBLE_SLOPE_PANEL_CORNER, consumer);
        makeRotationRecipe(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_DOUBLE_SLOPE_SLAB_CORNER, FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_DOUBLE_SLOPE_PANEL_CORNER, consumer);
        makeRotationRecipe(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER, FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER, consumer);
        makeRotationRecipe(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER, FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER, consumer);
        makeRotationRecipe(FBContent.BLOCK_FRAMED_CENTERED_SLAB, FBContent.BLOCK_FRAMED_CENTERED_PANEL, consumer);



        SpecialRecipeBuilder.special(category -> new CamoApplicationRecipe(category, Ingredient.of(Items.BRUSH)))
                .save(consumer, Utils.rl("camo_application"));
    }

    private static void makeRotationRecipe(Holder<Block> first, Holder<Block> second, RecipeOutput consumer)
    {
        String firstName = Utils.getKeyOrThrow(first).location().getPath();
        String secondName = Utils.getKeyOrThrow(second).location().getPath();

        String name = firstName + "_rotate_to_" + secondName;
        shapelessBuildingBlock(second.value())
                .requires(first.value())
                .requires(FBContent.ITEM_FRAMED_WRENCH.value())
                .unlockedBy(first)
                .save(consumer, Utils.rl(name));

        name = secondName + "_rotate_to_" + firstName;
        shapelessBuildingBlock(first.value())
                .requires(second.value())
                .requires(FBContent.ITEM_FRAMED_WRENCH.value())
                .unlockedBy(second)
                .save(consumer, Utils.rl(name));
    }

    private static ExtShapedRecipeBuilder shapedBuildingBlock(ItemLike output)
    {
        return shapedBuildingBlock(output, 1);
    }

    private static ExtShapedRecipeBuilder shapedBuildingBlock(ItemLike output, int count)
    {
        return shapedRecipe(RecipeCategory.BUILDING_BLOCKS, output, count);
    }

    private static ExtShapedRecipeBuilder shapedRecipe(RecipeCategory category, ItemLike output)
    {
        return shapedRecipe(category, output, 1);
    }

    private static ExtShapedRecipeBuilder shapedRecipe(RecipeCategory category, ItemLike output, int count)
    {
        return new ExtShapedRecipeBuilder(category, output, count);
    }

    private static ExtShapelessRecipeBuilder shapelessBuildingBlock(ItemLike output)
    {
        return shapelessBuildingBlock(output, 1);
    }

    private static ExtShapelessRecipeBuilder shapelessBuildingBlock(ItemLike output, int count)
    {
        return shapelessRecipe(RecipeCategory.BUILDING_BLOCKS, output, count);
    }

    private static ExtShapelessRecipeBuilder shapelessRecipe(RecipeCategory category, ItemLike output)
    {
        return shapelessRecipe(category, output, 1);
    }

    private static ExtShapelessRecipeBuilder shapelessRecipe(RecipeCategory category, ItemLike output, int count)
    {
        return new ExtShapelessRecipeBuilder(category, output, count);
    }
}
