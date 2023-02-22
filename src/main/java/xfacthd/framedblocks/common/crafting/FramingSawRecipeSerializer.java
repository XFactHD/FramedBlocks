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

        Ingredient additive = null;
        int additiveCount = 0;
        if (json.has("additives"))
        {
            JsonArray additiveArr = GsonHelper.getAsJsonArray(json, "additives");
            if (additiveArr.size() > 1 && !disabled)
            {
                // TODO: add support for more than 1 additive
                throw new JsonSyntaxException("More than 1 additive is currently not supported");
            }

            JsonObject firstAdditive = additiveArr.get(0).getAsJsonObject();
            additive = Ingredient.fromJson(firstAdditive.get("ingredient"));
            additiveCount = GsonHelper.getAsInt(firstAdditive, "count");
            if (additiveCount <= 0)
            {
                throw new JsonSyntaxException("Value of 'additive_count' must be greater than 0");
            }
        }

        JsonObject resultObj = GsonHelper.getAsJsonObject(json, "result");
        ItemStack result = CraftingHelper.getItemStack(resultObj, false);
        IBlockType resultType = findResultType(result);

        return new FramingSawRecipe(recipeId, material, additive, additiveCount, result, resultType, disabled);
    }

    @Override
    public FramingSawRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
    {
        int material = buffer.readInt();

        Ingredient additive = null;
        int additiveCount = 0;
        if (buffer.readBoolean())
        {
            additive = Ingredient.fromNetwork(buffer);
            additiveCount = buffer.readInt();
        }

        ItemStack result = buffer.readItem();
        IBlockType resultType = findResultType(result);
        boolean disabled = buffer.readBoolean();

        return new FramingSawRecipe(recipeId, material, additive, additiveCount, result, resultType, disabled);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, FramingSawRecipe recipe)
    {
        buffer.writeInt(recipe.getMaterialAmount());

        Ingredient additive = recipe.getAdditive();
        if (additive != null)
        {
            buffer.writeBoolean(true);
            additive.toNetwork(buffer);
            buffer.writeInt(recipe.getAdditiveCount());
        }
        else
        {
            buffer.writeBoolean(false);
        }

        buffer.writeItem(recipe.getResultItem());
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
