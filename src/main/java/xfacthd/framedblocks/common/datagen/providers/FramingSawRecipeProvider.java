package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.crafting.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class FramingSawRecipeProvider extends RecipeProvider
{
    public FramingSawRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> providerFuture)
    {
        super(output, providerFuture);
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer)
    {
        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_CUBE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_SLOPE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_HALF_SLOPE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_DIVIDED_SLOPE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_DOUBLE_HALF_SLOPE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_CORNER_SLOPE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_DOUBLE_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_PRISM_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_INNER_PRISM_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_DOUBLE_PRISM_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_THREEWAY_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_INNER_THREEWAY_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_SLOPE_EDGE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_EDGE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8 * 7)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_EDGE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_STACKED_SLOPE_EDGE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8 * 7)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_CORNER_SLOPE_EDGE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 16)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE_EDGE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 16 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_ELEVATED_CORNER_SLOPE_EDGE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 16 * 13)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 16 * 15)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_SLAB)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_ADJ_DOUBLE_SLAB)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE * 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_ADJ_DOUBLE_COPYCAT_SLAB)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .additive(FramingSawRecipeAdditive.of(Tags.Items.INGOTS_COPPER))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_DIVIDED_SLAB)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_SLAB_EDGE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_SLAB_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_ADJ_DOUBLE_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE * 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_ADJ_DOUBLE_COPYCAT_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .additive(FramingSawRecipeAdditive.of(Tags.Items.INGOTS_COPPER))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_DIVIDED_PANEL_HOR)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_DIVIDED_PANEL_VERT)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_CORNER_PILLAR)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_THREEWAY_CORNER_PILLAR)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER_PILLAR)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_STAIRS)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_DOUBLE_STAIRS)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_HALF_STAIRS)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3 / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_DIVIDED_STAIRS)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_DOUBLE_HALF_STAIRS)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_SLOPED_STAIRS)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_SLICED_STAIRS_SLAB)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_SLICED_STAIRS_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_VERTICAL_STAIRS)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_STAIRS)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3 / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_VERTICAL_DIVIDED_STAIRS)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_STAIRS)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_VERTICAL_SLICED_STAIRS)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_VERTICAL_SLOPED_STAIRS)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_WALL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FENCE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FENCE_GATE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_DOOR)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_IRON_DOOR)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .additive(FramingSawRecipeAdditive.of(Tags.Items.INGOTS_IRON, 2))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_TRAP_DOOR)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additive(FramingSawRecipeAdditive.of(Tags.Items.INGOTS_IRON))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_PRESSURE_PLATE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_STONE_PRESSURE_PLATE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additive(FramingSawRecipeAdditive.of(Tags.Items.STONES))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_OBSIDIAN_PRESSURE_PLATE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additive(FramingSawRecipeAdditive.of(Tags.Items.OBSIDIANS))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_GOLD_PRESSURE_PLATE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additive(FramingSawRecipeAdditive.of(Tags.Items.INGOTS_GOLD))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_IRON_PRESSURE_PLATE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additive(FramingSawRecipeAdditive.of(Tags.Items.INGOTS_IRON))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_LADDER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_BUTTON)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_STONE_BUTTON)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8)
                .additive(FramingSawRecipeAdditive.of(Tags.Items.STONES))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_LARGE_BUTTON)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_LARGE_STONE_BUTTON)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additive(FramingSawRecipeAdditive.of(Tags.Items.STONES))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_LEVER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additive(FramingSawRecipeAdditive.of(Tags.Items.COBBLESTONES))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_SIGN)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_HANGING_SIGN, 2)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .additive(FramingSawRecipeAdditive.of(Items.CHAIN))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_TORCH, 4)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additive(FramingSawRecipeAdditive.of(ItemTags.COALS))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_SOUL_TORCH, 4)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additives(List.of(
                        FramingSawRecipeAdditive.of(ItemTags.COALS),
                        FramingSawRecipeAdditive.of(Items.SOUL_SAND)
                ))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_REDSTONE_TORCH, 4)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additive(FramingSawRecipeAdditive.of(Tags.Items.DUSTS_REDSTONE))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FLOOR)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 16)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_WALL_BOARD)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 16)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_CORNER_STRIP)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 16 / 16)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_LATTICE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 6)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_THICK_LATTICE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_CHEST)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE * 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_SECRET_STORAGE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE * 4)
                .additive(FramingSawRecipeAdditive.of(FBContent.BLOCK_FRAMED_CHEST.value()))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_TANK)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE * 4)
                .additive(FramingSawRecipeAdditive.of(Tags.Items.GLASS_BLOCKS_CHEAP, 4))
                .additive(FramingSawRecipeAdditive.of(Items.BUCKET))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_BARS)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_PANE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_HORIZONTAL_PANE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_RAIL_SLOPE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .additive(FramingSawRecipeAdditive.of(Items.RAIL))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_POWERED_RAIL_SLOPE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .additive(FramingSawRecipeAdditive.of(Items.POWERED_RAIL))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_DETECTOR_RAIL_SLOPE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .additive(FramingSawRecipeAdditive.of(Items.DETECTOR_RAIL))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_ACTIVATOR_RAIL_SLOPE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .additive(FramingSawRecipeAdditive.of(Items.ACTIVATOR_RAIL))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FANCY_RAIL, 16)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .additive(FramingSawRecipeAdditive.of(Tags.Items.INGOTS_IRON, 6))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL, 6)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .additives(List.of(
                        FramingSawRecipeAdditive.of(Tags.Items.INGOTS_GOLD, 6),
                        FramingSawRecipeAdditive.of(Tags.Items.DUSTS_REDSTONE)
                ))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL, 6)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .additives(List.of(
                        FramingSawRecipeAdditive.of(Tags.Items.INGOTS_IRON, 6),
                        FramingSawRecipeAdditive.of(Tags.Items.DUSTS_REDSTONE),
                        FramingSawRecipeAdditive.of(Items.STONE_PRESSURE_PLATE)
                ))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL, 6)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .additives(List.of(
                        FramingSawRecipeAdditive.of(Tags.Items.INGOTS_IRON, 6),
                        FramingSawRecipeAdditive.of(Items.REDSTONE_TORCH)
                ))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FANCY_RAIL_SLOPE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .additive(FramingSawRecipeAdditive.of(FBContent.BLOCK_FRAMED_FANCY_RAIL.value()))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL_SLOPE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .additive(FramingSawRecipeAdditive.of(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL.value()))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL_SLOPE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .additive(FramingSawRecipeAdditive.of(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL.value()))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .additive(FramingSawRecipeAdditive.of(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL.value()))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FLOWER_POT)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_PILLAR)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_HALF_PILLAR)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_POST)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE * 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_COLLAPSIBLE_COPYCAT_BLOCK)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .additive(FramingSawRecipeAdditive.of(Tags.Items.INGOTS_COPPER))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_BOUNCY_CUBE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .additive(FramingSawRecipeAdditive.of(Tags.Items.SLIME_BALLS, 4))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_REDSTONE_BLOCK)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .additive(FramingSawRecipeAdditive.of(Tags.Items.DUSTS_REDSTONE, 8))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_PRISM)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_ELEVATED_INNER_PRISM)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_ELEVATED_INNER_DOUBLE_PRISM)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_SLOPED_PRISM)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_ELEVATED_INNER_SLOPED_PRISM)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_ELEVATED_INNER_DOUBLE_SLOPED_PRISM)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_SLOPE_SLAB)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_SLAB)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_COMPOUND_SLOPE_SLAB)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_SLAB)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_SLAB)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_STACKED_SLOPE_SLAB)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_SLOPE_SLAB_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_SLOPE_SLAB_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_SLAB_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_DOUBLE_SLOPE_SLAB_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_DOUBLE_SLOPE_SLAB_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_SLOPE_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_EXTENDED_SLOPE_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_COMPOUND_SLOPE_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_STACKED_SLOPE_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_SLOPE_PANEL_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_SLOPE_PANEL_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_PANEL_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_DOUBLE_SLOPE_PANEL_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_DOUBLE_SLOPE_PANEL_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_CORNER_SLOPE_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8 * 5)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8 * 5)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8 * 5)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8 * 5)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_GLOWING_CUBE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .additive(FramingSawRecipeAdditive.of(Tags.Items.DUSTS_GLOWSTONE, 4))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_PYRAMID)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_PYRAMID_SLAB, 3)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_TARGET)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .additives(List.of(
                        FramingSawRecipeAdditive.of(Items.HAY_BLOCK),
                        FramingSawRecipeAdditive.of(Tags.Items.DUSTS_REDSTONE, 4)
                ))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_GATE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_IRON_GATE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additive(FramingSawRecipeAdditive.of(Tags.Items.INGOTS_IRON))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_GLOWING_ITEM_FRAME)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additives(List.of(
                        FramingSawRecipeAdditive.of(Tags.Items.LEATHERS),
                        FramingSawRecipeAdditive.of(Items.GLOW_INK_SAC)
                ))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_ITEM_FRAME)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additive(FramingSawRecipeAdditive.of(Tags.Items.LEATHERS))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_MINI_CUBE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_ONE_WAY_WINDOW)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .additive(FramingSawRecipeAdditive.of(Blocks.TINTED_GLASS))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_BOOKSHELF)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE * 2)
                .additive(FramingSawRecipeAdditive.of(Items.BOOK, 3))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_CHISELED_BOOKSHELF)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_CENTERED_SLAB)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_CENTERED_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_MASONRY_CORNER)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_CHECKERED_CUBE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_CHECKERED_SLAB)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_CHECKERED_PANEL)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_TUBE)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.BLOCK_FRAMED_CHAIN)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 16)
                .save(consumer);
    }

    @Override
    public String getName()
    {
        return "Framing Saw Recipes";
    }
}
