package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.common.FBContent;

import java.util.function.Consumer;

public class FramedRecipeProvider extends RecipeProvider
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
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
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
                .save(consumer, rl("framed_corner_pillar_from_pillar"));

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedHalfPillar.get(), 1)
                .requires(FBContent.blockFramedSlabCorner.get())
                .unlockedBy("hasFramedSlabCorner", has(FBContent.blockFramedSlabCorner.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedSlabCorner.get(), 1)
                .requires(FBContent.blockFramedHalfPillar.get())
                .unlockedBy("hasFramedHalfPillar", has(FBContent.blockFramedHalfPillar.get()))
                .save(consumer, rl("framed_slab_corner_from_half_pillar"));

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedPost.get(), 1)
                .requires(FBContent.blockFramedFence.get())
                .unlockedBy("hasFramedFence", has(FBContent.blockFramedFence.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedFence.get(), 1)
                .requires(FBContent.blockFramedPost.get())
                .unlockedBy("hasFramedPost", has(FBContent.blockFramedPost.get()))
                .save(consumer, rl("framed_fence_from_post"));

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

        ShapedRecipeBuilder.shaped(FBContent.blockFramedStairs.get())
                .pattern("SS")
                .define('S', FBContent.blockFramedHalfStairs.get())
                .unlockedBy("hasFramedHalfStairs", has(FBContent.blockFramedHalfStairs.get()))
                .save(consumer, rl("framed_stairs_from_half_stairs"));

        ShapedRecipeBuilder.shaped(FBContent.blockFramedBouncyCube.get())
                .pattern(" S ")
                .pattern("SCS")
                .pattern(" S ")
                .define('S', Tags.Items.SLIMEBALLS)
                .define('C', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedSecretStorage.get())
                .pattern("FFF")
                .pattern("FCF")
                .pattern("FFF")
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

        ShapedRecipeBuilder.shaped(FBContent.blockFramedSlopedPrism.get(), 2)
                .pattern("FF")
                .define('F', FBContent.blockFramedCornerSlope.get())
                .unlockedBy("hasFramedCorner", has(FBContent.blockFramedCornerSlope.get()))
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
                .save(consumer, rl("framed_double_slope_slab_from_inverse"));

        ShapedRecipeBuilder.shaped(FBContent.blockFramedElevatedDoubleSlopeSlab.get())
                .pattern("S")
                .pattern("E")
                .define('S', FBContent.blockFramedSlopeSlab.get())
                .define('E', FBContent.blockFramedElevatedSlopeSlab.get())
                .unlockedBy("hasFramedSlopeSlab", has(FBContent.blockFramedSlopeSlab.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedVerticalHalfStairs.get(), 2)
                .requires(FBContent.blockFramedVerticalStairs.get())
                .requires(FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedVerticalStairs", has(FBContent.blockFramedVerticalStairs.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedSlopePanel.get())
                .requires(FBContent.blockFramedSlopeSlab.get())
                .requires(FBContent.itemFramedWrench.get())
                .unlockedBy("hasFramedSlopeSlab", has(FBContent.blockFramedSlopeSlab.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedExtendedSlopePanel.get())
                .requires(FBContent.blockFramedElevatedSlopeSlab.get())
                .requires(FBContent.itemFramedWrench.get())
                .unlockedBy("hasFramedSlopeElevatedSlab", has(FBContent.blockFramedElevatedSlopeSlab.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedDoubleSlopePanel.get())
                .requires(FBContent.blockFramedDoubleSlopeSlab.get())
                .requires(FBContent.itemFramedWrench.get())
                .unlockedBy("hasFramedDoubleSlopeSlab", has(FBContent.blockFramedDoubleSlopeSlab.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedInverseDoubleSlopePanel.get())
                .requires(FBContent.blockFramedInverseDoubleSlopeSlab.get())
                .requires(FBContent.itemFramedWrench.get())
                .unlockedBy("hasFramedInverseDoubleSlopeSlab", has(FBContent.blockFramedInverseDoubleSlopeSlab.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedSlopeSlab.get())
                .requires(FBContent.blockFramedSlopePanel.get())
                .requires(FBContent.itemFramedWrench.get())
                .unlockedBy("hasFramedSlopePanel", has(FBContent.blockFramedSlopePanel.get()))
                .save(consumer, rl("framed_slope_slab_from_slope_panel"));

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedElevatedSlopeSlab.get())
                .requires(FBContent.blockFramedExtendedSlopePanel.get())
                .requires(FBContent.itemFramedWrench.get())
                .unlockedBy("hasFramedExtendedSlopePanel", has(FBContent.blockFramedExtendedSlopePanel.get()))
                .save(consumer, rl("framed_elevated_slope_slab_from_extended_slope_panel"));

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedDoubleSlopeSlab.get())
                .requires(FBContent.blockFramedDoubleSlopePanel.get())
                .requires(FBContent.itemFramedWrench.get())
                .unlockedBy("hasFramedDoubleSlopePanel", has(FBContent.blockFramedDoubleSlopePanel.get()))
                .save(consumer, rl("framed_double_slope_slab_from_double_slope_panel"));

        ShapelessRecipeBuilder.shapeless(FBContent.blockFramedInverseDoubleSlopeSlab.get())
                .requires(FBContent.blockFramedInverseDoubleSlopePanel.get())
                .requires(FBContent.itemFramedWrench.get())
                .unlockedBy("hasFramedInverseDoubleSlopePanel", has(FBContent.blockFramedInverseDoubleSlopePanel.get()))
                .save(consumer, rl("framed_inverse_double_slope_slab_from_inverse_double_slope_panel"));

        ShapedRecipeBuilder.shaped(FBContent.blockFramedExtendedDoubleSlopePanel.get())
                .pattern("SE")
                .define('S', FBContent.blockFramedSlopePanel.get())
                .define('E', FBContent.blockFramedExtendedSlopePanel.get())
                .unlockedBy("hasFramedSlopePanel", has(FBContent.blockFramedSlopePanel.get()))
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
    }

    private static String rl(String path) { return FramedConstants.MOD_ID + ":" + path; }
}