package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;

import java.util.function.Consumer;

public final class FramedRecipeProvider extends RecipeProvider
{
    private final CriterionTriggerInstance HAS_FRAMED_BLOCK = has(FBContent.blockFramedCube.get());
    private final CriterionTriggerInstance HAS_FRAMED_SLOPE = has(FBContent.blockFramedSlope.get());

    public FramedRecipeProvider(DataGenerator gen) { super(gen); }

    @Override
    public String getName() { return super.getName() + ": " + FramedConstants.MOD_ID; }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer)
    {
        ShapedRecipeBuilder.shaped(FBContent.blockFramedCube.get(), 4)
                .pattern("PSP")
                .pattern("S S")
                .pattern("PSP")
                .define('P', ItemTags.PLANKS)
                .define('S', Items.STICK)
                .unlockedBy("hasPlanks", has(ItemTags.PLANKS))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedSlope.get(), 3)
                .pattern("F ")
                .pattern("FF")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedCornerSlope.get())
                .pattern("HF ")
                .pattern("  F")
                .define('F', FBContent.blockFramedSlope.get())
                .define('H', FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedInnerCornerSlope.get())
                .pattern("H F")
                .pattern(" F ")
                .define('F', FBContent.blockFramedSlope.get())
                .define('H', FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedPrismCorner.get())
                .pattern("F F")
                .pattern(" F ")
                .define('F', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedInnerPrismCorner.get())
                .pattern(" F ")
                .pattern("F F")
                .define('F', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedThreewayCorner.get())
                .pattern("F ")
                .pattern("FF")
                .define('F', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedInnerThreewayCorner.get())
                .pattern("FF")
                .pattern("F ")
                .define('F', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedSlab.get(), 6)
                .pattern("FFF")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedSlabEdge.get(), 6)
                .pattern("FFF")
                .define('F', FBContent.blockFramedSlab.get())
                .unlockedBy("hasFramedSlab", has(FBContent.blockFramedSlab.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedSlabCorner.get(), 8)
                .pattern("FF")
                .pattern("FF")
                .define('F', FBContent.blockFramedSlab.get())
                .unlockedBy("hasFramedSlab", has(FBContent.blockFramedSlab.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedDividedSlab.get())
                .pattern("EE")
                .define('E', FBContent.blockFramedSlabEdge.get())
                .unlockedBy("hasFramedSlabEdge", has(FBContent.blockFramedSlabEdge.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedPanel.get(), 6)
                .pattern("F")
                .pattern("F")
                .pattern("F")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedCornerPillar.get(), 4)
                .pattern("F")
                .pattern("F")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedDividedPanelHor.get())
                .pattern("E")
                .pattern("E")
                .define('E', FBContent.blockFramedSlabEdge.get())
                .unlockedBy("hasFramedSlabEdge", has(FBContent.blockFramedSlabEdge.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedDividedPanelVert.get())
                .pattern("PP")
                .define('P', FBContent.blockFramedCornerPillar.get())
                .unlockedBy("hasFramedCornerPillar", has(FBContent.blockFramedCornerPillar.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedStairs.get(), 4)
                .pattern("F  ")
                .pattern("FF ")
                .pattern("FFF")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedWall.get(), 6)
                .pattern("FFF")
                .pattern("FFF")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFence.get(), 3)
                .pattern("FSF")
                .pattern("FSF")
                .define('F', FBContent.blockFramedCube.get())
                .define('S', Items.STICK)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFenceGate.get())
                .pattern("SFS")
                .pattern("SFS")
                .define('F', FBContent.blockFramedCube.get())
                .define('S', Items.STICK)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedDoor.get(), 3)
                .pattern("FF")
                .pattern("FF")
                .pattern("FF")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedIronDoor.get())
                .pattern("IDI")
                .define('D', FBContent.blockFramedDoor.get())
                .define('I', Items.IRON_INGOT)
                .unlockedBy("hasFramedDoor", has(FBContent.blockFramedDoor.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedTrapDoor.get())
                .pattern("FFF")
                .pattern("FFF")
                .define('F', FBContent.blockFramedSlab.get())
                .unlockedBy("hasFramedSlab", has(FBContent.blockFramedSlab.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedIronTrapDoor.get())
                .requires(FBContent.blockFramedTrapDoor.get())
                .requires(Items.IRON_INGOT)
                .unlockedBy("hasFramedTrapdoor", has(FBContent.blockFramedTrapDoor.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedPressurePlate.get())
                .pattern("FF")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedStonePressurePlate.get())
                .pattern("FF")
                .pattern("SS")
                .define('F', FBContent.blockFramedCube.get())
                .define('S', Tags.Items.STONE)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedObsidianPressurePlate.get())
                .pattern("FF")
                .pattern("OO")
                .define('F', FBContent.blockFramedCube.get())
                .define('O', Tags.Items.OBSIDIAN)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedGoldPressurePlate.get())
                .pattern("FF")
                .pattern("GG")
                .define('F', FBContent.blockFramedCube.get())
                .define('G', Tags.Items.INGOTS_GOLD)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedIronPressurePlate.get())
                .pattern("FF")
                .pattern("II")
                .define('F', FBContent.blockFramedCube.get())
                .define('I', Tags.Items.INGOTS_IRON)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedLadder.get(), 3)
                .pattern("F F")
                .pattern("FSF")
                .pattern("F F")
                .define('F', FBContent.blockFramedCube.get())
                .define('S', Items.STICK)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedButton.get())
                .requires(FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", has(FBContent.blockFramedCube.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedStoneButton.get())
                .requires(FBContent.blockFramedCube.get())
                .requires(Tags.Items.STONE)
                .unlockedBy("hasFramedBlock", has(FBContent.blockFramedCube.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedLever.get())
                .pattern("S")
                .pattern("F")
                .define('F', FBContent.blockFramedCube.get())
                .define('S', Items.STICK)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedSign.get(), 3)
                .pattern("FFF")
                .pattern("FFF")
                .pattern(" S ")
                .define('F', FBContent.blockFramedCube.get())
                .define('S', Items.STICK)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedDoubleSlope.get(), 1)
                .pattern("FF")
                .define('F', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedDoubleCorner.get(), 1)
                .pattern("IC")
                .define('C', FBContent.blockFramedCornerSlope.get())
                .define('I', FBContent.blockFramedInnerCornerSlope.get())
                .unlockedBy("hasFramedCorner", has(FBContent.blockFramedCornerSlope.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedDoublePrismCorner.get(), 1)
                .pattern("IC")
                .define('C', FBContent.blockFramedPrismCorner.get())
                .define('I', FBContent.blockFramedInnerPrismCorner.get())
                .unlockedBy("hasFramedCorner", has(FBContent.blockFramedPrismCorner.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedDoubleThreewayCorner.get(), 1)
                .pattern("IC")
                .define('C', FBContent.blockFramedThreewayCorner.get())
                .define('I', FBContent.blockFramedInnerThreewayCorner.get())
                .unlockedBy("hasFramedCorner", has(FBContent.blockFramedThreewayCorner.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedTorch.get(), 4)
                .pattern("C")
                .pattern("F")
                .define('C', ItemTags.COALS)
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedSoulTorch.get(), 4)
                .pattern("C")
                .pattern("F")
                .pattern("S")
                .define('C', ItemTags.COALS)
                .define('F', FBContent.blockFramedCube.get())
                .define('S', ItemTags.SOUL_FIRE_BASE_BLOCKS)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedRedstoneTorch.get(), 4)
                .pattern("R")
                .pattern("F")
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFloor.get(), 4)
                .pattern("FFH")
                .define('F', FBContent.blockFramedSlab.get())
                .define('H', FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedSlab", has(FBContent.blockFramedSlab.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedLattice.get(), 3)
                .pattern(" F ")
                .pattern("FFF")
                .pattern(" F ")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedVerticalStairs.get(), 4)
                .pattern("FFF")
                .pattern("FF ")
                .pattern("F  ")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedChest.get(), 1)
                .pattern("FFF")
                .pattern("F F")
                .pattern("FFF")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedBars.get(), 16)
                .pattern("F F")
                .pattern("FFF")
                .pattern("F F")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedPane.get(), 12)
                .pattern("FF")
                .pattern("FF")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedRailSlope.get(), 1)
                .requires(FBContent.blockFramedSlope.get())
                .requires(Items.RAIL)
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedPoweredRailSlope.get(), 1)
                .requires(FBContent.blockFramedSlope.get())
                .requires(Items.POWERED_RAIL)
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedDetectorRailSlope.get(), 1)
                .requires(FBContent.blockFramedSlope.get())
                .requires(Items.DETECTOR_RAIL)
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedActivatorRailSlope.get(), 1)
                .requires(FBContent.blockFramedSlope.get())
                .requires(Items.ACTIVATOR_RAIL)
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFlowerPot.get(), 1)
                .pattern("F F")
                .pattern(" F ")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedPillar.get(), 1)
                .requires(FBContent.blockFramedCornerPillar.get())
                .unlockedBy("hasFramedCornerPillar", has(FBContent.blockFramedCornerPillar.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedCornerPillar.get(), 1)
                .requires(FBContent.blockFramedPillar.get())
                .unlockedBy("hasFramedPillar", has(FBContent.blockFramedPillar.get()))
                .save(consumer, Utils.rl("framed_corner_pillar_from_pillar"));

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedHalfPillar.get(), 1)
                .requires(FBContent.blockFramedSlabCorner.get())
                .unlockedBy("hasFramedSlabCorner", has(FBContent.blockFramedSlabCorner.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedSlabCorner.get(), 1)
                .requires(FBContent.blockFramedHalfPillar.get())
                .unlockedBy("hasFramedHalfPillar", has(FBContent.blockFramedHalfPillar.get()))
                .save(consumer, Utils.rl("framed_slab_corner_from_half_pillar"));

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedPost.get(), 1)
                .requires(FBContent.blockFramedFence.get())
                .unlockedBy("hasFramedFence", has(FBContent.blockFramedFence.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedFence.get(), 1)
                .requires(FBContent.blockFramedPost.get())
                .unlockedBy("hasFramedPost", has(FBContent.blockFramedPost.get()))
                .save(consumer, Utils.rl("framed_fence_from_post"));

        ShapedRecipeBuilder.shaped(FBContent.blockFramedCollapsibleBlock.get(), 4)
                .pattern("FFF")
                .pattern("FFF")
                .pattern("FFF")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedHalfStairs.get(), 2)
                .requires(FBContent.itemFramedHammer.get())
                .requires(FBContent.blockFramedStairs.get())
                .unlockedBy("hasFramedStairs", has(FBContent.blockFramedStairs.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedDividedStairs.get())
                .pattern("SS")
                .define('S', FBContent.blockFramedHalfStairs.get())
                .unlockedBy("hasFramedHalfStairs", has(FBContent.blockFramedHalfStairs.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedBouncyCube.get())
                .pattern(" S ")
                .pattern("SCS")
                .pattern(" S ")
                .define('S', Tags.Items.SLIMEBALLS)
                .define('C', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedSecretStorage.get())
                .pattern(" F ")
                .pattern("FCF")
                .pattern(" F ")
                .define('F', FBContent.blockFramedCube.get())
                .define('C', FBContent.blockFramedChest.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedRedstoneBlock.get())
                .pattern("RRR")
                .pattern("RCR")
                .pattern("RRR")
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('C', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedPrism.get(), 2)
                .pattern("FFH")
                .define('F', FBContent.blockFramedSlope.get())
                .define('H', FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedInnerPrism.get(), 2)
                .pattern("FF")
                .pattern("SS")
                .define('F', FBContent.blockFramedSlope.get())
                .define('S', FBContent.blockFramedSlab.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedDoublePrism.get())
                .pattern("P")
                .pattern("I")
                .define('P', FBContent.blockFramedPrism.get())
                .define('I', FBContent.blockFramedInnerPrism.get())
                .unlockedBy("hasFramedPrism", has(FBContent.blockFramedPrism.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedSlopedPrism.get(), 2)
                .pattern("FF")
                .define('F', FBContent.blockFramedCornerSlope.get())
                .unlockedBy("hasFramedCorner", has(FBContent.blockFramedCornerSlope.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedInnerSlopedPrism.get(), 2)
                .pattern("FF")
                .pattern("SS")
                .define('F', FBContent.blockFramedCornerSlope.get())
                .define('S', FBContent.blockFramedSlab.get())
                .unlockedBy("hasFramedCorner", has(FBContent.blockFramedCornerSlope.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedDoubleSlopedPrism.get())
                .pattern("P")
                .pattern("I")
                .define('P', FBContent.blockFramedSlopedPrism.get())
                .define('I', FBContent.blockFramedInnerSlopedPrism.get())
                .unlockedBy("hasFramedSlopedPrism", has(FBContent.blockFramedSlopedPrism.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedSlopeSlab.get(), 6)
                .pattern("FFF")
                .define('F', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedElevatedSlopeSlab.get())
                .pattern("S")
                .pattern("F")
                .define('S', FBContent.blockFramedSlopeSlab.get())
                .define('F', FBContent.blockFramedSlab.get())
                .unlockedBy("hasFramedSlopeSlab", has(FBContent.blockFramedSlopeSlab.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedDoubleSlopeSlab.get())
                .pattern("FF")
                .define('F', FBContent.blockFramedSlopeSlab.get())
                .unlockedBy("hasFramedSlopeSlab", has(FBContent.blockFramedSlopeSlab.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedInverseDoubleSlopeSlab.get())
                .requires(FBContent.blockFramedDoubleSlopeSlab.get())
                .unlockedBy("hasFramedDoubleSlopeSlab", has(FBContent.blockFramedDoubleSlopeSlab.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedDoubleSlopeSlab.get())
                .requires(FBContent.blockFramedInverseDoubleSlopeSlab.get())
                .unlockedBy("hasFramedInverseDoubleSlopeSlab", has(FBContent.blockFramedInverseDoubleSlopeSlab.get()))
                .save(consumer, Utils.rl("framed_double_slope_slab_from_inverse"));

        ShapedRecipeBuilder.shaped(FBContent.blockFramedElevatedDoubleSlopeSlab.get())
                .pattern("S")
                .pattern("E")
                .define('S', FBContent.blockFramedSlopeSlab.get())
                .define('E', FBContent.blockFramedElevatedSlopeSlab.get())
                .unlockedBy("hasFramedSlopeSlab", has(FBContent.blockFramedSlopeSlab.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedStackedSlopeSlab.get())
                .pattern("H")
                .pattern("S")
                .pattern("F")
                .define('H', FBContent.itemFramedHammer.get())
                .define('S', FBContent.blockFramedSlopeSlab.get())
                .define('F', FBContent.blockFramedSlab.get())
                .unlockedBy("hasFramedSlopeSlab", has(FBContent.blockFramedSlopeSlab.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedVerticalHalfStairs.get(), 2)
                .requires(FBContent.blockFramedVerticalStairs.get())
                .requires(FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedVerticalStairs", has(FBContent.blockFramedVerticalStairs.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedVerticalDividedStairs.get())
                .pattern("S")
                .pattern("S")
                .define('S', FBContent.blockFramedVerticalHalfStairs.get())
                .unlockedBy("hasFramedVerticalHalfStairs", has(FBContent.blockFramedVerticalHalfStairs.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFlatSlopeSlabCorner.get())
                .pattern("HF ")
                .pattern("  F")
                .define('F', FBContent.blockFramedSlopeSlab.get())
                .define('H', FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedSlopeSlab", has(FBContent.blockFramedSlopeSlab.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFlatInnerSlopeSlabCorner.get())
                .pattern("H F")
                .pattern(" F ")
                .define('F', FBContent.blockFramedSlopeSlab.get())
                .define('H', FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedSlopeSlab", has(FBContent.blockFramedSlopeSlab.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFlatElevatedSlopeSlabCorner.get())
                .pattern("C")
                .pattern("S")
                .define('C', FBContent.blockFramedFlatSlopeSlabCorner.get())
                .define('S', FBContent.blockFramedSlab.get())
                .unlockedBy("hasFramedFlatSlopeSlabCorner", has(FBContent.blockFramedFlatSlopeSlabCorner.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFlatElevatedInnerSlopeSlabCorner.get())
                .pattern("C")
                .pattern("S")
                .define('C', FBContent.blockFramedFlatInnerSlopeSlabCorner.get())
                .define('S', FBContent.blockFramedSlab.get())
                .unlockedBy("hasFramedFlatInnerSlopeSlabCorner", has(FBContent.blockFramedFlatInnerSlopeSlabCorner.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFlatDoubleSlopeSlabCorner.get())
                .pattern("C")
                .pattern("I")
                .define('C', FBContent.blockFramedFlatSlopeSlabCorner.get())
                .define('I', FBContent.blockFramedFlatInnerSlopeSlabCorner.get())
                .unlockedBy("hasFramedFlatSlopeSlabCorner", has(FBContent.blockFramedFlatSlopeSlabCorner.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedFlatInverseDoubleSlopeSlabCorner.get())
                .requires(FBContent.blockFramedFlatDoubleSlopeSlabCorner.get())
                .unlockedBy("hasFramedFlatDoubleSlopeSlabCorner", has(FBContent.blockFramedFlatDoubleSlopeSlabCorner.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedFlatDoubleSlopeSlabCorner.get())
                .requires(FBContent.blockFramedFlatInverseDoubleSlopeSlabCorner.get())
                .unlockedBy("hasFramedFlatInverseDoubleSlopeSlabCorner", has(FBContent.blockFramedFlatInverseDoubleSlopeSlabCorner.get()))
                .save(consumer, Utils.rl("framed_flat_double_slope_slab_corner_from_inverse"));

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFlatElevatedDoubleSlopeSlabCorner.get())
                .pattern("C")
                .pattern("E")
                .define('C', FBContent.blockFramedFlatInnerSlopeSlabCorner.get())
                .define('E', FBContent.blockFramedFlatElevatedSlopeSlabCorner.get())
                .unlockedBy("hasFramedFlatElevatedSlopeSlabCorner", has(FBContent.blockFramedFlatElevatedSlopeSlabCorner.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFlatElevatedInnerDoubleSlopeSlabCorner.get())
                .pattern("C")
                .pattern("E")
                .define('C', FBContent.blockFramedFlatSlopeSlabCorner.get())
                .define('E', FBContent.blockFramedFlatElevatedInnerSlopeSlabCorner.get())
                .unlockedBy("hasFramedFlatElevatedInnerSlopeSlabCorner", has(FBContent.blockFramedFlatElevatedInnerSlopeSlabCorner.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFlatStackedSlopeSlabCorner.get())
                .pattern("H")
                .pattern("C")
                .pattern("S")
                .define('H', FBContent.itemFramedHammer.get())
                .define('C', FBContent.blockFramedFlatSlopeSlabCorner.get())
                .define('S', FBContent.blockFramedSlab.get())
                .unlockedBy("hasFramedFlatSlopeSlabCorner", has(FBContent.blockFramedFlatSlopeSlabCorner.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFlatStackedInnerSlopeSlabCorner.get())
                .pattern("H")
                .pattern("C")
                .pattern("S")
                .define('H', FBContent.itemFramedHammer.get())
                .define('C', FBContent.blockFramedFlatInnerSlopeSlabCorner.get())
                .define('S', FBContent.blockFramedSlab.get())
                .unlockedBy("hasFramedFlatInnerSlopeSlabCorner", has(FBContent.blockFramedFlatInnerSlopeSlabCorner.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedSlopePanel.get(), 6)
                .pattern("S")
                .pattern("S")
                .pattern("S")
                .define('S', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedExtendedSlopePanel.get())
                .pattern("PS")
                .define('P', FBContent.blockFramedPanel.get())
                .define('S', FBContent.blockFramedSlopePanel.get())
                .unlockedBy("hasFramedSlopePanel", has(FBContent.blockFramedSlopePanel.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedDoubleSlopePanel.get())
                .pattern("PP")
                .define('P', FBContent.blockFramedSlopePanel.get())
                .unlockedBy("hasFramedSlopePanel", has(FBContent.blockFramedSlopePanel.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedInverseDoubleSlopePanel.get())
                .requires(FBContent.blockFramedDoubleSlopePanel.get())
                .unlockedBy("hasFramedDoubleSlopePanel", has(FBContent.blockFramedDoubleSlopePanel.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedDoubleSlopePanel.get())
                .requires(FBContent.blockFramedInverseDoubleSlopePanel.get())
                .unlockedBy("hasFramedInverseDoubleSlopePanel", has(FBContent.blockFramedInverseDoubleSlopePanel.get()))
                .save(consumer, Utils.rl("framed_double_slope_panel_from_inverse_double_slope_panel"));

        ShapedRecipeBuilder.shaped(FBContent.blockFramedExtendedDoubleSlopePanel.get())
                .pattern("ES")
                .define('S', FBContent.blockFramedSlopePanel.get())
                .define('E', FBContent.blockFramedExtendedSlopePanel.get())
                .unlockedBy("hasFramedSlopePanel", has(FBContent.blockFramedSlopePanel.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedStackedSlopePanel.get())
                .pattern("PSH")
                .define('P', FBContent.blockFramedPanel.get())
                .define('S', FBContent.blockFramedSlopePanel.get())
                .define('H', FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedSlopePanel", has(FBContent.blockFramedSlopePanel.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFlatSlopePanelCorner.get())
                .pattern("HF ")
                .pattern("  F")
                .define('F', FBContent.blockFramedSlopePanel.get())
                .define('H', FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedSlopePanel", has(FBContent.blockFramedSlopePanel.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFlatInnerSlopePanelCorner.get())
                .pattern("H F")
                .pattern(" F ")
                .define('F', FBContent.blockFramedSlopePanel.get())
                .define('H', FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedSlopePanel", has(FBContent.blockFramedSlopePanel.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFlatExtendedSlopePanelCorner.get())
                .pattern("PC")
                .define('C', FBContent.blockFramedFlatSlopePanelCorner.get())
                .define('P', FBContent.blockFramedPanel.get())
                .unlockedBy("hasFramedFlatSlopePanelCorner", has(FBContent.blockFramedFlatSlopePanelCorner.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFlatExtendedInnerSlopePanelCorner.get())
                .pattern("PC")
                .define('C', FBContent.blockFramedFlatInnerSlopePanelCorner.get())
                .define('P', FBContent.blockFramedPanel.get())
                .unlockedBy("hasFramedFlatInnerSlopePanelCorner", has(FBContent.blockFramedFlatInnerSlopePanelCorner.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFlatDoubleSlopePanelCorner.get())
                .pattern("IC")
                .define('C', FBContent.blockFramedFlatSlopePanelCorner.get())
                .define('I', FBContent.blockFramedFlatInnerSlopePanelCorner.get())
                .unlockedBy("hasFramedFlatSlopePanelCorner", has(FBContent.blockFramedFlatSlopePanelCorner.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedFlatInverseDoubleSlopePanelCorner.get())
                .requires(FBContent.blockFramedFlatDoubleSlopePanelCorner.get())
                .unlockedBy("hasFramedFlatDoubleSlopePanelCorner", has(FBContent.blockFramedFlatDoubleSlopePanelCorner.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedFlatDoubleSlopePanelCorner.get())
                .requires(FBContent.blockFramedFlatInverseDoubleSlopePanelCorner.get())
                .unlockedBy("hasFramedFlatInverseDoubleSlopePanelCorner", has(FBContent.blockFramedFlatInverseDoubleSlopePanelCorner.get()))
                .save(consumer, Utils.rl("framed_flat_double_slope_panel_corner_from_flat_inverse_double_slope_panel_corner"));

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFlatExtendedDoubleSlopePanelCorner.get())
                .pattern("CI")
                .define('C', FBContent.blockFramedFlatExtendedSlopePanelCorner.get())
                .define('I', FBContent.blockFramedFlatInnerSlopePanelCorner.get())
                .unlockedBy("hasFramedFlatExtendedSlopePanelCorner", has(FBContent.blockFramedFlatExtendedSlopePanelCorner.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFlatExtendedInnerDoubleSlopePanelCorner.get())
                .pattern("IC")
                .define('I', FBContent.blockFramedFlatExtendedInnerSlopePanelCorner.get())
                .define('C', FBContent.blockFramedFlatSlopePanelCorner.get())
                .unlockedBy("hasFramedFlatExtendedInnerSlopePanelCorner", has(FBContent.blockFramedFlatExtendedInnerSlopePanelCorner.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFlatStackedSlopePanelCorner.get())
                .pattern("PCH")
                .define('C', FBContent.blockFramedFlatSlopePanelCorner.get())
                .define('P', FBContent.blockFramedPanel.get())
                .define('H', FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedFlatSlopePanelCorner", has(FBContent.blockFramedFlatSlopePanelCorner.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFlatStackedInnerSlopePanelCorner.get())
                .pattern("PCH")
                .define('C', FBContent.blockFramedFlatInnerSlopePanelCorner.get())
                .define('P', FBContent.blockFramedPanel.get())
                .define('H', FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedFlatInnerSlopePanelCorner", has(FBContent.blockFramedFlatInnerSlopePanelCorner.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedDoubleStairs.get())
                .pattern("SE")
                .define('S', FBContent.blockFramedStairs.get())
                .define('E', FBContent.blockFramedSlabEdge.get())
                .unlockedBy("hasFramedStairs", has(FBContent.blockFramedStairs.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedVerticalDoubleStairs.get())
                .pattern("SE")
                .define('S', FBContent.blockFramedVerticalStairs.get())
                .define('E', FBContent.blockFramedCornerPillar.get())
                .unlockedBy("hasFramedStairs", has(FBContent.blockFramedVerticalStairs.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedWallBoard.get(), 4)
                .pattern("FFH")
                .define('F', FBContent.blockFramedPanel.get())
                .define('H', FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedPanel", has(FBContent.blockFramedPanel.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedGlowingCube.get())
                .pattern(" G ")
                .pattern("GCG")
                .pattern(" G ")
                .define('G', Tags.Items.DUSTS_GLOWSTONE)
                .define('C', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedCube", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedPyramid.get(), 4)
                .pattern("SS")
                .pattern("SS")
                .define('S', FBContent.blockFramedSlopePanel.get())
                .unlockedBy("hasFramedSlopePanel", has(FBContent.blockFramedSlopePanel.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedPyramidSlab.get(), 4)
                .pattern("SS")
                .pattern("SS")
                .define('S', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", has(FBContent.blockFramedSlope.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedHorizontalPane.get(), 4)
                .pattern("PP")
                .pattern("PP")
                .define('P', FBContent.blockFramedPane.get())
                .unlockedBy("hasFramedPane", has(FBContent.blockFramedPane.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedLargeButton.get())
                .pattern("BB")
                .pattern("BB")
                .define('B', FBContent.blockFramedButton.get())
                .unlockedBy("hasFramedButton", has(FBContent.blockFramedButton.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedLargeStoneButton.get())
                .pattern("BB")
                .pattern("BB")
                .define('B', FBContent.blockFramedStoneButton.get())
                .unlockedBy("hasFramedStoneButton", has(FBContent.blockFramedStoneButton.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedTarget.get())
                .pattern("FRF")
                .pattern("RHR")
                .pattern("FRF")
                .define('F', FBContent.blockFramedCube.get())
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('H', Items.HAY_BLOCK)
                .unlockedBy("hasFramedCube", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedGate.get(), 4)
                .pattern("DD")
                .pattern("DD")
                .define('D', FBContent.blockFramedDoor.get())
                .unlockedBy("hasFramedDoor", has(FBContent.blockFramedDoor.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedIronGate.get(), 4)
                .pattern("DD")
                .pattern("DD")
                .define('D', FBContent.blockFramedIronDoor.get())
                .unlockedBy("hasFramedIronDoor", has(FBContent.blockFramedIronDoor.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedItemFrame.get())
                .pattern("FFF")
                .pattern("FLF")
                .pattern("FFF")
                .define('F', FBContent.blockFramedCube.get())
                .define('L', Tags.Items.LEATHER)
                .unlockedBy("hasFramedCube", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedGlowingItemFrame.get())
                .requires(FBContent.blockFramedItemFrame.get())
                .requires(Items.GLOW_INK_SAC)
                .unlockedBy("hasFramedItemFrame", has(FBContent.blockFramedItemFrame.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFancyRail.get(), 16)
                .pattern("I I")
                .pattern("IFI")
                .pattern("I I")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFancyPoweredRail.get(), 6)
                .pattern("G G")
                .pattern("GFG")
                .pattern("GRG")
                .define('G', Tags.Items.INGOTS_GOLD)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFancyDetectorRail.get(), 6)
                .pattern("IPI")
                .pattern("IFI")
                .pattern("IRI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('P', Items.STONE_PRESSURE_PLATE)
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFancyActivatorRail.get(), 6)
                .pattern("IFI")
                .pattern("IRI")
                .pattern("IFI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', Items.REDSTONE_TORCH)
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFancyRailSlope.get())
                .pattern("R")
                .pattern("S")
                .define('R', FBContent.blockFramedFancyRail.get())
                .define('S', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFancyPoweredRailSlope.get())
                .pattern("R")
                .pattern("S")
                .define('R', FBContent.blockFramedFancyPoweredRail.get())
                .define('S', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFancyDetectorRailSlope.get())
                .pattern("R")
                .pattern("S")
                .define('R', FBContent.blockFramedFancyDetectorRail.get())
                .define('S', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFancyActivatorRailSlope.get())
                .pattern("R")
                .pattern("S")
                .define('R', FBContent.blockFramedFancyActivatorRail.get())
                .define('S', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedHalfSlope.get(), 2)
                .requires(FBContent.blockFramedSlope.get())
                .requires(FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedDividedSlope.get())
                .pattern("SS")
                .define('S', FBContent.blockFramedHalfSlope.get())
                .unlockedBy("hasFramedHalfSlope", has(FBContent.blockFramedHalfSlope.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedDoubleHalfSlope.get())
                .pattern("S")
                .pattern("S")
                .define('S', FBContent.blockFramedHalfSlope.get())
                .unlockedBy("hasFramedHalfSlope", has(FBContent.blockFramedHalfSlope.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedSlopedStairs.get())
                .pattern("H")
                .pattern("S")
                .define('H', FBContent.blockFramedHalfSlope.get())
                .define('S', FBContent.blockFramedSlab.get())
                .unlockedBy("hasFramedSlab", has(FBContent.blockFramedSlab.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedVerticalSlopedStairs.get())
                .pattern("PH")
                .define('H', FBContent.blockFramedHalfSlope.get())
                .define('P', FBContent.blockFramedPanel.get())
                .unlockedBy("hasFramedPanel", has(FBContent.blockFramedPanel.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedMiniCube.get(), 1)
                .requires(FBContent.blockFramedCube.get())
                .requires(FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedHalfPillar", has(FBContent.blockFramedHalfPillar.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedHalfPillar.get(), 1)
                .requires(FBContent.blockFramedMiniCube.get())
                .unlockedBy("hasFramedMiniCube", has(FBContent.blockFramedMiniCube.get()))
                .save(consumer, Utils.rl("framed_half_pillar_from_mini_cube"));

        ShapedRecipeBuilder.shaped(FBContent.blockFramedOneWayWindow.get(), 4)
                .pattern("GFG")
                .pattern("F F")
                .pattern("GFG")
                .define('F', FBContent.blockFramedCube.get())
                .define('G', Blocks.TINTED_GLASS)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);



        ShapedRecipeBuilder.shaped(FBContent.blockFramingSaw.get())
                .pattern(" I ")
                .pattern("FFF")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);



        ShapedRecipeBuilder.shaped(FBContent.itemFramedHammer.get())
                .pattern(" F ")
                .pattern(" SF")
                .pattern("S  ")
                .define('F', FBContent.blockFramedCube.get())
                .define('S', Items.STICK)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.itemFramedWrench.get())
                .pattern("F F")
                .pattern(" S ")
                .pattern(" S ")
                .define('F', FBContent.blockFramedCube.get())
                .define('S', Items.STICK)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.itemFramedBlueprint.get())
                .pattern(" F ")
                .pattern("FPF")
                .pattern(" F ")
                .define('F', FBContent.blockFramedCube.get())
                .define('P', Items.PAPER)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.itemFramedKey.get())
                .pattern("SSF")
                .pattern("NN ")
                .define('S', Tags.Items.RODS_WOODEN)
                .define('F', FBContent.blockFramedCube.get())
                .define('N', Tags.Items.NUGGETS_IRON)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.itemFramedScrewdriver.get())
                .pattern("S ")
                .pattern(" F")
                .define('S', Tags.Items.RODS_WOODEN)
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.itemFramedReinforcement.get(), 16)
                .pattern("OSO")
                .pattern("SFS")
                .pattern("OSO")
                .define('O', Tags.Items.OBSIDIAN)
                .define('S', Items.STICK)
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);



        makeRotationRecipe(FBContent.blockFramedSlab, FBContent.blockFramedPanel, consumer);
        makeRotationRecipe(FBContent.blockFramedDividedSlab, FBContent.blockFramedDividedPanelHor, consumer);
        makeRotationRecipe(FBContent.blockFramedStairs, FBContent.blockFramedVerticalStairs, consumer);
        makeRotationRecipe(FBContent.blockFramedDoubleStairs, FBContent.blockFramedVerticalDoubleStairs, consumer);
        makeRotationRecipe(FBContent.blockFramedFloor, FBContent.blockFramedWallBoard, consumer);
        makeRotationRecipe(FBContent.blockFramedSlopeSlab, FBContent.blockFramedSlopePanel, consumer);
        makeRotationRecipe(FBContent.blockFramedElevatedSlopeSlab, FBContent.blockFramedExtendedSlopePanel, consumer);
        makeRotationRecipe(FBContent.blockFramedDoubleSlopeSlab, FBContent.blockFramedDoubleSlopePanel, consumer);
        makeRotationRecipe(FBContent.blockFramedInverseDoubleSlopeSlab, FBContent.blockFramedInverseDoubleSlopePanel, consumer);
        makeRotationRecipe(FBContent.blockFramedElevatedDoubleSlopeSlab, FBContent.blockFramedExtendedDoubleSlopePanel, consumer);
        makeRotationRecipe(FBContent.blockFramedStackedSlopeSlab, FBContent.blockFramedStackedSlopePanel, consumer);
        makeRotationRecipe(FBContent.blockFramedFlatSlopeSlabCorner, FBContent.blockFramedFlatSlopePanelCorner, consumer);
        makeRotationRecipe(FBContent.blockFramedFlatInnerSlopeSlabCorner, FBContent.blockFramedFlatInnerSlopePanelCorner, consumer);
        makeRotationRecipe(FBContent.blockFramedFlatElevatedSlopeSlabCorner, FBContent.blockFramedFlatExtendedSlopePanelCorner, consumer);
        makeRotationRecipe(FBContent.blockFramedFlatElevatedInnerSlopeSlabCorner, FBContent.blockFramedFlatExtendedInnerSlopePanelCorner, consumer);
        makeRotationRecipe(FBContent.blockFramedFlatDoubleSlopeSlabCorner, FBContent.blockFramedFlatDoubleSlopePanelCorner, consumer);
        makeRotationRecipe(FBContent.blockFramedFlatInverseDoubleSlopeSlabCorner, FBContent.blockFramedFlatInverseDoubleSlopePanelCorner, consumer);
        makeRotationRecipe(FBContent.blockFramedFlatElevatedDoubleSlopeSlabCorner, FBContent.blockFramedFlatExtendedDoubleSlopePanelCorner, consumer);
        makeRotationRecipe(FBContent.blockFramedFlatElevatedInnerDoubleSlopeSlabCorner, FBContent.blockFramedFlatExtendedInnerDoubleSlopePanelCorner, consumer);
        makeRotationRecipe(FBContent.blockFramedFlatStackedSlopeSlabCorner, FBContent.blockFramedFlatStackedSlopePanelCorner, consumer);
        makeRotationRecipe(FBContent.blockFramedFlatStackedInnerSlopeSlabCorner, FBContent.blockFramedFlatStackedInnerSlopePanelCorner, consumer);
    }

    private static void makeRotationRecipe(RegistryObject<Block> first, RegistryObject<Block> second, Consumer<FinishedRecipe> consumer)
    {
        String name = first.getId().getPath() + "_rotate_to_" + second.getId().getPath();
        ShapelessRecipeBuilder.shapeless(second.get())
                .requires(first.get())
                .requires(FBContent.itemFramedWrench.get())
                .unlockedBy("has_" + first.getId().getPath(), has(first.get()))
                .save(consumer, Utils.rl(name));

        name = second.getId().getPath() + "_rotate_to_" + first.getId().getPath();
        ShapelessRecipeBuilder.shapeless(first.get())
                .requires(second.get())
                .requires(FBContent.itemFramedWrench.get())
                .unlockedBy("has_" + second.getId().getPath(), has(second.get()))
                .save(consumer, Utils.rl(name));
    }
}