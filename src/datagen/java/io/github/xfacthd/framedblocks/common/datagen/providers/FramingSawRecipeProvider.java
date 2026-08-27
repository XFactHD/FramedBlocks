package io.github.xfacthd.framedblocks.common.datagen.providers;

import io.github.xfacthd.framedblocks.api.datagen.recipes.AbstractFramingSawRecipeProvider;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class FramingSawRecipeProvider extends AbstractFramingSawRecipeProvider {
    private FramingSawRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        sawRecipe(FBContent.BLOCK_FRAMED_CUBE)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SLOPE)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_HALF_SLOPE)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_DIVIDED_SLOPE)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_DOUBLE_HALF_SLOPE)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_CORNER_SLOPE)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_DOUBLE_CORNER)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_PRISM_CORNER)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_INNER_PRISM_CORNER)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_DOUBLE_PRISM_CORNER)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_THREEWAY_CORNER)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_INNER_THREEWAY_CORNER)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SLOPE_EDGE)
                .material(CUBE_MATERIAL_VALUE / 8)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_EDGE)
                .material(CUBE_MATERIAL_VALUE / 8 * 7)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_EDGE)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_STACKED_SLOPE_EDGE)
                .material(CUBE_MATERIAL_VALUE / 8 * 7)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_CORNER_SLOPE_EDGE)
                .material(CUBE_MATERIAL_VALUE / 16)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE_EDGE)
                .material(CUBE_MATERIAL_VALUE / 16 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_ELEVATED_CORNER_SLOPE_EDGE)
                .material(CUBE_MATERIAL_VALUE / 16 * 13)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE)
                .material(CUBE_MATERIAL_VALUE / 16 * 15)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_CORNER_SLOPE_EDGE)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_INNER_CORNER_SLOPE_EDGE)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_EDGE)
                .material(CUBE_MATERIAL_VALUE / 16 * 13)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_EDGE)
                .material(CUBE_MATERIAL_VALUE / 16 * 15)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_THREEWAY_CORNER_SLOPE_EDGE)
                .material(CUBE_MATERIAL_VALUE / 16)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_INNER_THREEWAY_CORNER_SLOPE_EDGE)
                .material(CUBE_MATERIAL_VALUE / 16 * 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SLOPE_EDGE_SLAB)
                .material(CUBE_MATERIAL_VALUE / 8 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SLOPE_EDGE_PANEL)
                .material(CUBE_MATERIAL_VALUE / 8 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SLAB)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_DOUBLE_SLAB)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_ADJ_DOUBLE_SLAB)
                .material(CUBE_MATERIAL_VALUE)
                .additive(additive(Tags.Items.INGOTS_COPPER))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_DIVIDED_SLAB)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SLAB_EDGE)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SLAB_CORNER)
                .material(CUBE_MATERIAL_VALUE / 8)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_PANEL)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_DOUBLE_PANEL)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_ADJ_DOUBLE_PANEL)
                .material(CUBE_MATERIAL_VALUE)
                .additive(additive(Tags.Items.INGOTS_COPPER))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_DIVIDED_PANEL_HOR)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_DIVIDED_PANEL_VERT)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_CORNER_PILLAR)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_THREEWAY_CORNER_PILLAR)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER_PILLAR)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_STAIRS)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_DOUBLE_STAIRS)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_HALF_STAIRS)
                .material(CUBE_MATERIAL_VALUE / 4 * 3 / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_DIVIDED_STAIRS)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_DOUBLE_HALF_STAIRS)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SLOPED_STAIRS)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SLOPED_DOUBLE_STAIRS)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SLICED_SLOPED_STAIRS_SLAB)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SLICED_SLOPED_STAIRS_SLOPE)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SLICED_STAIRS_SLAB)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SLICED_STAIRS_PANEL)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_VERTICAL_STAIRS)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_STAIRS)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS)
                .material(CUBE_MATERIAL_VALUE / 4 * 3 / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_VERTICAL_DIVIDED_STAIRS)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_STAIRS)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_VERTICAL_SLICED_STAIRS)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_VERTICAL_SLOPED_STAIRS)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_VERTICAL_SLOPED_DOUBLE_STAIRS)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_VERTICAL_SLICED_SLOPED_STAIRS_PANEL)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_VERTICAL_SLICED_SLOPED_STAIRS_SLOPE)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_WALL)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FENCE)
                .material(CUBE_MATERIAL_VALUE / 8)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FENCE_GATE)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_DOOR)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_IRON_DOOR)
                .material(CUBE_MATERIAL_VALUE / 2)
                .additive(additive(Tags.Items.INGOTS_IRON, 2))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_TRAP_DOOR)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR)
                .material(CUBE_MATERIAL_VALUE / 4)
                .additive(additive(Tags.Items.INGOTS_IRON))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_PRESSURE_PLATE)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_STONE_PRESSURE_PLATE)
                .material(CUBE_MATERIAL_VALUE / 4)
                .additive(additive(Tags.Items.STONES))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_OBSIDIAN_PRESSURE_PLATE)
                .material(CUBE_MATERIAL_VALUE / 4)
                .additive(additive(Tags.Items.OBSIDIANS))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_GOLD_PRESSURE_PLATE)
                .material(CUBE_MATERIAL_VALUE / 4)
                .additive(additive(Tags.Items.INGOTS_GOLD))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_IRON_PRESSURE_PLATE)
                .material(CUBE_MATERIAL_VALUE / 4)
                .additive(additive(Tags.Items.INGOTS_IRON))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_LADDER)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_BUTTON)
                .material(CUBE_MATERIAL_VALUE / 8)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_STONE_BUTTON)
                .material(CUBE_MATERIAL_VALUE / 8)
                .additive(additive(Tags.Items.STONES))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_LARGE_BUTTON)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_LARGE_STONE_BUTTON)
                .material(CUBE_MATERIAL_VALUE / 4)
                .additive(additive(Tags.Items.STONES))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_LEVER)
                .material(CUBE_MATERIAL_VALUE / 4)
                .additive(additive(Tags.Items.COBBLESTONES))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SIGN)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_HANGING_SIGN, 2)
                .material(CUBE_MATERIAL_VALUE / 2)
                .additive(additive(Items.IRON_CHAIN))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_TORCH, 4)
                .material(CUBE_MATERIAL_VALUE / 4)
                .additive(additive(ItemTags.COALS))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SOUL_TORCH, 4)
                .material(CUBE_MATERIAL_VALUE / 4)
                .additives(List.of(
                        additive(ItemTags.COALS),
                        additive(ItemTags.SOUL_FIRE_BASE_BLOCKS)
                ))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_COPPER_TORCH, 4)
                .material(CUBE_MATERIAL_VALUE / 4)
                .additives(List.of(
                        additive(ItemTags.COALS),
                        additive(Items.COPPER_NUGGET)
                ))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_REDSTONE_TORCH, 4)
                .material(CUBE_MATERIAL_VALUE / 4)
                .additive(additive(Tags.Items.DUSTS_REDSTONE))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_BOARD)
                .material(CUBE_MATERIAL_VALUE / 16)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_HALF_BOARD)
                .material(CUBE_MATERIAL_VALUE / 16 / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_DIVIDED_BOARD)
                .material(CUBE_MATERIAL_VALUE / 16)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_CORNER_BOARD)
                .material(CUBE_MATERIAL_VALUE / 16 / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_INNER_CORNER_BOARD)
                .material(CUBE_MATERIAL_VALUE / 16 / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_DOUBLE_CORNER_BOARD)
                .material(CUBE_MATERIAL_VALUE / 16)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_CORNER_STRIP)
                .material(CUBE_MATERIAL_VALUE / 16 / 16)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_LATTICE)
                .material(CUBE_MATERIAL_VALUE / 6)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_THICK_LATTICE)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_CHEST)
                .material(CUBE_MATERIAL_VALUE * 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SECRET_STORAGE)
                .material(CUBE_MATERIAL_VALUE * 4)
                .additive(additive(FBContent.BLOCK_FRAMED_CHEST.value()))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_TANK)
                .material(CUBE_MATERIAL_VALUE * 4)
                .additive(additive(Tags.Items.GLASS_BLOCKS_CHEAP, 4))
                .additive(additive(Items.BUCKET))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_BARS)
                .material(CUBE_MATERIAL_VALUE / 8)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_PANE)
                .material(CUBE_MATERIAL_VALUE / 8)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_HORIZONTAL_PANE)
                .material(CUBE_MATERIAL_VALUE / 8)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_RAIL_SLOPE)
                .material(CUBE_MATERIAL_VALUE / 2)
                .additive(additive(Items.RAIL))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_POWERED_RAIL_SLOPE)
                .material(CUBE_MATERIAL_VALUE / 2)
                .additive(additive(Items.POWERED_RAIL))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_DETECTOR_RAIL_SLOPE)
                .material(CUBE_MATERIAL_VALUE / 2)
                .additive(additive(Items.DETECTOR_RAIL))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_ACTIVATOR_RAIL_SLOPE)
                .material(CUBE_MATERIAL_VALUE / 2)
                .additive(additive(Items.ACTIVATOR_RAIL))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FANCY_RAIL, 16)
                .material(CUBE_MATERIAL_VALUE)
                .additive(additive(Tags.Items.INGOTS_IRON, 6))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL, 6)
                .material(CUBE_MATERIAL_VALUE)
                .additives(List.of(
                        additive(Tags.Items.INGOTS_GOLD, 6),
                        additive(Tags.Items.DUSTS_REDSTONE)
                ))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL, 6)
                .material(CUBE_MATERIAL_VALUE)
                .additives(List.of(
                        additive(Tags.Items.INGOTS_IRON, 6),
                        additive(Tags.Items.DUSTS_REDSTONE),
                        additive(Items.STONE_PRESSURE_PLATE)
                ))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL, 6)
                .material(CUBE_MATERIAL_VALUE)
                .additives(List.of(
                        additive(Tags.Items.INGOTS_IRON, 6),
                        additive(Items.REDSTONE_TORCH)
                ))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FANCY_RAIL_SLOPE)
                .material(CUBE_MATERIAL_VALUE / 2)
                .additive(additive(FBContent.BLOCK_FRAMED_FANCY_RAIL.value()))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL_SLOPE)
                .material(CUBE_MATERIAL_VALUE / 2)
                .additive(additive(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL.value()))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL_SLOPE)
                .material(CUBE_MATERIAL_VALUE / 2)
                .additive(additive(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL.value()))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE)
                .material(CUBE_MATERIAL_VALUE / 2)
                .additive(additive(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL.value()))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FLOWER_POT)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_PILLAR)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_HALF_PILLAR)
                .material(CUBE_MATERIAL_VALUE / 8)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_PILLAR_SOCKET)
                .material(CUBE_MATERIAL_VALUE / 8 * 5)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SPLIT_PILLAR_SOCKET)
                .material(CUBE_MATERIAL_VALUE / 8 * 5)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_POST)
                .material(CUBE_MATERIAL_VALUE / 8)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK)
                .material(CUBE_MATERIAL_VALUE * 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_COLLAPSIBLE_CUBE)
                .material(CUBE_MATERIAL_VALUE)
                .additive(additive(Tags.Items.INGOTS_COPPER))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_BOUNCY_CUBE)
                .material(CUBE_MATERIAL_VALUE)
                .additive(additive(Tags.Items.SLIME_BALLS, 4))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_REDSTONE_BLOCK)
                .material(CUBE_MATERIAL_VALUE)
                .additive(additive(Tags.Items.DUSTS_REDSTONE, 8))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_PRISM)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_ELEVATED_INNER_PRISM)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_ELEVATED_INNER_DOUBLE_PRISM)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SLOPED_PRISM)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_ELEVATED_INNER_SLOPED_PRISM)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_ELEVATED_INNER_DOUBLE_SLOPED_PRISM)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SLOPE_SLAB)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_SLAB)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_COMPOUND_SLOPE_SLAB)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_SLAB)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_SLAB)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_STACKED_SLOPE_SLAB)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_SLOPE_SLAB_CORNER)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_SLOPE_SLAB_CORNER)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_SLAB_CORNER)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_DOUBLE_SLOPE_SLAB_CORNER)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_DOUBLE_SLOPE_SLAB_CORNER)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SLOPE_PANEL)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_EXTENDED_SLOPE_PANEL)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_COMPOUND_SLOPE_PANEL)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_PANEL)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_PANEL)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_STACKED_SLOPE_PANEL)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_SLOPE_PANEL_CORNER)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_SLOPE_PANEL_CORNER)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_PANEL_CORNER)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_DOUBLE_SLOPE_PANEL_CORNER)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_DOUBLE_SLOPE_PANEL_CORNER)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL)
                .material(CUBE_MATERIAL_VALUE / 8)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL)
                .material(CUBE_MATERIAL_VALUE / 8 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL)
                .material(CUBE_MATERIAL_VALUE / 8)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL)
                .material(CUBE_MATERIAL_VALUE / 8 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL)
                .material(CUBE_MATERIAL_VALUE / 4 * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_CORNER_SLOPE_PANEL)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL)
                .material(CUBE_MATERIAL_VALUE / 8 * 5)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL)
                .material(CUBE_MATERIAL_VALUE / 8 * 5)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL)
                .material(CUBE_MATERIAL_VALUE / 8 * 5)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL)
                .material(CUBE_MATERIAL_VALUE / 8 * 5)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SMALL_PRISM_SLOPE_PANEL_CORNER)
                .material(CUBE_MATERIAL_VALUE / 8)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_LARGE_PRISM_SLOPE_PANEL_CORNER)
                .material(CUBE_MATERIAL_VALUE / 8)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SMALL_INNER_PRISM_SLOPE_PANEL_CORNER)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_LARGE_INNER_PRISM_SLOPE_PANEL_CORNER)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_PYRAMID, 3)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_PYRAMID_SLAB, 3)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_ELEVATED_PYRAMID_SLAB, 3)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_STACKED_PYRAMID_SLAB, 3)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_TARGET)
                .material(CUBE_MATERIAL_VALUE)
                .additives(List.of(
                        additive(Items.HAY_BLOCK),
                        additive(Tags.Items.DUSTS_REDSTONE, 4)
                ))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_GATE)
                .material(CUBE_MATERIAL_VALUE / 4)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_IRON_GATE)
                .material(CUBE_MATERIAL_VALUE / 4)
                .additive(additive(Tags.Items.INGOTS_IRON))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_GLOWING_ITEM_FRAME)
                .material(CUBE_MATERIAL_VALUE / 4)
                .additives(List.of(
                        additive(Tags.Items.LEATHERS),
                        additive(Items.GLOW_INK_SAC)
                ))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_ITEM_FRAME)
                .material(CUBE_MATERIAL_VALUE / 4)
                .additive(additive(Tags.Items.LEATHERS))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_MINI_CUBE)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_ONE_WAY_WINDOW)
                .material(CUBE_MATERIAL_VALUE)
                .additive(additive(Blocks.TINTED_GLASS))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_BOOKSHELF)
                .material(CUBE_MATERIAL_VALUE * 2)
                .additive(additive(Items.BOOK, 3))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_CHISELED_BOOKSHELF)
                .material(CUBE_MATERIAL_VALUE * 3)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_CENTERED_SLAB)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_CENTERED_PANEL)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_MASONRY_CORNER)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_CHECKERED_CUBE)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_CHECKERED_SLAB)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_CHECKERED_PANEL)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_TUBE)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_CORNER_TUBE)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_CHAIN)
                .material(CUBE_MATERIAL_VALUE / 16)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_LANTERN)
                .material(CUBE_MATERIAL_VALUE)
                .additive(additive(Items.TORCH))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SOUL_LANTERN)
                .material(CUBE_MATERIAL_VALUE)
                .additive(additive(Items.SOUL_TORCH))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_COPPER_LANTERN)
                .material(CUBE_MATERIAL_VALUE)
                .additive(additive(Items.COPPER_TORCH))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_HOPPER)
                .material(CUBE_MATERIAL_VALUE)
                .additive(additive(Tags.Items.CHESTS))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_LAYERED_CUBE)
                .material(CUBE_MATERIAL_VALUE / 8)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_LIGHTNING_ROD)
                .material(CUBE_MATERIAL_VALUE / 16)
                .additive(additive(Tags.Items.INGOTS_COPPER, 3))
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_PATH)
                .material(CUBE_MATERIAL_VALUE)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_SHELF)
                .material(CUBE_MATERIAL_VALUE / 2)
                .save(output);

        sawRecipe(FBContent.BLOCK_FRAMED_BANNER)
                .material(CUBE_MATERIAL_VALUE)
                .additive(additive(ItemTags.WOOL, 2))
                .save(output);
    }

    public static final class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new FramingSawRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Framing Saw Recipes";
        }
    }
}
