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
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CamoApplicationRecipe extends CustomRecipe {
    public static final MapCodec<CamoApplicationRecipe> CODEC = Ingredient.CODEC.fieldOf("copy_tool")
            .xmap(CamoApplicationRecipe::new, CamoApplicationRecipe::getCopyTool);
    public static final StreamCodec<RegistryFriendlyByteBuf, CamoApplicationRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            CamoApplicationRecipe::getCopyTool,
            CamoApplicationRecipe::new
    );

    private final Ingredient copyTool;

    public CamoApplicationRecipe(Ingredient copyTool) {
        this.copyTool = copyTool;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() != 2 || input.height() != 2 || !copyTool.test(input.getItem(1, 0))) {
            return false;
        }

        ItemStack blockStack = input.getItem(0, 0);
        if (!(blockStack.getItem() instanceof BlockItem item) || !(item.getBlock() instanceof IFramedBlock block)) {
            return false;
        }

        boolean consume = ConfigView.Server.INSTANCE.shouldConsumeCamoItem();

        ItemStack camoOneStack = input.getItem(0, 1);
        boolean camoOne = false;
        if (!camoOneStack.isEmpty()) {
            CamoCraftingHandler<?> handlerOne = CamoContainerHelper.findCraftingHandler(camoOneStack);
            if (handlerOne == null || !handlerOne.canApply(camoOneStack, consume)) {
                return false;
            }
            camoOne = true;
        }

        ItemStack camoTwoStack = input.getItem(1, 1);
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
        if (input.width() != 2 || input.height() != 2 || !copyTool.test(input.getItem(1, 0))) {
            return ItemStack.EMPTY;
        }

        ItemStack blockStack = input.getItem(0, 0);
        if (!(blockStack.getItem() instanceof BlockItem item) || !(item.getBlock() instanceof IFramedBlock block)) {
            return ItemStack.EMPTY;
        }

        boolean consume = ConfigView.Server.INSTANCE.shouldConsumeCamoItem();
        List<CamoContainer<?, ?>> camos = new ArrayList<>(2);

        ItemStack camoOneStack = input.getItem(0, 1);
        if (!camoOneStack.isEmpty()) {
            CamoCraftingHandler<?> handlerOne = CamoContainerHelper.findCraftingHandler(camoOneStack);
            if (handlerOne == null || !handlerOne.canApply(camoOneStack, consume)) {
                return ItemStack.EMPTY;
            }
            camos.add(handlerOne.apply(camoOneStack, consume));
        } else if (block.getBlockType().consumesTwoCamosInCamoApplicationRecipe()) {
            camos.add(EmptyCamoContainer.EMPTY);
        }

        ItemStack camoTwoStack = input.getItem(1, 1);
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
        if (remaining.get(1).isEmpty()) {
            remaining.set(1, input.getItem(1, 0).copy());
        }

        boolean consume = ConfigView.Server.INSTANCE.shouldConsumeCamoItem();

        ItemStack camoOneStack = input.getItem(0, 1);
        if (!camoOneStack.isEmpty()) {
            CamoCraftingHandler<?> handlerOne = CamoContainerHelper.findCraftingHandler(camoOneStack);
            remaining.set(2, Objects.requireNonNull(handlerOne).getRemainder(camoOneStack, consume));
        }

        ItemStack camoTwoStack = input.getItem(1, 1);
        if (!camoTwoStack.isEmpty()) {
            CamoCraftingHandler<?> handlerTwo = CamoContainerHelper.findCraftingHandler(camoTwoStack);
            remaining.set(3, Objects.requireNonNull(handlerTwo).getRemainder(camoTwoStack, consume));
        }

        return remaining;
    }

    public Ingredient getCopyTool() {
        return copyTool;
    }

    @Override
    public RecipeSerializer<CamoApplicationRecipe> getSerializer() {
        return FBContent.RECIPE_SERIALIZER_APPLY_CAMO.value();
    }
}
