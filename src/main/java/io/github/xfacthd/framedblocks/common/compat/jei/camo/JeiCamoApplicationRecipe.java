package io.github.xfacthd.framedblocks.common.compat.jei.camo;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record JeiCamoApplicationRecipe(
        Ingredient frame,
        Ingredient copyTool,
        Ingredient camoOne,
        Ingredient camoTwo,
        Optional<ItemStackTemplate> result
) implements CraftingRecipe
{
    public static final MapCodec<JeiCamoApplicationRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("frame").forGetter(JeiCamoApplicationRecipe::frame),
            Ingredient.CODEC.fieldOf("copy_tool").forGetter(JeiCamoApplicationRecipe::copyTool),
            Ingredient.CODEC.fieldOf("camo_one").forGetter(JeiCamoApplicationRecipe::camoOne),
            Ingredient.CODEC.fieldOf("camo_two").forGetter(JeiCamoApplicationRecipe::camoTwo),
            ItemStackTemplate.CODEC.optionalFieldOf("result").forGetter(JeiCamoApplicationRecipe::result)
    ).apply(inst, JeiCamoApplicationRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, JeiCamoApplicationRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            JeiCamoApplicationRecipe::frame,
            Ingredient.CONTENTS_STREAM_CODEC,
            JeiCamoApplicationRecipe::copyTool,
            Ingredient.CONTENTS_STREAM_CODEC,
            JeiCamoApplicationRecipe::camoOne,
            Ingredient.CONTENTS_STREAM_CODEC,
            JeiCamoApplicationRecipe::camoTwo,
            ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC),
            JeiCamoApplicationRecipe::result,
            JeiCamoApplicationRecipe::new
    );

    @Override
    public CraftingBookCategory category()
    {
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean matches(CraftingInput craftingInput, Level level)
    {
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput craftingInput)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean showNotification()
    {
        return false;
    }

    @Override
    public String group()
    {
        return "";
    }

    @Override
    public PlacementInfo placementInfo()
    {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public List<RecipeDisplay> display()
    {
        return List.of(new ShapedCraftingRecipeDisplay(
                2,
                2,
                Stream.of(frame, copyTool, camoOne, camoTwo).map(Ingredient::display).toList(),
                result.<SlotDisplay>map(SlotDisplay.ItemStackSlotDisplay::new).orElse(SlotDisplay.Empty.INSTANCE),
                new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
        ));
    }

    @Override
    public RecipeSerializer<JeiCamoApplicationRecipe> getSerializer()
    {
        return FBContent.RECIPE_SERIALIZER_JEI_CAMO.value();
    }
}
