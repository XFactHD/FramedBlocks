package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.*;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;

import java.util.function.Consumer;

public class FramedRecipeProvider extends RecipeProvider
{
    private final ICriterionInstance HAS_FRAMED_BLOCK = has(FBContent.blockFramedCube.get());
    private final ICriterionInstance HAS_FRAMED_SLOPE = has(FBContent.blockFramedSlope.get());

    public FramedRecipeProvider(DataGenerator gen) { super(gen); }

    @Override
    public String getName() { return super.getName() + ": " + FramedBlocks.MODID; }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer)
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

        ShapedRecipeBuilder.shaped(FBContent.blockFramedGate.get())
                .pattern("SFS")
                .pattern("SFS")
                .define('F', FBContent.blockFramedCube.get())
                .define('S', Items.STICK)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedDoor.get())
                .pattern("FF")
                .pattern("FF")
                .pattern("FF")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedTrapDoor.get())
                .pattern("FFF")
                .pattern("FFF")
                .define('F', FBContent.blockFramedSlab.get())
                .unlockedBy("hasFramedSlab", has(FBContent.blockFramedSlab.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(FBContent.blockFramedPressurePlate.get())
                .pattern("FF")
                .define('F', FBContent.blockFramedCube.get())
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

        ShapedRecipeBuilder.shaped(FBContent.blockFramedFloor.get(), 3)
                .pattern("FFH")
                .define('F', FBContent.blockFramedCube.get())
                .define('H', FBContent.itemFramedHammer.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
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

        /*ShapedRecipeBuilder.shaped(FBContent.blockFramedCollapsibleBlock.get(), 4)
                .pattern("FF")
                .pattern("FF")
                .define('F', FBContent.blockFramedCube.get())
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);*/



        ShapedRecipeBuilder.shaped(FBContent.itemFramedHammer.get())
                .pattern(" F ")
                .pattern(" SF")
                .pattern("S  ")
                .define('F', FBContent.blockFramedCube.get())
                .define('S', Items.STICK)
                .unlockedBy("hasFramedBlock", HAS_FRAMED_BLOCK)
                .save(consumer);
    }
}