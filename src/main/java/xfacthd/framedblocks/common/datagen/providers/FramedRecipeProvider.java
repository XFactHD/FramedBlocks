package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.common.FBContent;

import java.util.function.Consumer;

public final class FramedRecipeProvider extends RecipeProvider
{
    private final CriterionTriggerInstance HAS_FRAMED_BLOCK = has(FBContent.blockFramedCube.get());
    private final CriterionTriggerInstance HAS_FRAMED_SLOPE = has(FBContent.blockFramedSlope.get());

    public FramedRecipeProvider(PackOutput output) { super(output); }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer)
    {
        shapedBuildingBlock(FBContent.blockFramedCube.get(), 4)
                .pattern("PSP")
                .pattern("S S")
                .pattern("PSP")
                .define('P', ItemTags.PLANKS)
                .define('S', Items.STICK)
                .unlockedBy("hasPlanks", has(ItemTags.PLANKS))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedSlope.get(), 3)
                .pattern("F ")
                .pattern("FF")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedCornerSlope.get())
                .pattern("HF ")
                .pattern("  F")
                .define('F', FBContent.blockFramedSlope.get())
                .define('H', FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedInnerCornerSlope.get())
                .pattern("H F")
                .pattern(" F ")
                .define('F', FBContent.blockFramedSlope.get())
                .define('H', FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedPrismCorner.get())
                .pattern("F F")
                .pattern(" F ")
                .define('F', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedInnerPrismCorner.get())
                .pattern(" F ")
                .pattern("F F")
                .define('F', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedThreewayCorner.get())
                .pattern("F ")
                .pattern("FF")
                .define('F', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedInnerThreewayCorner.get())
                .pattern("FF")
                .pattern("F ")
                .define('F', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedSlab.get(), 6)
                .pattern("FFF")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedSlabEdge.get(), 6)
                .pattern("FFF")
                .define('F', FBContent.blockFramedSlab.get())
                .unlockedBy("hasFramedSlab", has(FBContent.blockFramedSlab.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedSlabCorner.get(), 8)
                .pattern("FF")
                .pattern("FF")
                .define('F', FBContent.blockFramedSlab.get())
                .unlockedBy("hasFramedSlab", has(FBContent.blockFramedSlab.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedPanel.get(), 6)
                .pattern("F")
                .pattern("F")
                .pattern("F")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedCornerPillar.get(), 4)
                .pattern("F")
                .pattern("F")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedStairs.get(), 4)
                .pattern("F  ")
                .pattern("FF ")
                .pattern("FFF")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedWall.get(), 6)
                .pattern("FFF")
                .pattern("FFF")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFence.get(), 3)
                .pattern("FSF")
                .pattern("FSF")
                .define('F', FBContent.blockFramedCube.get())
                .define('S', Items.STICK)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFenceGate.get())
                .pattern("SFS")
                .pattern("SFS")
                .define('F', FBContent.blockFramedCube.get())
                .define('S', Items.STICK)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedDoor.get(), 3)
                .pattern("FF")
                .pattern("FF")
                .pattern("FF")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedIronDoor.get())
                .pattern("IDI")
                .define('D', FBContent.blockFramedDoor.get())
                .define('I', Items.IRON_INGOT)
                .unlockedBy("hasFramedDoor", has(FBContent.blockFramedDoor.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedTrapDoor.get())
                .pattern("FFF")
                .pattern("FFF")
                .define('F', FBContent.blockFramedSlab.get())
                .unlockedBy("hasFramedSlab", has(FBContent.blockFramedSlab.get()))
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedIronTrapDoor.get())
                .requires(FBContent.blockFramedTrapDoor.get())
                .requires(Items.IRON_INGOT)
                .unlockedBy("hasFramedTrapdoor", has(FBContent.blockFramedTrapDoor.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedPressurePlate.get())
                .pattern("FF")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedStonePressurePlate.get())
                .pattern("FF")
                .pattern("SS")
                .define('F', FBContent.blockFramedCube.get())
                .define('S', Tags.Items.STONE)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedObsidianPressurePlate.get())
                .pattern("FF")
                .pattern("OO")
                .define('F', FBContent.blockFramedCube.get())
                .define('O', Tags.Items.OBSIDIAN)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedGoldPressurePlate.get())
                .pattern("FF")
                .pattern("GG")
                .define('F', FBContent.blockFramedCube.get())
                .define('G', Tags.Items.INGOTS_GOLD)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedIronPressurePlate.get())
                .pattern("FF")
                .pattern("II")
                .define('F', FBContent.blockFramedCube.get())
                .define('I', Tags.Items.INGOTS_IRON)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedLadder.get(), 3)
                .pattern("F F")
                .pattern("FSF")
                .pattern("F F")
                .define('F', FBContent.blockFramedCube.get())
                .define('S', Items.STICK)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedButton.get())
                .requires(FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", has(FBContent.blockFramedCube.get()))
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedStoneButton.get())
                .requires(FBContent.blockFramedCube.get())
                .requires(Tags.Items.STONE)
                .unlockedBy("hasFramedBlock", has(FBContent.blockFramedCube.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedLever.get())
                .pattern("S")
                .pattern("F")
                .define('F', FBContent.blockFramedCube.get())
                .define('S', Items.STICK)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedSign.get(), 3)
                .pattern("FFF")
                .pattern("FFF")
                .pattern(" S ")
                .define('F', FBContent.blockFramedCube.get())
                .define('S', Items.STICK)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedDoubleSlope.get(), 1)
                .pattern("FF")
                .define('F', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedDoubleCorner.get(), 1)
                .pattern("IC")
                .define('C', FBContent.blockFramedCornerSlope.get())
                .define('I', FBContent.blockFramedInnerCornerSlope.get())
                .unlockedBy("hasFramedCorner", has(FBContent.blockFramedCornerSlope.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedDoublePrismCorner.get(), 1)
                .pattern("IC")
                .define('C', FBContent.blockFramedPrismCorner.get())
                .define('I', FBContent.blockFramedInnerPrismCorner.get())
                .unlockedBy("hasFramedCorner", has(FBContent.blockFramedPrismCorner.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedDoubleThreewayCorner.get(), 1)
                .pattern("IC")
                .define('C', FBContent.blockFramedThreewayCorner.get())
                .define('I', FBContent.blockFramedInnerThreewayCorner.get())
                .unlockedBy("hasFramedCorner", has(FBContent.blockFramedThreewayCorner.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedTorch.get(), 4)
                .pattern("C")
                .pattern("F")
                .define('C', ItemTags.COALS)
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedSoulTorch.get(), 4)
                .pattern("C")
                .pattern("F")
                .pattern("S")
                .define('C', ItemTags.COALS)
                .define('F', FBContent.blockFramedCube.get())
                .define('S', ItemTags.SOUL_FIRE_BASE_BLOCKS)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedRedstoneTorch.get(), 4)
                .pattern("R")
                .pattern("F")
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFloor.get(), 4)
                .pattern("FFH")
                .define('F', FBContent.blockFramedSlab.get())
                .define('H', FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedSlab", has(FBContent.blockFramedSlab.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedLattice.get(), 3)
                .pattern(" F ")
                .pattern("FFF")
                .pattern(" F ")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedVerticalStairs.get(), 4)
                .pattern("FFF")
                .pattern("FF ")
                .pattern("F  ")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedChest.get(), 1)
                .pattern("FFF")
                .pattern("F F")
                .pattern("FFF")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedBars.get(), 16)
                .pattern("F F")
                .pattern("FFF")
                .pattern("F F")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedPane.get(), 12)
                .pattern("FF")
                .pattern("FF")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedRailSlope.get(), 1)
                .requires(FBContent.blockFramedSlope.get())
                .requires(Items.RAIL)
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedPoweredRailSlope.get(), 1)
                .requires(FBContent.blockFramedSlope.get())
                .requires(Items.POWERED_RAIL)
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedDetectorRailSlope.get(), 1)
                .requires(FBContent.blockFramedSlope.get())
                .requires(Items.DETECTOR_RAIL)
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedActivatorRailSlope.get(), 1)
                .requires(FBContent.blockFramedSlope.get())
                .requires(Items.ACTIVATOR_RAIL)
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFlowerPot.get(), 1)
                .pattern("F F")
                .pattern(" F ")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedPillar.get(), 1)
                .requires(FBContent.blockFramedCornerPillar.get())
                .unlockedBy("hasFramedCornerPillar", has(FBContent.blockFramedCornerPillar.get()))
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedCornerPillar.get(), 1)
                .requires(FBContent.blockFramedPillar.get())
                .unlockedBy("hasFramedPillar", has(FBContent.blockFramedPillar.get()))
                .save(consumer, rl("framed_corner_pillar_from_pillar"));

        shapelessBuildingBlock(FBContent.blockFramedHalfPillar.get(), 1)
                .requires(FBContent.blockFramedSlabCorner.get())
                .unlockedBy("hasFramedSlabCorner", has(FBContent.blockFramedSlabCorner.get()))
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedSlabCorner.get(), 1)
                .requires(FBContent.blockFramedHalfPillar.get())
                .unlockedBy("hasFramedHalfPillar", has(FBContent.blockFramedHalfPillar.get()))
                .save(consumer, rl("framed_slab_corner_from_half_pillar"));

        shapelessBuildingBlock(FBContent.blockFramedPost.get(), 1)
                .requires(FBContent.blockFramedFence.get())
                .unlockedBy("hasFramedFence", has(FBContent.blockFramedFence.get()))
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedFence.get(), 1)
                .requires(FBContent.blockFramedPost.get())
                .unlockedBy("hasFramedPost", has(FBContent.blockFramedPost.get()))
                .save(consumer, rl("framed_fence_from_post"));

        shapedBuildingBlock(FBContent.blockFramedCollapsibleBlock.get(), 4)
                .pattern("FFF")
                .pattern("FFF")
                .pattern("FFF")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedHalfStairs.get(), 2)
                .requires(FBContent.itemFramedHammer.get())
                .requires(FBContent.blockFramedStairs.get())
                .unlockedBy("hasFramedStairs", has(FBContent.blockFramedStairs.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedStairs.get())
                .pattern("SS")
                .define('S', FBContent.blockFramedHalfStairs.get())
                .unlockedBy("hasFramedHalfStairs", has(FBContent.blockFramedHalfStairs.get()))
                .save(consumer, rl("framed_stairs_from_half_stairs"));

        shapedBuildingBlock(FBContent.blockFramedBouncyCube.get())
                .pattern(" S ")
                .pattern("SCS")
                .pattern(" S ")
                .define('S', Tags.Items.SLIMEBALLS)
                .define('C', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedSecretStorage.get())
                .pattern("FFF")
                .pattern("FCF")
                .pattern("FFF")
                .define('F', FBContent.blockFramedCube.get())
                .define('C', FBContent.blockFramedChest.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedRedstoneBlock.get())
                .pattern("RRR")
                .pattern("RCR")
                .pattern("RRR")
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('C', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedPrism.get(), 2)
                .pattern("FFH")
                .define('F', FBContent.blockFramedSlope.get())
                .define('H', FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedInnerPrism.get(), 2)
                .pattern("FF")
                .pattern("SS")
                .define('F', FBContent.blockFramedSlope.get())
                .define('S', FBContent.blockFramedSlab.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedDoublePrism.get())
                .pattern("P")
                .pattern("I")
                .define('P', FBContent.blockFramedPrism.get())
                .define('I', FBContent.blockFramedInnerPrism.get())
                .unlockedBy("hasFramedPrism", has(FBContent.blockFramedPrism.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedSlopedPrism.get(), 2)
                .pattern("FF")
                .define('F', FBContent.blockFramedCornerSlope.get())
                .unlockedBy("hasFramedCorner", has(FBContent.blockFramedCornerSlope.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedInnerSlopedPrism.get(), 2)
                .pattern("FF")
                .pattern("SS")
                .define('F', FBContent.blockFramedCornerSlope.get())
                .define('S', FBContent.blockFramedSlab.get())
                .unlockedBy("hasFramedCorner", has(FBContent.blockFramedCornerSlope.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedDoubleSlopedPrism.get())
                .pattern("P")
                .pattern("I")
                .define('P', FBContent.blockFramedSlopedPrism.get())
                .define('I', FBContent.blockFramedInnerSlopedPrism.get())
                .unlockedBy("hasFramedSlopedPrism", has(FBContent.blockFramedSlopedPrism.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedSlopeSlab.get(), 6)
                .pattern("FFF")
                .define('F', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedElevatedSlopeSlab.get())
                .pattern("S")
                .pattern("F")
                .define('S', FBContent.blockFramedSlopeSlab.get())
                .define('F', FBContent.blockFramedSlab.get())
                .unlockedBy("hasFramedSlopeSlab", has(FBContent.blockFramedSlopeSlab.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedDoubleSlopeSlab.get())
                .pattern("FF")
                .define('F', FBContent.blockFramedSlopeSlab.get())
                .unlockedBy("hasFramedSlopeSlab", has(FBContent.blockFramedSlopeSlab.get()))
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedInverseDoubleSlopeSlab.get())
                .requires(FBContent.blockFramedDoubleSlopeSlab.get())
                .unlockedBy("hasFramedDoubleSlopeSlab", has(FBContent.blockFramedDoubleSlopeSlab.get()))
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedDoubleSlopeSlab.get())
                .requires(FBContent.blockFramedInverseDoubleSlopeSlab.get())
                .unlockedBy("hasFramedInverseDoubleSlopeSlab", has(FBContent.blockFramedInverseDoubleSlopeSlab.get()))
                .save(consumer, rl("framed_double_slope_slab_from_inverse"));

        shapedBuildingBlock(FBContent.blockFramedElevatedDoubleSlopeSlab.get())
                .pattern("S")
                .pattern("E")
                .define('S', FBContent.blockFramedSlopeSlab.get())
                .define('E', FBContent.blockFramedElevatedSlopeSlab.get())
                .unlockedBy("hasFramedSlopeSlab", has(FBContent.blockFramedSlopeSlab.get()))
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedVerticalHalfStairs.get(), 2)
                .requires(FBContent.blockFramedVerticalStairs.get())
                .requires(FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedVerticalStairs", has(FBContent.blockFramedVerticalStairs.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFlatSlopeSlabCorner.get())
                .pattern("HF ")
                .pattern("  F")
                .define('F', FBContent.blockFramedSlopeSlab.get())
                .define('H', FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedSlopeSlab", has(FBContent.blockFramedSlopeSlab.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFlatInnerSlopeSlabCorner.get())
                .pattern("H F")
                .pattern(" F ")
                .define('F', FBContent.blockFramedSlopeSlab.get())
                .define('H', FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedSlopeSlab", has(FBContent.blockFramedSlopeSlab.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFlatElevatedSlopeSlabCorner.get())
                .pattern("C")
                .pattern("S")
                .define('C', FBContent.blockFramedFlatSlopeSlabCorner.get())
                .define('S', FBContent.blockFramedSlab.get())
                .unlockedBy("hasFramedFlatSlopeSlabCorner", has(FBContent.blockFramedFlatSlopeSlabCorner.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFlatElevatedInnerSlopeSlabCorner.get())
                .pattern("C")
                .pattern("S")
                .define('C', FBContent.blockFramedFlatInnerSlopeSlabCorner.get())
                .define('S', FBContent.blockFramedSlab.get())
                .unlockedBy("hasFramedFlatInnerSlopeSlabCorner", has(FBContent.blockFramedFlatInnerSlopeSlabCorner.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFlatDoubleSlopeSlabCorner.get())
                .pattern("C")
                .pattern("I")
                .define('C', FBContent.blockFramedFlatSlopeSlabCorner.get())
                .define('I', FBContent.blockFramedFlatInnerSlopeSlabCorner.get())
                .unlockedBy("hasFramedFlatSlopeSlabCorner", has(FBContent.blockFramedFlatSlopeSlabCorner.get()))
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedFlatInverseDoubleSlopeSlabCorner.get())
                .requires(FBContent.blockFramedFlatDoubleSlopeSlabCorner.get())
                .unlockedBy("hasFramedFlatDoubleSlopeSlabCorner", has(FBContent.blockFramedFlatDoubleSlopeSlabCorner.get()))
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedFlatDoubleSlopeSlabCorner.get())
                .requires(FBContent.blockFramedFlatInverseDoubleSlopeSlabCorner.get())
                .unlockedBy("hasFramedFlatInverseDoubleSlopeSlabCorner", has(FBContent.blockFramedFlatInverseDoubleSlopeSlabCorner.get()))
                .save(consumer, rl("framed_flat_double_slope_slab_corner_from_inverse"));

        shapedBuildingBlock(FBContent.blockFramedFlatElevatedDoubleSlopeSlabCorner.get())
                .pattern("C")
                .pattern("E")
                .define('C', FBContent.blockFramedFlatInnerSlopeSlabCorner.get())
                .define('E', FBContent.blockFramedFlatElevatedSlopeSlabCorner.get())
                .unlockedBy("hasFramedFlatElevatedSlopeSlabCorner", has(FBContent.blockFramedFlatElevatedSlopeSlabCorner.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFlatElevatedInnerDoubleSlopeSlabCorner.get())
                .pattern("C")
                .pattern("E")
                .define('C', FBContent.blockFramedFlatSlopeSlabCorner.get())
                .define('E', FBContent.blockFramedFlatElevatedInnerSlopeSlabCorner.get())
                .unlockedBy("hasFramedFlatElevatedInnerSlopeSlabCorner", has(FBContent.blockFramedFlatElevatedInnerSlopeSlabCorner.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedSlopePanel.get(), 6)
                .pattern("S")
                .pattern("S")
                .pattern("S")
                .define('S', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedExtendedSlopePanel.get())
                .pattern("PS")
                .define('P', FBContent.blockFramedPanel.get())
                .define('S', FBContent.blockFramedSlopePanel.get())
                .unlockedBy("hasFramedSlopePanel", has(FBContent.blockFramedSlopePanel.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedDoubleSlopePanel.get())
                .pattern("PP")
                .define('P', FBContent.blockFramedSlopePanel.get())
                .unlockedBy("hasFramedSlopePanel", has(FBContent.blockFramedSlopePanel.get()))
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedInverseDoubleSlopePanel.get())
                .requires(FBContent.blockFramedDoubleSlopePanel.get())
                .unlockedBy("hasFramedDoubleSlopePanel", has(FBContent.blockFramedDoubleSlopePanel.get()))
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedDoubleSlopePanel.get())
                .requires(FBContent.blockFramedInverseDoubleSlopePanel.get())
                .unlockedBy("hasFramedInverseDoubleSlopePanel", has(FBContent.blockFramedInverseDoubleSlopePanel.get()))
                .save(consumer, rl("framed_double_slope_panel_from_inverse_double_slope_panel"));

        shapedBuildingBlock(FBContent.blockFramedExtendedDoubleSlopePanel.get())
                .pattern("ES")
                .define('S', FBContent.blockFramedSlopePanel.get())
                .define('E', FBContent.blockFramedExtendedSlopePanel.get())
                .unlockedBy("hasFramedSlopePanel", has(FBContent.blockFramedSlopePanel.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFlatSlopePanelCorner.get())
                .pattern("HF ")
                .pattern("  F")
                .define('F', FBContent.blockFramedSlopePanel.get())
                .define('H', FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedSlopePanel", has(FBContent.blockFramedSlopePanel.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFlatInnerSlopePanelCorner.get())
                .pattern("H F")
                .pattern(" F ")
                .define('F', FBContent.blockFramedSlopePanel.get())
                .define('H', FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedSlopePanel", has(FBContent.blockFramedSlopePanel.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFlatExtendedSlopePanelCorner.get())
                .pattern("PC")
                .define('C', FBContent.blockFramedFlatSlopePanelCorner.get())
                .define('P', FBContent.blockFramedPanel.get())
                .unlockedBy("hasFramedFlatSlopePanelCorner", has(FBContent.blockFramedFlatSlopePanelCorner.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFlatExtendedInnerSlopePanelCorner.get())
                .pattern("PC")
                .define('C', FBContent.blockFramedFlatInnerSlopePanelCorner.get())
                .define('P', FBContent.blockFramedPanel.get())
                .unlockedBy("hasFramedFlatInnerSlopePanelCorner", has(FBContent.blockFramedFlatInnerSlopePanelCorner.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFlatDoubleSlopePanelCorner.get())
                .pattern("IC")
                .define('C', FBContent.blockFramedFlatSlopePanelCorner.get())
                .define('I', FBContent.blockFramedFlatInnerSlopePanelCorner.get())
                .unlockedBy("hasFramedFlatSlopePanelCorner", has(FBContent.blockFramedFlatSlopePanelCorner.get()))
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedFlatInverseDoubleSlopePanelCorner.get())
                .requires(FBContent.blockFramedFlatDoubleSlopePanelCorner.get())
                .unlockedBy("hasFramedFlatDoubleSlopePanelCorner", has(FBContent.blockFramedFlatDoubleSlopePanelCorner.get()))
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedFlatDoubleSlopePanelCorner.get())
                .requires(FBContent.blockFramedFlatInverseDoubleSlopePanelCorner.get())
                .unlockedBy("hasFramedFlatInverseDoubleSlopePanelCorner", has(FBContent.blockFramedFlatInverseDoubleSlopePanelCorner.get()))
                .save(consumer, rl("framed_flat_double_slope_panel_corner_from_flat_inverse_double_slope_panel_corner"));

        shapedBuildingBlock(FBContent.blockFramedFlatExtendedDoubleSlopePanelCorner.get())
                .pattern("CI")
                .define('C', FBContent.blockFramedFlatExtendedSlopePanelCorner.get())
                .define('I', FBContent.blockFramedFlatInnerSlopePanelCorner.get())
                .unlockedBy("hasFramedFlatExtendedSlopePanelCorner", has(FBContent.blockFramedFlatExtendedSlopePanelCorner.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFlatExtendedInnerDoubleSlopePanelCorner.get())
                .pattern("IC")
                .define('I', FBContent.blockFramedFlatExtendedInnerSlopePanelCorner.get())
                .define('C', FBContent.blockFramedFlatSlopePanelCorner.get())
                .unlockedBy("hasFramedFlatExtendedInnerSlopePanelCorner", has(FBContent.blockFramedFlatExtendedInnerSlopePanelCorner.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedDoubleStairs.get())
                .pattern("SE")
                .define('S', FBContent.blockFramedStairs.get())
                .define('E', FBContent.blockFramedSlabEdge.get())
                .unlockedBy("hasFramedStairs", has(FBContent.blockFramedStairs.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedVerticalDoubleStairs.get())
                .pattern("SE")
                .define('S', FBContent.blockFramedVerticalStairs.get())
                .define('E', FBContent.blockFramedCornerPillar.get())
                .unlockedBy("hasFramedStairs", has(FBContent.blockFramedVerticalStairs.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedWallBoard.get(), 4)
                .pattern("FFH")
                .define('F', FBContent.blockFramedPanel.get())
                .define('H', FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedPanel", has(FBContent.blockFramedPanel.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedGlowingCube.get())
                .pattern(" G ")
                .pattern("GCG")
                .pattern(" G ")
                .define('G', Tags.Items.DUSTS_GLOWSTONE)
                .define('C', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedCube", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedPyramid.get(), 4)
                .pattern("SS")
                .pattern("SS")
                .define('S', FBContent.blockFramedSlopePanel.get())
                .unlockedBy("hasFramedSlopePanel", has(FBContent.blockFramedSlopePanel.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedPyramidSlab.get(), 4)
                .pattern("SS")
                .pattern("SS")
                .define('S', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", has(FBContent.blockFramedSlope.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedHorizontalPane.get(), 4)
                .pattern("PP")
                .pattern("PP")
                .define('P', FBContent.blockFramedPane.get())
                .unlockedBy("hasFramedPane", has(FBContent.blockFramedPane.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedLargeButton.get())
                .pattern("BB")
                .pattern("BB")
                .define('B', FBContent.blockFramedButton.get())
                .unlockedBy("hasFramedButton", has(FBContent.blockFramedButton.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedLargeStoneButton.get())
                .pattern("BB")
                .pattern("BB")
                .define('B', FBContent.blockFramedStoneButton.get())
                .unlockedBy("hasFramedStoneButton", has(FBContent.blockFramedStoneButton.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedTarget.get())
                .pattern("FRF")
                .pattern("RHR")
                .pattern("FRF")
                .define('F', FBContent.blockFramedCube.get())
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('H', Items.HAY_BLOCK)
                .unlockedBy("hasFramedCube", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedGate.get(), 4)
                .pattern("DD")
                .pattern("DD")
                .define('D', FBContent.blockFramedDoor.get())
                .unlockedBy("hasFramedDoor", has(FBContent.blockFramedDoor.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedIronGate.get(), 4)
                .pattern("DD")
                .pattern("DD")
                .define('D', FBContent.blockFramedIronDoor.get())
                .unlockedBy("hasFramedIronDoor", has(FBContent.blockFramedIronDoor.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedItemFrame.get())
                .pattern("FFF")
                .pattern("FLF")
                .pattern("FFF")
                .define('F', FBContent.blockFramedCube.get())
                .define('L', Tags.Items.LEATHER)
                .unlockedBy("hasFramedCube", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedGlowingItemFrame.get())
                .requires(FBContent.blockFramedItemFrame.get())
                .requires(Items.GLOW_INK_SAC)
                .unlockedBy("hasFramedItemFrame", has(FBContent.blockFramedItemFrame.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFancyRail.get(), 16)
                .pattern("I I")
                .pattern("IFI")
                .pattern("I I")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFancyPoweredRail.get(), 6)
                .pattern("G G")
                .pattern("GFG")
                .pattern("GRG")
                .define('G', Tags.Items.INGOTS_GOLD)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFancyDetectorRail.get(), 6)
                .pattern("IPI")
                .pattern("IFI")
                .pattern("IRI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('P', Items.STONE_PRESSURE_PLATE)
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFancyActivatorRail.get(), 6)
                .pattern("IFI")
                .pattern("IRI")
                .pattern("IFI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', Items.REDSTONE_TORCH)
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFancyRailSlope.get())
                .pattern("R")
                .pattern("S")
                .define('R', FBContent.blockFramedFancyRail.get())
                .define('S', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFancyPoweredRailSlope.get())
                .pattern("R")
                .pattern("S")
                .define('R', FBContent.blockFramedFancyPoweredRail.get())
                .define('S', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFancyDetectorRailSlope.get())
                .pattern("R")
                .pattern("S")
                .define('R', FBContent.blockFramedFancyDetectorRail.get())
                .define('S', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedFancyActivatorRailSlope.get())
                .pattern("R")
                .pattern("S")
                .define('R', FBContent.blockFramedFancyActivatorRail.get())
                .define('S', FBContent.blockFramedSlope.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        shapelessBuildingBlock(FBContent.blockFramedHalfSlope.get(), 2)
                .requires(FBContent.blockFramedSlope.get())
                .requires(FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedSlope", HAS_FRAMED_SLOPE)
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedDividedSlope.get())
                .pattern("SS")
                .define('S', FBContent.blockFramedHalfSlope.get())
                .unlockedBy("hasFramedHalfSlope", has(FBContent.blockFramedHalfSlope.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedDoubleHalfSlope.get())
                .pattern("S")
                .pattern("S")
                .define('S', FBContent.blockFramedHalfSlope.get())
                .unlockedBy("hasFramedHalfSlope", has(FBContent.blockFramedHalfSlope.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedSlopedStairs.get())
                .pattern("H")
                .pattern("S")
                .define('H', FBContent.blockFramedHalfSlope.get())
                .define('S', FBContent.blockFramedSlab.get())
                .unlockedBy("hasFramedSlab", has(FBContent.blockFramedSlab.get()))
                .save(consumer);

        shapedBuildingBlock(FBContent.blockFramedVerticalSlopedStairs.get())
                .pattern("PH")
                .define('H', FBContent.blockFramedHalfSlope.get())
                .define('P', FBContent.blockFramedPanel.get())
                .unlockedBy("hasFramedPanel", has(FBContent.blockFramedPanel.get()))
                .save(consumer);



        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, FBContent.itemFramedHammer.get())
                .pattern(" F ")
                .pattern(" SF")
                .pattern("S  ")
                .define('F', FBContent.blockFramedCube.get())
                .define('S', Items.STICK)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, FBContent.itemFramedWrench.get())
                .pattern("F F")
                .pattern(" S ")
                .pattern(" S ")
                .define('F', FBContent.blockFramedCube.get())
                .define('S', Items.STICK)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, FBContent.itemFramedBlueprint.get())
                .pattern(" F ")
                .pattern("FPF")
                .pattern(" F ")
                .define('F', FBContent.blockFramedCube.get())
                .define('P', Items.PAPER)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, FBContent.itemFramedKey.get())
                .pattern("SSF")
                .pattern("NN ")
                .define('S', Tags.Items.RODS_WOODEN)
                .define('F', FBContent.blockFramedCube.get())
                .define('N', Tags.Items.NUGGETS_IRON)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, FBContent.itemFramedScrewdriver.get())
                .pattern("S ")
                .pattern(" F")
                .define('S', Tags.Items.RODS_WOODEN)
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);



        makeRotationRecipe(FBContent.blockFramedSlab, FBContent.blockFramedPanel, consumer);
        makeRotationRecipe(FBContent.blockFramedStairs, FBContent.blockFramedVerticalStairs, consumer);
        makeRotationRecipe(FBContent.blockFramedDoubleStairs, FBContent.blockFramedVerticalDoubleStairs, consumer);
        makeRotationRecipe(FBContent.blockFramedFloor, FBContent.blockFramedWallBoard, consumer);
        makeRotationRecipe(FBContent.blockFramedSlopeSlab, FBContent.blockFramedSlopePanel, consumer);
        makeRotationRecipe(FBContent.blockFramedElevatedSlopeSlab, FBContent.blockFramedExtendedSlopePanel, consumer);
        makeRotationRecipe(FBContent.blockFramedDoubleSlopeSlab, FBContent.blockFramedDoubleSlopePanel, consumer);
        makeRotationRecipe(FBContent.blockFramedInverseDoubleSlopeSlab, FBContent.blockFramedInverseDoubleSlopePanel, consumer);
        makeRotationRecipe(FBContent.blockFramedElevatedDoubleSlopeSlab, FBContent.blockFramedExtendedDoubleSlopePanel, consumer);
        makeRotationRecipe(FBContent.blockFramedFlatSlopeSlabCorner, FBContent.blockFramedFlatSlopePanelCorner, consumer);
        makeRotationRecipe(FBContent.blockFramedFlatInnerSlopeSlabCorner, FBContent.blockFramedFlatInnerSlopePanelCorner, consumer);
        makeRotationRecipe(FBContent.blockFramedFlatElevatedSlopeSlabCorner, FBContent.blockFramedFlatExtendedSlopePanelCorner, consumer);
        makeRotationRecipe(FBContent.blockFramedFlatElevatedInnerSlopeSlabCorner, FBContent.blockFramedFlatExtendedInnerSlopePanelCorner, consumer);
        makeRotationRecipe(FBContent.blockFramedFlatDoubleSlopeSlabCorner, FBContent.blockFramedFlatDoubleSlopePanelCorner, consumer);
        makeRotationRecipe(FBContent.blockFramedFlatInverseDoubleSlopeSlabCorner, FBContent.blockFramedFlatInverseDoubleSlopePanelCorner, consumer);
        makeRotationRecipe(FBContent.blockFramedFlatElevatedDoubleSlopeSlabCorner, FBContent.blockFramedFlatExtendedDoubleSlopePanelCorner, consumer);
        makeRotationRecipe(FBContent.blockFramedFlatElevatedInnerDoubleSlopeSlabCorner, FBContent.blockFramedFlatExtendedInnerDoubleSlopePanelCorner, consumer);
    }

    private static void makeRotationRecipe(RegistryObject<Block> first, RegistryObject<Block> second, Consumer<FinishedRecipe> consumer)
    {
        String name = first.getId().getPath() + "_rotate_to_" + second.getId().getPath();
        shapelessBuildingBlock(second.get())
                .requires(first.get())
                .requires(FBContent.itemFramedWrench.get())
                .unlockedBy("has_" + first.getId().getPath(), has(first.get()))
                .save(consumer, rl(name));

        name = second.getId().getPath() + "_rotate_to_" + first.getId().getPath();
        shapelessBuildingBlock(first.get())
                .requires(second.get())
                .requires(FBContent.itemFramedWrench.get())
                .unlockedBy("has_" + second.getId().getPath(), has(second.get()))
                .save(consumer, rl(name));
    }
    
    private static ShapedRecipeBuilder shapedBuildingBlock(ItemLike output)
    {
        return shapedBuildingBlock(output, 1);
    }

    private static ShapedRecipeBuilder shapedBuildingBlock(ItemLike output, int count)
    {
        return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, count);
    }

    private static ShapelessRecipeBuilder shapelessBuildingBlock(ItemLike output)
    {
        return shapelessBuildingBlock(output, 1);
    }

    private static ShapelessRecipeBuilder shapelessBuildingBlock(ItemLike output, int count)
    {
        return ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, output, count);
    }

    private static String rl(String path) { return FramedConstants.MOD_ID + ":" + path; }
}