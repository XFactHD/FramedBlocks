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
    private final ICriterionInstance HAS_FRAMED_BLOCK = hasItem(FBContent.blockFramedCube);
    private final ICriterionInstance HAS_FRAMED_SLOPE = hasItem(FBContent.blockFramedSlope);

    public FramedRecipeProvider(DataGenerator gen) { super(gen); }

    @Override
    public String getName() { return super.getName() + ": " + FramedBlocks.MODID; }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
    {
        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedCube, 4)
                .patternLine("PSP")
                .patternLine("S S")
                .patternLine("PSP")
                .key('P', ItemTags.PLANKS)
                .key('S', Items.STICK)
                .addCriterion("hasPlanks", hasItem(ItemTags.PLANKS))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedSlope, 3)
                .patternLine("F ")
                .patternLine("FF")
                .key('F', FBContent.blockFramedCube)
                .addCriterion("hasFramedBlock", HAS_FRAMED_BLOCK)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedCornerSlope)
                .patternLine("HF ")
                .patternLine("  F")
                .key('F', FBContent.blockFramedSlope)
                .key('H', FBContent.itemFramedHammer)
                .addCriterion("hasFramedSlope", HAS_FRAMED_SLOPE)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedInnerCornerSlope)
                .patternLine("H F")
                .patternLine(" F ")
                .key('F', FBContent.blockFramedSlope)
                .key('H', FBContent.itemFramedHammer)
                .addCriterion("hasFramedSlope", HAS_FRAMED_SLOPE)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedPrismCorner)
                .patternLine("F F")
                .patternLine(" F ")
                .key('F', FBContent.blockFramedSlope)
                .addCriterion("hasFramedSlope", HAS_FRAMED_SLOPE)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedInnerPrismCorner)
                .patternLine(" F ")
                .patternLine("F F")
                .key('F', FBContent.blockFramedSlope)
                .addCriterion("hasFramedSlope", HAS_FRAMED_SLOPE)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedThreewayCorner)
                .patternLine("F ")
                .patternLine("FF")
                .key('F', FBContent.blockFramedSlope)
                .addCriterion("hasFramedSlope", HAS_FRAMED_SLOPE)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedInnerThreewayCorner)
                .patternLine("FF")
                .patternLine("F ")
                .key('F', FBContent.blockFramedSlope)
                .addCriterion("hasFramedSlope", HAS_FRAMED_SLOPE)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedSlab, 6)
                .patternLine("FFF")
                .key('F', FBContent.blockFramedCube)
                .addCriterion("hasFramedBlock", HAS_FRAMED_BLOCK)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedSlabEdge, 6)
                .patternLine("FFF")
                .key('F', FBContent.blockFramedSlab)
                .addCriterion("hasFramedSlab", hasItem(FBContent.blockFramedSlab))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedSlabCorner, 8)
                .patternLine("FF")
                .patternLine("FF")
                .key('F', FBContent.blockFramedSlab)
                .addCriterion("hasFramedSlab", hasItem(FBContent.blockFramedSlab))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedPanel, 6)
                .patternLine("F")
                .patternLine("F")
                .patternLine("F")
                .key('F', FBContent.blockFramedCube)
                .addCriterion("hasFramedBlock", HAS_FRAMED_BLOCK)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedCornerPillar, 4)
                .patternLine("F")
                .patternLine("F")
                .key('F', FBContent.blockFramedCube)
                .addCriterion("hasFramedBlock", HAS_FRAMED_BLOCK)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedStairs, 4)
                .patternLine("F  ")
                .patternLine("FF ")
                .patternLine("FFF")
                .key('F', FBContent.blockFramedCube)
                .addCriterion("hasFramedBlock", HAS_FRAMED_BLOCK)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedWall, 6)
                .patternLine("FFF")
                .patternLine("FFF")
                .key('F', FBContent.blockFramedCube)
                .addCriterion("hasFramedBlock", HAS_FRAMED_BLOCK)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedFence, 3)
                .patternLine("FSF")
                .patternLine("FSF")
                .key('F', FBContent.blockFramedCube)
                .key('S', Items.STICK)
                .addCriterion("hasFramedBlock", HAS_FRAMED_BLOCK)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedGate)
                .patternLine("SFS")
                .patternLine("SFS")
                .key('F', FBContent.blockFramedCube)
                .key('S', Items.STICK)
                .addCriterion("hasFramedBlock", HAS_FRAMED_BLOCK)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedDoor)
                .patternLine("FF")
                .patternLine("FF")
                .patternLine("FF")
                .key('F', FBContent.blockFramedCube)
                .addCriterion("hasFramedBlock", HAS_FRAMED_BLOCK)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedTrapDoor)
                .patternLine("FFF")
                .patternLine("FFF")
                .key('F', FBContent.blockFramedSlab)
                .addCriterion("hasFramedSlab", hasItem(FBContent.blockFramedSlab))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedPressurePlate)
                .patternLine("FF")
                .key('F', FBContent.blockFramedCube)
                .addCriterion("hasFramedBlock", HAS_FRAMED_BLOCK)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedLadder, 3)
                .patternLine("F F")
                .patternLine("FSF")
                .patternLine("F F")
                .key('F', FBContent.blockFramedCube)
                .key('S', Items.STICK)
                .addCriterion("hasFramedBlock", HAS_FRAMED_BLOCK)
                .build(consumer);

        ShapelessRecipeBuilder.shapelessRecipe(FBContent.blockFramedButton)
                .addIngredient(FBContent.blockFramedCube)
                .addCriterion("hasFramedBlock", hasItem(FBContent.blockFramedCube))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedLever)
                .patternLine("S")
                .patternLine("F")
                .key('F', FBContent.blockFramedCube)
                .key('S', Items.STICK)
                .addCriterion("hasFramedBlock", HAS_FRAMED_BLOCK)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedSign, 3)
                .patternLine("FFF")
                .patternLine("FFF")
                .patternLine(" S ")
                .key('F', FBContent.blockFramedCube)
                .key('S', Items.STICK)
                .addCriterion("hasFramedBlock", HAS_FRAMED_BLOCK)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedDoubleSlope, 1)
                .patternLine("FF")
                .key('F', FBContent.blockFramedSlope)
                .addCriterion("hasFramedSlope", HAS_FRAMED_SLOPE)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedDoubleCorner, 1)
                .patternLine("IC")
                .key('C', FBContent.blockFramedCornerSlope)
                .key('I', FBContent.blockFramedInnerCornerSlope)
                .addCriterion("hasFramedCorner", hasItem(FBContent.blockFramedCornerSlope))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedDoublePrismCorner, 1)
                .patternLine("IC")
                .key('C', FBContent.blockFramedPrismCorner)
                .key('I', FBContent.blockFramedInnerPrismCorner)
                .addCriterion("hasFramedCorner", hasItem(FBContent.blockFramedPrismCorner))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedDoubleThreewayCorner, 1)
                .patternLine("IC")
                .key('C', FBContent.blockFramedThreewayCorner)
                .key('I', FBContent.blockFramedInnerThreewayCorner)
                .addCriterion("hasFramedCorner", hasItem(FBContent.blockFramedThreewayCorner))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedTorch, 4)
                .patternLine("C")
                .patternLine("F")
                .key('C', ItemTags.COALS)
                .key('F', FBContent.blockFramedCube)
                .addCriterion("hasFramedBlock", HAS_FRAMED_BLOCK)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedSoulTorch, 4)
                .patternLine("C")
                .patternLine("F")
                .patternLine("S")
                .key('C', ItemTags.COALS)
                .key('F', FBContent.blockFramedCube)
                .key('S', ItemTags.SOUL_FIRE_BASE_BLOCKS)
                .addCriterion("hasFramedBlock", HAS_FRAMED_BLOCK)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedFloor, 3)
                .patternLine("FFH")
                .key('F', FBContent.blockFramedCube)
                .key('H', FBContent.itemFramedHammer)
                .addCriterion("hasFramedBlock", HAS_FRAMED_BLOCK)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedLattice, 3)
                .patternLine(" F ")
                .patternLine("FFF")
                .patternLine(" F ")
                .key('F', FBContent.blockFramedCube)
                .addCriterion("hasFramedBlock", HAS_FRAMED_BLOCK)
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedVerticalStairs, 4)
                .patternLine("FFF")
                .patternLine("FF ")
                .patternLine("F  ")
                .key('F', FBContent.blockFramedCube)
                .addCriterion("hasFramedBlock", HAS_FRAMED_BLOCK)
                .build(consumer);

        /*ShapedRecipeBuilder.shapedRecipe(FBContent.blockFramedCollapsibleBlock, 4)
                .patternLine("FF")
                .patternLine("FF")
                .key('F', FBContent.blockFramedCube)
                .addCriterion("hasFramedBlock", HAS_FRAMED_BLOCK)
                .build(consumer);*/



        ShapedRecipeBuilder.shapedRecipe(FBContent.itemFramedHammer)
                .patternLine(" F ")
                .patternLine(" SF")
                .patternLine("S  ")
                .key('F', FBContent.blockFramedCube)
                .key('S', Items.STICK)
                .addCriterion("hasFramedBlock", HAS_FRAMED_BLOCK)
                .build(consumer);
    }
}