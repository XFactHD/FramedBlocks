package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeBuilder;

import java.util.List;
import java.util.function.Consumer;

public final class FramingSawRecipeProvider extends RecipeProvider
{
    public FramingSawRecipeProvider(PackOutput output)
    {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer)
    {
        FramingSawRecipeBuilder.builder(FBContent.blockFramedCube)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedSlope)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedCornerSlope)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedInnerCornerSlope)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedPrismCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedInnerPrismCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedThreewayCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedInnerThreewayCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedSlab)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedSlabEdge)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedSlabCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedDividedSlab)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedPanel)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedCornerPillar)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedDividedPanelHor)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedDividedPanelVert)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedStairs)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedDoubleStairs)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedHalfStairs)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3 / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedDividedStairs)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedVerticalStairs)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedVerticalDoubleStairs)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedVerticalHalfStairs)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3 / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedVerticalDividedStairs)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedWall)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFence)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFenceGate)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedDoor)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedIronDoor)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .additive(FramingSawRecipeBuilder.Additive.of(Tags.Items.INGOTS_IRON, 2))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedTrapDoor)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedIronTrapDoor)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additive(FramingSawRecipeBuilder.Additive.of(Tags.Items.INGOTS_IRON))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedPressurePlate)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedStonePressurePlate)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additive(FramingSawRecipeBuilder.Additive.of(Tags.Items.STONE))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedObsidianPressurePlate)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additive(FramingSawRecipeBuilder.Additive.of(Tags.Items.OBSIDIAN))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedGoldPressurePlate)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additive(FramingSawRecipeBuilder.Additive.of(Tags.Items.INGOTS_GOLD))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedIronPressurePlate)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additive(FramingSawRecipeBuilder.Additive.of(Tags.Items.INGOTS_IRON))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedLadder)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedButton)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedStoneButton)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8)
                .additive(FramingSawRecipeBuilder.Additive.of(Tags.Items.STONE))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedLargeButton)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedLargeStoneButton)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additive(FramingSawRecipeBuilder.Additive.of(Tags.Items.STONE))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedLever)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additive(FramingSawRecipeBuilder.Additive.of(Tags.Items.COBBLESTONE))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedSign)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedDoubleSlope)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedDoubleCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedDoublePrismCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedDoubleThreewayCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedTorch, 4)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additive(FramingSawRecipeBuilder.Additive.of(ItemTags.COALS))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedSoulTorch)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additives(List.of(
                        FramingSawRecipeBuilder.Additive.of(ItemTags.COALS),
                        FramingSawRecipeBuilder.Additive.of(Items.SOUL_SAND)
                ))
                .disabled() // TODO: Needs more than 1 additive
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedRedstoneTorch, 4)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additive(FramingSawRecipeBuilder.Additive.of(Tags.Items.DUSTS_REDSTONE))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFloor)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 16)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedWallBoard)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 16)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedLattice)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedChest)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE * 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedSecretStorage)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE * 4)
                .additive(FramingSawRecipeBuilder.Additive.of(FBContent.blockFramedChest.get()))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedBars)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedPane)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedHorizontalPane)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedRailSlope)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .additive(FramingSawRecipeBuilder.Additive.of(Items.RAIL))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedPoweredRailSlope)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .additive(FramingSawRecipeBuilder.Additive.of(Items.POWERED_RAIL))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedDetectorRailSlope)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .additive(FramingSawRecipeBuilder.Additive.of(Items.DETECTOR_RAIL))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedActivatorRailSlope)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .additive(FramingSawRecipeBuilder.Additive.of(Items.ACTIVATOR_RAIL))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFlowerPot)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedPillar)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedHalfPillar)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedPost)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 8)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedCollapsibleBlock)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE * 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedBouncyCube)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .additive(FramingSawRecipeBuilder.Additive.of(Tags.Items.SLIMEBALLS, 4))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedRedstoneBlock)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .additive(FramingSawRecipeBuilder.Additive.of(Tags.Items.DUSTS_REDSTONE, 8))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedPrism)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedInnerPrism)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedDoublePrism)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedSlopedPrism)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedInnerSlopedPrism)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedDoubleSlopedPrism)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedSlopeSlab)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedElevatedSlopeSlab)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedDoubleSlopeSlab)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedInverseDoubleSlopeSlab)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedElevatedDoubleSlopeSlab)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedStackedSlopeSlab)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFlatSlopeSlabCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFlatInnerSlopeSlabCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFlatElevatedSlopeSlabCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFlatElevatedInnerSlopeSlabCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFlatDoubleSlopeSlabCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFlatInverseDoubleSlopeSlabCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFlatElevatedDoubleSlopeSlabCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFlatElevatedInnerDoubleSlopeSlabCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFlatStackedSlopeSlabCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFlatStackedInnerSlopeSlabCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedSlopePanel)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedExtendedSlopePanel)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedDoubleSlopePanel)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedInverseDoubleSlopePanel)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedExtendedDoubleSlopePanel)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedStackedSlopePanel)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFlatSlopePanelCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFlatInnerSlopePanelCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFlatExtendedSlopePanelCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFlatExtendedInnerSlopePanelCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFlatDoubleSlopePanelCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFlatInverseDoubleSlopePanelCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFlatExtendedDoubleSlopePanelCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFlatExtendedInnerDoubleSlopePanelCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFlatStackedSlopePanelCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFlatStackedInnerSlopePanelCorner)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedGlowingCube)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .additive(FramingSawRecipeBuilder.Additive.of(Tags.Items.DUSTS_GLOWSTONE, 4))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedPyramid)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedPyramidSlab, 3)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedTarget)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .additives(List.of(
                        FramingSawRecipeBuilder.Additive.of(Items.HAY_BLOCK),
                        FramingSawRecipeBuilder.Additive.of(Tags.Items.DUSTS_REDSTONE, 4)
                ))
                .disabled() // TODO: Needs more than 1 additive
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedGate)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedIronGate)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additive(FramingSawRecipeBuilder.Additive.of(Tags.Items.INGOTS_IRON))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedGlowingItemFrame)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additives(List.of(
                        FramingSawRecipeBuilder.Additive.of(Tags.Items.LEATHER),
                        FramingSawRecipeBuilder.Additive.of(Items.GLOW_INK_SAC)
                ))
                .disabled() // TODO: Needs more than 1 additive
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedItemFrame)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .additive(FramingSawRecipeBuilder.Additive.of(Tags.Items.LEATHER))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFancyRail, 16)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .additive(FramingSawRecipeBuilder.Additive.of(Tags.Items.INGOTS_IRON, 6))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFancyPoweredRail, 6)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .additives(List.of(
                        FramingSawRecipeBuilder.Additive.of(Tags.Items.INGOTS_GOLD, 6),
                        FramingSawRecipeBuilder.Additive.of(Tags.Items.DUSTS_REDSTONE)
                ))
                .disabled() // TODO: Needs more than 1 additive
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFancyDetectorRail, 6)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .additives(List.of(
                        FramingSawRecipeBuilder.Additive.of(Tags.Items.INGOTS_IRON, 6),
                        FramingSawRecipeBuilder.Additive.of(Tags.Items.DUSTS_REDSTONE),
                        FramingSawRecipeBuilder.Additive.of(Items.STONE_PRESSURE_PLATE)
                ))
                .disabled() // TODO: Needs more than 1 additive
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFancyActivatorRail, 6)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .additives(List.of(
                        FramingSawRecipeBuilder.Additive.of(Tags.Items.INGOTS_IRON, 6),
                        FramingSawRecipeBuilder.Additive.of(Items.REDSTONE_TORCH)
                ))
                .disabled() // TODO: Needs more than 1 additive
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFancyRailSlope)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .additive(FramingSawRecipeBuilder.Additive.of(FBContent.blockFramedFancyRail.get()))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFancyPoweredRailSlope)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .additive(FramingSawRecipeBuilder.Additive.of(FBContent.blockFramedFancyPoweredRail.get()))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFancyDetectorRailSlope)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .additive(FramingSawRecipeBuilder.Additive.of(FBContent.blockFramedFancyDetectorRail.get()))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedFancyActivatorRailSlope)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .additive(FramingSawRecipeBuilder.Additive.of(FBContent.blockFramedFancyActivatorRail.get()))
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedHalfSlope)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedDividedSlope)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedDoubleHalfSlope)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 2)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedSlopedStairs)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedVerticalSlopedStairs)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE / 4 * 3)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedMiniCube)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .save(consumer);

        FramingSawRecipeBuilder.builder(FBContent.blockFramedOneWayWindow)
                .material(FramingSawRecipe.CUBE_MATERIAL_VALUE)
                .additive(FramingSawRecipeBuilder.Additive.of(Blocks.TINTED_GLASS))
                .save(consumer);
    }

    @Override
    public String getName()
    {
        return "Framing Saw Recipes";
    }
}
