package io.github.xfacthd.framedblocks.common.crafting.camo;

import com.mojang.serialization.MapCodec;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerHelper;
import io.github.xfacthd.framedblocks.api.camo.CamoCraftingHandler;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import io.github.xfacthd.framedblocks.api.util.ConfigView;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.Optionull;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class CamoApplicationRecipe extends CustomRecipe {
    public static final MapCodec<CamoApplicationRecipe> CODEC = Ingredient.CODEC.fieldOf("copy_tool")
            .xmap(CamoApplicationRecipe::new, CamoApplicationRecipe::getCopyTool);
    public static final StreamCodec<RegistryFriendlyByteBuf, CamoApplicationRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            CamoApplicationRecipe::getCopyTool,
            CamoApplicationRecipe::new
    );
    private static final int WIDTH = 2;
    private static final int HEIGHT = 2;
    private static final int SIZE = WIDTH * HEIGHT;
    private static final int FRAME_X = 0;
    private static final int FRAME_Y = 0;
    private static final int FRAME_INDEX = FRAME_Y * WIDTH + FRAME_X;
    private static final int TOOL_X = 1;
    private static final int TOOL_Y = 0;
    private static final int TOOL_INDEX = TOOL_Y * WIDTH + TOOL_X;
    private static final int CAMO_ONE_X = 0;
    private static final int CAMO_ONE_Y = 1;
    private static final int CAMO_ONE_INDEX = CAMO_ONE_Y * WIDTH + CAMO_ONE_X;
    private static final int CAMO_TWO_X = 1;
    private static final int CAMO_TWO_Y = 1;
    private static final int CAMO_TWO_INDEX = CAMO_TWO_Y * WIDTH + CAMO_TWO_X;

    private final Ingredient copyTool;

    public CamoApplicationRecipe(Ingredient copyTool) {
        this.copyTool = copyTool;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() != WIDTH || input.height() != HEIGHT || !copyTool.test(input.getItem(TOOL_X, TOOL_Y))) {
            return false;
        }

        ItemStack blockStack = input.getItem(FRAME_X, FRAME_Y);
        if (!(blockStack.getItem() instanceof BlockItem item) || !(item.getBlock() instanceof IFramedBlock block)) {
            return false;
        }

        boolean consume = ConfigView.Synced.INSTANCE.shouldConsumeCamoItem();

        ItemStack camoOneStack = input.getItem(CAMO_ONE_X, CAMO_ONE_Y);
        boolean camoOne = false;
        if (!camoOneStack.isEmpty()) {
            CamoCraftingHandler<?> handlerOne = CamoContainerHelper.findCraftingHandler(camoOneStack);
            if (handlerOne == null || !handlerOne.canApply(camoOneStack, consume)) {
                return false;
            }
            camoOne = true;
        }

        ItemStack camoTwoStack = input.getItem(CAMO_TWO_X, CAMO_TWO_Y);
        boolean camoTwo = false;
        if (!camoTwoStack.isEmpty()) {
            CamoCraftingHandler<?> handlerTwo = CamoContainerHelper.findCraftingHandler(camoTwoStack);
            if (handlerTwo == null || !handlerTwo.canApply(camoTwoStack, consume)) {
                return false;
            }
            camoTwo = true;
        }

        boolean doubleBlock = block.getBlockType().consumesTwoCamosInCamoApplicationRecipe();
        return doubleBlock ? (camoOne || camoTwo) : (camoOne ^ camoTwo);
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        if (input.width() != WIDTH || input.height() != HEIGHT || !copyTool.test(input.getItem(TOOL_X, TOOL_Y))) {
            return ItemStack.EMPTY;
        }

        ItemStack blockStack = input.getItem(FRAME_X, FRAME_Y);
        if (!(blockStack.getItem() instanceof BlockItem item) || !(item.getBlock() instanceof IFramedBlock block)) {
            return ItemStack.EMPTY;
        }

        boolean consume = ConfigView.Synced.INSTANCE.shouldConsumeCamoItem();
        List<CamoContainer<?, ?>> camos = new ArrayList<>(2);

        ItemStack camoOneStack = input.getItem(CAMO_ONE_X, CAMO_ONE_Y);
        if (!camoOneStack.isEmpty()) {
            CamoCraftingHandler<?> handlerOne = CamoContainerHelper.findCraftingHandler(camoOneStack);
            if (handlerOne == null || !handlerOne.canApply(camoOneStack, consume)) {
                return ItemStack.EMPTY;
            }
            camos.add(handlerOne.apply(camoOneStack, consume));
        } else if (block.getBlockType().consumesTwoCamosInCamoApplicationRecipe()) {
            camos.add(EmptyCamoContainer.EMPTY);
        }

        ItemStack camoTwoStack = input.getItem(CAMO_TWO_X, CAMO_TWO_Y);
        if (!camoTwoStack.isEmpty()) {
            CamoCraftingHandler<?> handlerTwo = CamoContainerHelper.findCraftingHandler(camoTwoStack);
            if (handlerTwo == null || !handlerTwo.canApply(camoTwoStack, consume)) {
                return ItemStack.EMPTY;
            }
            camos.add(handlerTwo.apply(camoTwoStack, consume));
        }

        ItemStack result = blockStack.copyWithCount(1);
        result.set(FBContent.DC_TYPE_CAMO_LIST, CamoList.of(camos));
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = super.getRemainingItems(input);
        if (remaining.get(TOOL_INDEX).isEmpty()) {
            remaining.set(TOOL_INDEX, input.getItem(TOOL_X, TOOL_Y).copy());
        }

        boolean consume = ConfigView.Synced.INSTANCE.shouldConsumeCamoItem();

        ItemStack camoOneStack = input.getItem(CAMO_ONE_X, CAMO_ONE_Y);
        if (!camoOneStack.isEmpty()) {
            CamoCraftingHandler<?> handlerOne = CamoContainerHelper.findCraftingHandler(camoOneStack);
            remaining.set(CAMO_ONE_INDEX, Objects.requireNonNull(handlerOne).getRemainder(camoOneStack, consume));
        }

        ItemStack camoTwoStack = input.getItem(CAMO_TWO_X, CAMO_TWO_Y);
        if (!camoTwoStack.isEmpty()) {
            CamoCraftingHandler<?> handlerTwo = CamoContainerHelper.findCraftingHandler(camoTwoStack);
            remaining.set(CAMO_TWO_INDEX, Objects.requireNonNull(handlerTwo).getRemainder(camoTwoStack, consume));
        }

        return remaining;
    }

    public Ingredient getCopyTool() {
        return copyTool;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(makeDisplay(new ItemStackTemplate(Items.RESIN_BRICKS)), makeDisplay(new ItemStackTemplate(Items.WATER_BUCKET)));
    }

    private RecipeDisplay makeDisplay(ItemStackTemplate camo) {
        ItemStackTemplate[] ingredientTemplates = new ItemStackTemplate[SIZE];
        ingredientTemplates[FRAME_INDEX] = new ItemStackTemplate(FBContent.BLOCK_FRAMED_SLOPE.value().asItem());
        ingredientTemplates[TOOL_INDEX] = new ItemStackTemplate(copyTool.getSingleItem().orElseThrow());
        ingredientTemplates[CAMO_ONE_INDEX] = camo;
        List<SlotDisplay> ingredients = mapIngredients(ingredientTemplates, SlotDisplay.ItemStackSlotDisplay::new, SlotDisplay.Empty.INSTANCE);
        CraftingInput input = CraftingInput.of(WIDTH, HEIGHT, mapIngredients(ingredientTemplates, ItemStackTemplate::create, ItemStack.EMPTY));
        ItemStackTemplate result = ItemStackTemplate.fromStack(assemble(input));
        return new ShapedCraftingRecipeDisplay(WIDTH, HEIGHT, ingredients, new SlotDisplay.ItemStackSlotDisplay(result), new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE));
    }

    private static <T> List<T> mapIngredients(ItemStackTemplate[] templates, Function<ItemStackTemplate, T> mapper, T defaultValue) {
        List<T> result = new ArrayList<>(templates.length);
        for (ItemStackTemplate template : templates) {
            result.add(Optionull.mapOrDefault(template, mapper, defaultValue));
        }
        return result;
    }

    @Override
    public RecipeSerializer<CamoApplicationRecipe> getSerializer() {
        return FBContent.RECIPE_SERIALIZER_APPLY_CAMO.value();
    }
}
