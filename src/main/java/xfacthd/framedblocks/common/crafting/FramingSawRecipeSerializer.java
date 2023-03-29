package xfacthd.framedblocks.common.crafting;

import com.google.gson.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.type.IBlockType;

import java.util.ArrayList;
import java.util.List;

public final class FramingSawRecipeSerializer implements RecipeSerializer<FramingSawRecipe>
{
    @Override
    public FramingSawRecipe fromJson(ResourceLocation recipeId, JsonObject json)
    {
        boolean disabled = GsonHelper.getAsBoolean(json, "disabled", false);

        int material = GsonHelper.getAsInt(json, "material");
        if (material <= 0)
        {
            throw new JsonSyntaxException("Value of 'material' must be greater than 0");
        }

        List<FramingSawRecipeAdditive> additives = new ArrayList<>();
        if (json.has("additives"))
        {
            JsonArray additiveArr = GsonHelper.getAsJsonArray(json, "additives");
            if (additiveArr.size() > FramingSawRecipe.MAX_ADDITIVE_COUNT)
            {
                throw new JsonSyntaxException("More than 3 additives are not supported");
            }

            for (JsonElement additiveElem : additiveArr)
            {
                JsonObject additiveObj = additiveElem.getAsJsonObject();
                Ingredient additive = Ingredient.fromJson(additiveObj.get("ingredient"));
                if (!additive.isSimple())
                {
                    throw new JsonSyntaxException("Custom ingredients are not supported");
                }
                int additiveCount = GsonHelper.getAsInt(additiveObj, "count");
                if (additiveCount <= 0)
                {
                    throw new JsonSyntaxException("Value of 'additive_count' must be greater than 0");
                }
                additives.add(new FramingSawRecipeAdditive(additive, additiveCount));
            }
        }

        JsonObject resultObj = GsonHelper.getAsJsonObject(json, "result");
        ItemStack result = CraftingHelper.getItemStack(resultObj, false);
        IBlockType resultType = findResultType(result);

        return new FramingSawRecipe(recipeId, material, additives, result, resultType, disabled);
    }

    @Override
    public FramingSawRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
    {
        int material = buffer.readInt();

        int count = buffer.readInt();
        List<FramingSawRecipeAdditive> additives = new ArrayList<>(count);
        for (int i = 0; i < count; i++)
        {
            Ingredient additive = Ingredient.fromNetwork(buffer);
            int additiveCount = buffer.readInt();
            additives.add(new FramingSawRecipeAdditive(additive, additiveCount));
        }

        ItemStack result = buffer.readItem();
        IBlockType resultType = findResultType(result);
        boolean disabled = buffer.readBoolean();

        return new FramingSawRecipe(recipeId, material, additives, result, resultType, disabled);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, FramingSawRecipe recipe)
    {
        buffer.writeInt(recipe.getMaterialAmount());

        List<FramingSawRecipeAdditive> additives = recipe.getAdditives();
        buffer.writeInt(additives.size());
        for (FramingSawRecipeAdditive additive : additives)
        {
            additive.ingredient().toNetwork(buffer);
            buffer.writeInt(additive.count());
        }

        buffer.writeItem(recipe.getResult());
        buffer.writeBoolean(recipe.isDisabled());
    }

    private static IBlockType findResultType(ItemStack result)
    {
        if (!(result.getItem() instanceof BlockItem item))
        {
            throw new JsonSyntaxException("Result items must be BlockItems");
        }
        if (!(item.getBlock() instanceof IFramedBlock block))
        {
            throw new JsonSyntaxException("Block of result items must be IFramedBlocks");
        }
        return block.getBlockType();
    }
}
